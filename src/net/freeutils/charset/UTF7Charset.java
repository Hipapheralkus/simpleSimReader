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
import java.nio.charset.*;

/**
 * The <b>UTF7Charset</b> class handles the encoding and decoding of the
 * UTF-7 charset.
 *
 * The encoding and decoding are based on RFC 2152
 * (http://www.ietf.org/rfc/rfc2152.txt)
 *
 * @author  Amichai Rothman
 * @since   2005-06-10
 */
public class UTF7Charset extends Charset {

    static final String NAME = "UTF-7";

    static final String[] ALIASES = {
        "UTF7", "UNICODE-1-1-UTF-7", "csUnicode11UTF7", "UNICODE-2-0-UTF-7" };

    // the RFC specifies that the O-set characters may
    // optionally be directly encoded. Whether they are
    // encoded directly or using a shift sequence depends
    // on the value of the optionalDirect flag.
    boolean optionalDirect;


    /**
     * Constructs an instance of the UTF7Charset.
     *
     * O-set characters are not directly encoded.
     */
    public UTF7Charset() {
        this(NAME,ALIASES,false);
    }

    /**
     * Constructs an instance of the UTF7Charset, specifying whether the
     * O-set characters are to be encoded directly or using a shift sequence.
     *
     * @param  canonicalName The canonical name of this charset
     * @param  aliases An array of this charset's aliases, or null if it has no aliases
     * @param  optionalDirect if true, O-set characters are encoded directly,
     *                       otherwise they are encoded using a shift sequence.
     * @throws IllegalCharsetNameException
     *         If the canonical name or any of the aliases are illegal
     */
    public UTF7Charset(String canonicalName, String[] aliases, boolean optionalDirect) {
        super(canonicalName, aliases);
        this.optionalDirect = optionalDirect;
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
     * Tells whether or not this charset supports encoding.
     *
     * @return  <tt>true</tt> if, and only if, this charset supports encoding
     */
    public boolean canEncode() {
        return true;
    }

    /**
     * Constructs a new decoder for this charset.
     *
     * @return  A new decoder for this charset
     */
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    /**
     * Constructs a new encoder for this charset.
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
     * The <b>Encoder</b> inner class handles the encoding of the UTF7 charset.
     */
    protected class Encoder extends CharsetEncoder {

        boolean shifted;  // flags whether we are currently in a shift sequence
        byte encodedByte; // holds the leftover bits of previously encoded char
        int requiredBits; // number of bits required to complete a 6-bit value

        /**
         * Constructs an Encoder.
         */
        protected Encoder(Charset cs) {
            super(cs,1f,5f);
            reset();
        }

        /**
         * Resets this decoder, clearing any charset-specific internal state.
         */
        protected void implReset() {
            shifted = false;
        }

        /**
         * Flushes this encoder.
         *
         * @param  out The output byte buffer
         * @return  A coder-result object, either {@link CoderResult#UNDERFLOW} or
         *          {@link CoderResult#OVERFLOW}
         */
        protected CoderResult implFlush(ByteBuffer out) {
            if (shifted) {
                if (out.remaining() < 2)
                    return CoderResult.OVERFLOW;
                if (requiredBits != 6) // dump last encoded byte, zero-bit padded
                    out.put(toBase64((byte)((encodedByte << requiredBits) & 0x3F)));
                out.put((byte)'-'); // terminate shift sequence explicitly
            }
            return CoderResult.UNDERFLOW;
        }

        /**
         * Encodes one or more characters into one or more bytes.
         *
         * @param   in the input character buffer
         * @param   out the output byte buffer
         * @return  a coder-result object describing the reason for termination
         */
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            int remaining = in.remaining();

            while (remaining-- > 0) {
                // make sure we have output space (the max we might need)
                if (out.remaining() < 4)
                    return CoderResult.OVERFLOW;

                // get next byte
                char c = in.get();

                // if not in shift sequence
                if (!shifted) {
                    // if char is in set D, write it as byte directly
                    if (isDirect(c)) {
                        out.put((byte)c);
                    } else if (c == '+') { // specially encoded char
                        out.put((byte)'+').put((byte)'-');
                    } else { // start shift sequence
                        out.put((byte)'+');
                        shifted = true;
                        requiredBits = 6;
                        writeBase64Char(out,c);
                    }
                } else { // shifted
                    if (isDirect(c)) { // direct char
                        // terminate shift sequence
                        shifted = false;
                        if (requiredBits != 6) // dump last encoded byte, zero-bit padded
                            out.put(toBase64((byte)((encodedByte << requiredBits) & 0x3F)));
                        if (isBSet(c) || c == '-') // requires explicit termination
                            out.put((byte)'-');
                        // write direct char
                        out.put((byte)c);
                    } else { // another encoded char
                        writeBase64Char(out,c);
                    }
                }
            }

            return CoderResult.UNDERFLOW;
        }

        /**
         * Writes the base64 bytes representing the given character
         * to the given output ByteBuffer. Bits left over from
         * previously written characters are writen first, followed
         * by this character's bits. Similarly, bits left over from
         * this character are saved until the next call to this method.
         *
         * @param out the ByteBuffer to which the base64 bytes are written
         * @param c the character to be written
         */
        void writeBase64Char(ByteBuffer out, char c) {
            byte b = encodedByte;

            b = (byte)(((b << requiredBits) & 0x3F) | (c >>> (16 - requiredBits)));
            out.put(toBase64(b));

            b = (byte)((c >>> (10 - requiredBits)) & 0x3F);
            out.put(toBase64(b));

            if (requiredBits != 6) {
                b = (byte)((c >>> (4 - requiredBits)) & 0x3F);
                out.put(toBase64(b));
                requiredBits += 2;
            } else {
                requiredBits = 2;
            }
            encodedByte = (byte)(c & 0x3F);
        }

    } // Encoder class


