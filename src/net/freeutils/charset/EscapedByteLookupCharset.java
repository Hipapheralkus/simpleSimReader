/*
 *  (c) copyright 2005-2007 Amichai Rothman
 *
 *  This file is part of the Java Charset package.
 *
 *  The Java Charset package is free software; you can redistribute
 *  it and/or modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation; either version 2 of the License,
 *  or (at your option) any later version.
 *
 *  The Java Charset package is distributed in the hope that it
 *  will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.freeutils.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * The <b>EscapedByteLookupCharset</b> class handles the encoding and
 * decoding of simple charsets where the byte-to-char conversion
 * is performed using a simple lookup table, with the addition of a special
 * escape byte, such that the single byte following it is converted using
 * an alternate lookup table.
 *
 * @author  Amichai Rothman
 * @since   2007-03-26
 */
public abstract class EscapedByteLookupCharset extends Charset {

    int[] BYTE_TO_CHAR;
    int[] BYTE_TO_CHAR_ESCAPED;
    int[][] CHAR_TO_BYTE;
    int[][] CHAR_TO_BYTE_ESCAPED;
    byte ESCAPE;
    
    /**
     * Initializes a new charset with the given canonical name and alias
     * set, and byte-to-char/char-to-byte lookup tables. </p>
     *
     * @param  canonicalName The canonical name of this charset
     * @param  aliases An array of this charset's aliases, or null if it has no aliases
     * @param  escape the special escape byte value
     * @param  byteToChar a byte-to-char conversion table for this charset
     * @param  charToByte a char-to-byte conversion table for this charset. It can
     *         be generated on-the-fly by calling createInverseLookupTable(byteToChar).
     * @throws java.nio.charset.IllegalCharsetNameException
     *         If the canonical name or any of the aliases are illegal
     */
    protected EscapedByteLookupCharset(String canonicalName, String[] aliases,
        byte escape, int[] byteToChar, int[] byteToCharEscaped,
        int[][] charToByte, int[][] charToByteEscaped) {
        super(canonicalName,aliases);
        ESCAPE = escape;
        BYTE_TO_CHAR = byteToChar;
        CHAR_TO_BYTE = charToByte;
        BYTE_TO_CHAR_ESCAPED = byteToCharEscaped;
        CHAR_TO_BYTE_ESCAPED = charToByteEscaped;
    }

    /**
     * Tells whether or not this charset contains the given charset.
     *
     * <p> A charset <i>C</i> is said to <i>contain</i> a charset <i>D</i> if,
     * and only if, every character representable in <i>D</i> is also
     * representable in <i>C</i>.  If this relationship holds then it is
     * guaranteed that every string that can be encoded in <i>D</i> can also be
     * encoded in <i>C</i> without performing any replacements.
     *
     * <p> That <i>C</i> contains <i>D</i> does not imply that each character
     * representable in <i>C</i> by a particular byte sequence is represented
     * in <i>D</i> by the same byte sequence, although sometimes this is the
     * case.
     *
     * <p> Every charset contains itself.
     *
     * <p> This method computes an approximation of the containment relation:
     * If it returns <tt>true</tt> then the given charset is known to be
     * contained by this charset; if it returns <tt>false</tt>, however, then
     * it is not necessarily the case that the given charset is not contained
     * in this charset.
     *
     * @return  <tt>true</tt> if, and only if, the given charset
     *          is contained in this charset
     */
    public boolean contains(Charset cs) {
        return this.getClass().isInstance(cs);
    }

    /**
     * Constructs a new decoder for this charset. </p>
     *
     * @return  A new decoder for this charset
     */
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    /**
     * Constructs a new encoder for this charset. </p>
     *
     * @return  A new encoder for this charset
     *
     * @throws  UnsupportedOperationException
     *          If this charset does not support encoding
     */
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    /**
     * The <b>Encoder</b> inner class handles the encoding of the
     * charset using the lookup tables.
     */
    protected class Encoder extends CharsetEncoder {

        /**
         * Constructs an Encoder.
         */
        protected Encoder(Charset cs) {
            super(cs,1f,2f);
        }
        
        /**
         * Constructs an Encoder.
         */
        protected Encoder(Charset cs,
                 float averageBytesPerChar,
                 float maxBytesPerChar) {
            super(cs,averageBytesPerChar,maxBytesPerChar);
        }

        /**
         * Encodes one or more characters into one or more bytes.
         *
         * @param   in the input character buffer
         * @param   out the output byte buffer
         * @return  a coder-result object describing the reason for termination
         */
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            int b,c;
            int[] table;
            int remaining = in.remaining();

            while (remaining-- > 0) {
                if (out.remaining() < 2)
                    return CoderResult.OVERFLOW;
                c = in.get();
                table = CHAR_TO_BYTE[c >> 8];
                b =  table == null ? -1 : table[c & 0xFF];
                if (b == -1) { // maybe escapable char?
                    table = CHAR_TO_BYTE_ESCAPED[c >> 8];
                    b =  table == null ? -1 : table[c & 0xFF];
                    if (b == -1) {
                        in.position(in.position() - 1);
                        return CoderResult.unmappableForLength(1);
                    }
                    out.put(ESCAPE);
                }
                out.put((byte)(b & 0xFF));
            }
            return CoderResult.UNDERFLOW;
        }

    }

    /**
     * The <b>Decoder</b> inner class handles the decoding of the
     * charset using the inverse lookup tables.
     */
    protected class Decoder extends CharsetDecoder {

        /**
         * Constructs a Decoder.
         */
        protected Decoder(Charset cs) {
            super(cs,1f,1f);
        }
        
        /**
         * Constructs a Decoder.
         */
        protected Decoder(Charset cs,
                          float averageCharsPerByte,
                          float maxCharsPerByte) {
            super(cs,averageCharsPerByte,maxCharsPerByte);
        }

        /**
         * Decodes one or more bytes into one or more characters.
         *
         * @param   in the input byte buffer
         * @param   out the output character buffer
         * @return  a coder-result object describing the reason for termination
         */
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            int b,c;
            int remaining = in.remaining();
            while (remaining-- > 0) {
                if (out.remaining() < 1)
                    return CoderResult.OVERFLOW;
                b = in.get();
                if (b == ESCAPE) {
                    if (remaining-- == 0) {
                        in.position(in.position() - 1);
                        return CoderResult.UNDERFLOW;
                    }
                    b = in.get();
                    c = BYTE_TO_CHAR_ESCAPED[b & 0xFF];
                } else {
                    c = BYTE_TO_CHAR[b & 0xFF];
                }

                if (c == -1) {
                    in.position(in.position() - 1);
                    return CoderResult.malformedForLength(1);
                }
                out.put((char)c);
            }

            return CoderResult.UNDERFLOW;
        }
    }

}
