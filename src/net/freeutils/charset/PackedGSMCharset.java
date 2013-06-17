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
 * The <b>PackedGSMCharset</b> class handles the encoding and decoding of the
 * GSM default encoding charset, with packing as per GSM 03.38 spec.
 *
 * The encoding and decoding are based on the mapping at
 * http://www.unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT
 *
 * @author  Amichai Rothman
 * @since   2007-03-20
 */
public class PackedGSMCharset extends GSMCharset {
    
    static final int BUFFER_SIZE = 256;

    /**
     * Initializes a new charset with the given canonical name and alias
     * set, and byte-to-char/char-to-byte lookup tables. </p>
     *
     * @param  canonicalName The canonical name of this charset
     * @param  aliases An array of this charset's aliases, or null if it has no aliases
     * @param  byteToChar a byte-to-char conversion table for this charset
     * @param  charToByte a char-to-byte conversion table for this charset. It can
     *         be generated on-the-fly by calling createInverseLookupTable(byteToChar).
     * @throws java.nio.charset.IllegalCharsetNameException
     *         If the canonical name or any of the aliases are illegal
     */
    protected PackedGSMCharset(String canonicalName, String[] aliases,
            int[] byteToChar, int[] byteToCharEscaped,
            int[][] charToByte, int[][] charToByteEscaped) {
        super(canonicalName,aliases,
              byteToChar,byteToCharEscaped,charToByte,charToByteEscaped);
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
     * Packed GSM default encoding charset.
     */
    protected class Encoder extends GSMCharset.Encoder {
        
        int bitpos;
        byte current;
        ByteBuffer buf;

        /**
         * Constructs an Encoder.
         */
        protected Encoder(Charset cs) {
            super(cs,7/8f,2f);
            buf = ByteBuffer.allocate(BUFFER_SIZE);
            implReset();
        }
        
        /**
         * Resets this encoder, clearing any charset-specific internal state.
         */
        protected void implReset() {
            bitpos = 0;
            current = 0;
            buf.limit(0);
        }
        
        /**
         * Flushes this encoder.
         *
         * @param  out
         *         The output byte buffer
         *
         * @return  A coder-result object, either {@link CoderResult#UNDERFLOW} or
         *          {@link CoderResult#OVERFLOW}
         */
        protected CoderResult implFlush(ByteBuffer out) {
            // flush buffer
            CoderResult result = pack(buf,out);
            // flush last (current) partial byte if it exists
            if (bitpos != 0) {
                if (!out.hasRemaining())
                    return CoderResult.OVERFLOW;
                out.put(current); // write final leftover byte
            }
            return result;
        }

        /**
         * Encodes one or more characters into one or more bytes.
         *
         * @param   in the input character buffer
         * @param   out the output byte buffer
         * @return  a coder-result object describing the reason for termination
         */
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            CoderResult result;
            while (true) {
                // output buffered data
                if (buf.hasRemaining()) {
                    result = pack(buf,out);
                    if (result == CoderResult.OVERFLOW)
                        return result;
                }
                // process new data into buffer
                buf.clear();
                result = super.encodeLoop(in, buf);
                buf.flip();
                // stop if out of input or error
                if (!buf.hasRemaining() || result.isError())
                    return result;
            }
        }
        
        /**
         * Packs the given data into full bytes.
         * 
         * @param in the input byte buffer
         * @param out the output byte buffer
         * @return A coder-result object, either {@link CoderResult#UNDERFLOW} or
         *          {@link CoderResult#OVERFLOW}
         */
        protected CoderResult pack(ByteBuffer in, ByteBuffer out) {
            int remaining = in.remaining();
            while (remaining-- > 0) {
                if (!out.hasRemaining())
                    return CoderResult.OVERFLOW;
                byte b = (byte)(in.get() & 0x7F); // remove top bit
                // assign first half of partial bits
                current |= (byte) ((b & 0xFF) << bitpos);
                // assign second half of partial bits (if exist)
                if (bitpos >= 2) {
                    out.put(current);
                    current = 0;
                    current |= (b >> (8 - bitpos));
                }
                bitpos = (bitpos + 7) % 8;
                if (bitpos == 0) {
                    out.put(current);
                    current = 0;
                }
            }
            return CoderResult.UNDERFLOW;
        }

    }

    /**
     * The <b>Decoder</b> inner class handles the decoding of the
     * Packed GSM default encoding charset.
     */
    protected class Decoder extends GSMCharset.Decoder {
        
        int bitpos;
        byte current;
        int unpackedCount;
        ByteBuffer buf;

        /**
         * Constructs a Decoder.
         */
        protected Decoder(Charset cs) {
            super(cs,8/7f,2f);
            buf = ByteBuffer.allocate(BUFFER_SIZE);
            implReset();
        }
        
        /**
         * Resets this decoder, clearing any charset-specific internal state.
         */
        protected void implReset() {
            bitpos = 0;
            current = 0;
            unpackedCount = 0;
            buf.limit(0);
        }
        