    /**
     * The <b>Decoder</b> inner class handles the decoding of the UTF7 charset.
     */
    protected class Decoder extends CharsetDecoder {

        boolean shifted; // flags whether we are currently in a shift sequence
        boolean emptyShift; // flags whether the current shift sequence is empty
        char decodedChar; // holds the bits of previous incompletely decoded char
        int requiredBits; // number of bits required to complete a 16-bit char

        /**
         * Constructs a Decoder.
         */
        protected Decoder(Charset cs) {
            super(cs,1f,1f);
            reset();
        }

        /**
         * Resets this decoder, clearing any charset-specific internal state.
         */
        protected void implReset() {
            shifted = false;
        }

        /**
         * Decodes one or more bytes into one or more characters.
         *
         * @param   in the input byte buffer
         * @param   out the output character buffer
         * @return  a coder-result object describing the reason for termination
         */
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            int val, required;
            int remaining = in.remaining();

            while (remaining-- > 0) {
                // make sure we have output space
                if (out.remaining() < 1)
                    return CoderResult.OVERFLOW;

                // get next byte
                byte b = in.get();

                // if not in shift sequence
                if (!shifted) {
                    // if byte is in set D or O, write it as char
                    if (isDorOSet(b)) {
                        out.put((char)b);
                    } else if (b == '+') { // start shift sequence
                        shifted = true;
                        emptyShift = true;
                        requiredBits = 16;
                    } else { // invalid byte
                        in.position(in.position()-1); // position input at error byte
                        return CoderResult.malformedForLength(1); // invalid byte
                    }
                } else if ((val = fromBase64(b)) != -1) { // valid base64 byte
                    // get bits from shift sequence byte
                    emptyShift = false;
                    // 6 is the max number of bits we can get from a single input byte
                    required = requiredBits > 6 ? 6 : requiredBits;

                    // add new bits to currently decoded char
                    decodedChar = (char)((decodedChar << required) | (val >> (6 - required)));
                    requiredBits -= required;

                    // check if we're done decoding a full 16-bit char
                    if (requiredBits == 0) {
                        // output it
                        out.put(decodedChar);
                        // and start off next char with remaining bits
                        requiredBits = 10 + required; // 16 - (6 - required)
                        decodedChar =(char)val; // fill in lower bits
                    }
                } else { // terminating a shift sequence
                    shifted = false;

                    // if when terminating the shift sequence we have
                    // leftover nonzero bits or more than a byte's worth
                    // of bits, the input is invalid
                    if (requiredBits <= 8 || (char)(decodedChar << requiredBits) != 0) {
                        in.position(in.position()-1); // position input at error byte
                        return CoderResult.malformedForLength(1); // invalid byte
                    }

                    // process implicit or explicit shift sequence termination
                    if (b == '-') {
                        if (emptyShift) // a "+-" sequence outputs a '+'
                            out.put('+');
                        // otherwise shift ends, and '-' is absorbed
                    } else {
                        // process regular char that ended base64 sequence
                        if (isDorOSet(b)) { // output regular char
                            out.put((char)b);
                        } else {
                            in.position(in.position()-1); // position input at error byte
                            return CoderResult.malformedForLength(1); // invalid byte
                        }
                    }
                }
            }

