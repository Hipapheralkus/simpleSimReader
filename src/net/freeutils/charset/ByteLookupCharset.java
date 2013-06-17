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
 * The <b>ByteLookupCharset</b> class handles the encoding and decoding of
 * single-byte charsets where the byte-to-char conversion is performed
 * using a simple lookup table.
 *
 * @author  Amichai Rothman
 * @since   2005-06-30
 */
abstract public class ByteLookupCharset extends Charset {

    int[] BYTE_TO_CHAR;
    int[][] CHAR_TO_BYTE;
    
    /**
     * Returns whether the running JDK version is 1.5 or higher.
     * 
     * @return true if running in JDK 1.5 or higher, false otherwise.
     */
    static boolean isJDK15() {
        float version;
        try {
            version = Float.parseFloat(System.getProperty("java.class.version"));
        } catch (Exception e) {
            version = 0;
        }
        return version >= 49.0; // 49.0 is the class version of JDK 1.5
    }
    
    /**
     * Returns a copy of the given array in which several items
     * are modified.
     * 
     * @param src the array to mutate
     * @param ind the array indices in which the values will be modified
     * @param val the respective values to place in these indices
     * @return the mutated array
     */
    protected static int[] mutate(int[] src, int[] ind, int val[]) {
        int[] mut = new int[src.length];
        System.arraycopy(src,0,mut,0,src.length);
        for (int i = 0; i < ind.length; i++)
            mut[ind[i]] = val[i];
        return mut;
    }

    /**
     * Creates an inverse lookup table for the given byte-to-char lookup table.
     *
     * The returned table contains 256 tables, one per high-order byte of a
     * potential character to be converted (unused ones are null), and each
     * such table can be indexed using the character's low-order byte, to
     * obtain the actual converted byte value.
     * A null table in the top level table, or a -1 within a lower level table,
     * both indicate that there is no legal mapping for the given character.
     *
     * @param chars a lookup table which holds the character value
     *        that each byte value (0-255) is converted to.
     * @return the created inverse lookup (char-to-byte) table.
     */
    public static int[][] createInverseLookupTable(int[] chars) {
        int[][] tables = new int[256][];
        for (int i = 0; i < 256; i++) {
            int c = chars[i];
            if (c > -1) {
                int high = (c >>> 8) & 0xFF;
                int low = c & 0xFF;
                int[] table = tables[high];
                if (table == null) {
                    table = new int[256];
                    for (int j = 0; j < table.length; j++)
                        table[j] = -1;
                    tables[high] = table;
                }
                table[low] = i;
            }
        }
        return tables;
    }

    /**
     * Returns a string containing Java definitions of the inverse lookup
     * table returned by getInverseLookupTable for the given byte-to-char
     * lookup table.
     *
     * This is a convenient utility method for design-time building
     * of charsets based on lookup table mapping, as an alternative to
     * creating these inverse lookup tables on-the-fly.
     *
     * @param chars a lookup table which holds the character value
     *        that each byte value (0-255) is converted to.
     * @return the Java definitions of the created inverse lookup
     *         (char-to-byte) table.
     */
    public static String createInverseLookupTableDefinition(int[] chars) {
        int[][] tables = createInverseLookupTable(chars);
        StringBuffer sb = new StringBuffer();
        int nulls = 0;
        sb.append("static final int[][] CHAR_TO_BYTE = {\n\t");
        for (int i = 0; i < tables.length; i++) {
            int[] table = tables[i];
            if (table == null) {
                if ((nulls++) %8 == 0 && nulls > 1)
                    sb.append("\n\t");
                sb.append("null, ");
            } else {
                if (nulls > 0)
                    sb.append("\n\t");
                nulls = 0;
                sb.append("{ // high byte = 0x");
                if (i < 0x10)
                    sb.append('0');
                sb.append(Integer.toHexString(i));
                sb.append("\n\t");
                for (int j = 0; j < table.length; j++) {
                    if (table[j] == -1) {
                        sb.append("  -1, ");
                    } else {
                        sb.append("0x");
                        if (table[j] < 0x10)
                            sb.append('0');
                        sb.append(Integer.toHexString(table[j])).append(", ");
                    }
                    if ((j+1) % 8 == 0)
                        sb.append("\n\t");
                }
                sb.append("}, \n\t");
            }
        }
        sb.append("\n\t};");
        return sb.toString();
    }

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
    protected ByteLookupCharset(String canonicalName, String[] aliases,
        int[] byteToChar, int[][] charToByte) {
        super(canonicalName,aliases);
        BYTE_TO_CHAR = byteToChar;
        CHAR_TO_BYTE = charToByte;
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
     * charset using the lookup table.
     */
    protected class Encoder extends CharsetEncoder {

        /**
         * Constructs an Encoder.
         */
        protected Encoder(Charset cs) {
            super(cs,1f,1f);
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
            int[][] lookup = CHAR_TO_BYTE; // getfield bytecode optimization
            int[] table;
            int remaining = in.remaining();
            while (remaining-- > 0) {
                if (out.remaining() < 1)
                    return CoderResult.OVERFLOW; // we need exactly one byte per char
                c = in.get();
                table = lookup[c >>> 8];
                b =  table == null ? -1 : table[c & 0xFF];
                if (b == -1) {
                    in.position(in.position() - 1);
                    return CoderResult.unmappableForLength(1);
                }
                out.put((byte)(b & 0xFF));
            }
            return CoderResult.UNDERFLOW;
        }

    }

    /**
     * The <b>Decoder</b> inner class handles the decoding of the
     * charset using the inverse lookup table.
     */
    protected class Decoder extends CharsetDecoder {

        /**
         * Constructs a Decoder.
         */
        protected Decoder(Charset cs) {
            super(cs,1f,1f);
        }

        /**
         * Decodes one or more bytes into one or more characters.
         *
         * @param   in the input byte buffer
         * @param   out the output character buffer
         * @return  a coder-result object describing the reason for termination
         */
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            int c;
            int[] lookup = BYTE_TO_CHAR; // getfield bytecode optimization
            int remaining = in.remaining();
            while (remaining-- > 0) {
                if (out.remaining() < 1)
                    return CoderResult.OVERFLOW; // we need exactly one char per byte
                c = lookup[in.get() & 0xFF];
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