        /**
         * Flushes this decoder.
         *
         * @param  out
         *         The output character buffer
         *
         * @return  A coder-result object, either {@link CoderResult#UNDERFLOW} or
         *          {@link CoderResult#OVERFLOW}
         */
        protected CoderResult implFlush(CharBuffer out) {
            // this fixes an ambiguity bug in the specs
            // where the last of 8 packed bytes is 0
            // and it's impossible to distinguish whether it is a
            // trailing '@' character (which is mapped to 0)
            // or extra zero-bit padding for 7 actual data bytes.
            //
            // we opt for the latter, since it's far more likely,
            // at the cost of losing a trailing '@' character
            // in strings whose unpacked size modulo 8 is 0,
            // and whose last character is '@'.
            //
            // an application that wishes to handle this rare case
            // properly must disambiguate this case externally, such
            // as by obtaining the original string length, and
            // appending the trailing '@' if the length
            // shows that there is one character missing.
            if (unpackedCount % 8 == 0) {
                int pos = out.position();
                if (pos > 0 && out.get(pos-1) == '@')
                    out.position(pos-1);
            }
            return CoderResult.UNDERFLOW;
        }

        /**
         * Decodes one or more bytes into one or more characters.
         *
         * @param   in the input byte buffer
         * @param   out the output character buffer
         * @return  a coder-result object describing the reason for termination
         */
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            CoderResult result;
            while (true) {
                // output buffered data
                if (buf.hasRemaining()) {
                    result = super.decodeLoop(buf,out);
                    // stop if out of output space or error
                    if (buf.hasRemaining() || result.isError())
                        return result;
                }
                // process new data into buffer
                buf.clear();
                result = unpack(in, buf);
                buf.flip(); 
                if (!buf.hasRemaining())
                    return result;
                unpackedCount += buf.remaining();
            }
        }
        
        /**
         * Unpacks the given data into original bytes.
         * 
         * @param in the input byte buffer
         * @param out the output byte buffer
         * @return A coder-result object, either {@link CoderResult#UNDERFLOW} or
         *          {@link CoderResult#OVERFLOW}
         */
        protected CoderResult unpack(ByteBuffer in, ByteBuffer out) {
            byte b;
            int remaining = out.remaining();
            while (remaining-- > 0) {
                if (!in.hasRemaining() && bitpos != 1)
                    return CoderResult.UNDERFLOW;
                if (bitpos == 0)
                    current = in.get();
                // remove top bit and assign first half of partial bits 
                b = (byte)(((current & 0xFF) >> bitpos) & 0x7F);
                // remove top bit and assign second half of partial bits (if exist)
                if (bitpos >= 2) {
                    current = in.get();
                    b |= (byte)((current << (8 - bitpos)) & 0x7F);
                }
                bitpos = (bitpos + 7) % 8;
                out.put(b);
            }
            return CoderResult.OVERFLOW;
        }
    }


    /**
     * Unpacks the given data into original bytes.
     * 
     * This is an external utility method and is not used
     * internally by the Charset implementation.
     * 
     * @param in the input bytes
     * @return the unpacked output bytes
     */
    static public byte[] unpack(byte[] in) {
        byte[] out = new byte[(in.length * 8) / 7];
        int len = out.length;
        int current = 0;
        int bitpos = 0;
        for (int i = 0; i < len; i++) {
            // remove top bit and assign first half of partial bits 
            out[i] = (byte)(((in[current] & 0xFF) >> bitpos) & 0x7F);
            // remove top bit and assign second half of partial bits (if exist)
            if (bitpos >= 2)
                out[i] |= (byte)((in[++current] << (8 - bitpos)) & 0x7F);
            bitpos = (bitpos + 7) % 8;
            if (bitpos == 0)
                current++;
        }
        // this fixes an ambiguity bug in the specs
        // where the last of 8 packed bytes is 0
        // and it's impossible to distinguish whether it is a
        // trailing '@' character (which is mapped to 0)
        // or extra zero-bit padding for 7 actual data bytes.
        //
        // we opt for the latter, since it's far more likely,
        // at the cost of losing a trailing '@' character
        // in strings whose unpacked size modulo 8 is 0,
        // and whose last character is '@'.
        //
        // an application that wishes to handle this rare case
        // properly must disambiguate this case externally, such
        // as by obtaining the original string length, and
        // appending the trailing '@' if the length
        // shows that there is one character missing.
        if (len % 8 == 0 && len > 0 && out[len-1] == 0) {
            byte[] fixed = new byte[len-1];
            System.arraycopy(out, 0, fixed, 0, len-1);
            out = fixed;
        }
        return out;
    }
    
    /**
     * Packs the given data into full bytes.
     * 
     * This is an external utility method and is not used
     * internally by the Charset implementation.
     * 
     * @param in the input bytes
     * @return the packed output bytes
     */
    static public byte[] pack(byte[] in) {
        byte[] out = new byte[(int)Math.ceil((in.length * 7) / 8f)];
        int len = in.length;
        int current = 0;
        int bitpos = 0;
        for (int i = 0; i < len; i++) {
            byte b = (byte)(in[i] & 0x7F); // remove top bit
            // assign first half of partial bits
            out[current] |= (byte) ((b & 0xFF) << bitpos);
            // assign second half of partial bits (if exist)
            if (bitpos >= 2)
                out[++current] |= (b >> (8 - bitpos));
            bitpos = (bitpos + 7) % 8;
            if (bitpos == 0)
                current++;
        }
        return out;
    }

}