            return CoderResult.UNDERFLOW;
        }
    } // Decoder class


    // a lookup table for characters that are part of the D Set
    static final boolean D_SET[] = {
        false, false, false, false, false, false, false, false,
        false, true,  true,  false, false, true,  false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        true,  false, false, false, false, false, false, true,
        true,  true,  false, false, true,  true,  true,  true,
        true,  true,  true,  true,  true,  true,  true,  true,
        true,  true,  true,  false, false, false, false, true,
        false, true,  true,  true,  true,  true,  true,  true,
        true,  true,  true,  true,  true,  true,  true,  true,
        true,  true,  true,  true,  true,  true,  true,  true,
        true,  true,  true,  false, false, false, false, false,
        false, true,  true,  true,  true,  true,  true,  true,
        true,  true,  true,  true,  true,  true,  true,  true,
        true,  true,  true,  true,  true,  true,  true,  true,
        true,  true,  true,  false, false, false, false, false,
    };

    // a lookup table for characters that are part of the O Set
    static final boolean O_SET[] = {
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, true,  true,  true,  true,  true,  true,  false,
        false, false, true,  false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, true,  true,  true,  true,  false,
        true,  false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, true,  false, true,  true,  true,
        true,  false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, true,  true,  true,  false, false,
    };

    // a lookup table for characters that are part of the B Set
    static final int B_SET[] = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
        -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
    };

    // an inverse lookup table for characters that are part of the B Set
    static final int B_SET_INVERSE[] = {
        65,  66,  67,  68,  69,  70,  71,  72,
        73,  74,  75,  76,  77,  78,  79,  80,
        81,  82,  83,  84,  85,  86,  87,  88,
        89,  90,  97,  98,  99,  100, 101, 102,
        103, 104, 105, 106, 107, 108, 109, 110,
        111, 112, 113, 114, 115, 116, 117, 118,
        119, 120, 121, 122, 48,  49,  50,  51,
        52,  53,  54,  55,  56,  57,  43,  47,
    };

    static boolean isDSet(byte b) {
        return b >= 0 && D_SET[b];
    }

    static boolean isDSet(char c) {
        return c < 0x80 && D_SET[c];
    }

    static boolean isOSet(byte b) {
        return b >= 0 && O_SET[b];
    }

    static boolean isOSet(char c) {
        return c < 0x80 && O_SET[c];
    }

    static boolean isDorOSet(byte b) {
        return b >= 0 && (D_SET[b] || O_SET[b]);
    }

    static boolean isDorOSet(char c) {
        return c < 0x80 && (D_SET[c] || O_SET[c]);
    }

    static boolean isBSet(byte b) {
        return b >= 0 && B_SET[b] != -1;
    }

    static boolean isBSet(char c) {
        return c < 0x80 && B_SET[c] != -1;
    }

    static byte fromBase64(byte b) {
        return (byte)(b < 0 ? -1 : B_SET[b]);
    }

    static byte toBase64(byte b) {
        return (byte)(b < 0 || b >= 64 ? -1 : B_SET_INVERSE[b]);
    }

    boolean isDirect(char c) {
        return c < 0x80 && (D_SET[c] || (optionalDirect && O_SET[c]));
    }

}
