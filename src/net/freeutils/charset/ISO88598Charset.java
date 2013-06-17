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

/**
 * The <b>ISO88598Charset</b> class handles the encoding and decoding of the
 * ISO 8859-8 charset. Although the JRE includes an implementation of this
 * charset, it does not recognize two aliases for this charset:
 * ISO-8859-8-i and ISO-8859-8-e. These signify whether bidirectionality
 * is implicit or explicit. However, this is the displayer's responsibility
 * in any case, and the character conversion is the same.
 * See RFC 1556.
 *
 * @author  Amichai Rothman
 * @since   2005-06-30
 */
public class ISO88598Charset extends ByteLookupCharset {

    static final String NAME = "ISO-8859-8-BIDI";

    static final String[] ALIASES = {
        "csISO88598I", "ISO-8859-8-I", "ISO_8859-8-I",
        "csISO88598E", "ISO-8859-8-E", "ISO_8859-8-E" };
    
    /**
     * Constructs an instance of the ISO88598Charset.
     */
    public ISO88598Charset() {
        super(NAME,ALIASES,BYTE_TO_CHAR,CHAR_TO_BYTE);
    }

    static final int[] BYTE_TO_CHAR = {
        0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007,
        0x0008, 0x0009, 0x000a, 0x000b, 0x000c, 0x000d, 0x000e, 0x000f,
        0x0010, 0x0011, 0x0012, 0x0013, 0x0014, 0x0015, 0x0016, 0x0017,
        0x0018, 0x0019, 0x001a, 0x001b, 0x001c, 0x001d, 0x001e, 0x001f,
        0x0020, 0x0021, 0x0022, 0x0023, 0x0024, 0x0025, 0x0026, 0x0027,
        0x0028, 0x0029, 0x002a, 0x002b, 0x002c, 0x002d, 0x002e, 0x002f,
        0x0030, 0x0031, 0x0032, 0x0033, 0x0034, 0x0035, 0x0036, 0x0037,
        0x0038, 0x0039, 0x003a, 0x003b, 0x003c, 0x003d, 0x003e, 0x003f,
        0x0040, 0x0041, 0x0042, 0x0043, 0x0044, 0x0045, 0x0046, 0x0047,
        0x0048, 0x0049, 0x004a, 0x004b, 0x004c, 0x004d, 0x004e, 0x004f,
        0x0050, 0x0051, 0x0052, 0x0053, 0x0054, 0x0055, 0x0056, 0x0057,
        0x0058, 0x0059, 0x005a, 0x005b, 0x005c, 0x005d, 0x005e, 0x005f,
        0x0060, 0x0061, 0x0062, 0x0063, 0x0064, 0x0065, 0x0066, 0x0067,
        0x0068, 0x0069, 0x006a, 0x006b, 0x006c, 0x006d, 0x006e, 0x006f,
        0x0070, 0x0071, 0x0072, 0x0073, 0x0074, 0x0075, 0x0076, 0x0077,
        0x0078, 0x0079, 0x007a, 0x007b, 0x007c, 0x007d, 0x007e, 0x007f,
        0x0080, 0x0081, 0x0082, 0x0083, 0x0084, 0x0085, 0x0086, 0x0087,
        0x0088, 0x0089, 0x008a, 0x008b, 0x008c, 0x008d, 0x008e, 0x008f,
        0x0090, 0x0091, 0x0092, 0x0093, 0x0094, 0x0095, 0x0096, 0x0097,
        0x0098, 0x0099, 0x009a, 0x009b, 0x009c, 0x009d, 0x009e, 0x009f,
        0x00a0,     -1, 0x00a2, 0x00a3, 0x00a4, 0x00a5, 0x00a6, 0x00a7,
        0x00a8, 0x00a9, 0x00d7, 0x00ab, 0x00ac, 0x00ad, 0x00ae, 0x203e, // 0x00af or 0x203e?
        0x00b0, 0x00b1, 0x00b2, 0x00b3, 0x00b4, 0x00b5, 0x00b6, 0x00b7,
        0x00b8, 0x00b9, 0x00f7, 0x00bb, 0x00bc, 0x00bd, 0x00be,     -1,
            -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
            -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
            -1,     -1,     -1,     -1,     -1,     -1,     -1,     -1,
            -1,     -1,     -1,     -1,     -1,     -1,     -1, 0x2017,
        0x05d0, 0x05d1, 0x05d2, 0x05d3, 0x05d4, 0x05d5, 0x05d6, 0x05d7,
        0x05d8, 0x05d9, 0x05da, 0x05db, 0x05dc, 0x05dd, 0x05de, 0x05df,
        0x05e0, 0x05e1, 0x05e2, 0x05e3, 0x05e4, 0x05e5, 0x05e6, 0x05e7,
        0x05e8, 0x05e9, 0x05ea,     -1,     -1, 0x200e, 0x200f,     -1, // 0x200e/0x200f added to spec
    };
    
    static {
        // update the mapping for the MACRON character
        // (see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4760496).
        // apply the fix only if we're running in JDK 1.5 or higher,
        // so that we remain consistent with the JDK ISO-8859-8 charset
        // implementation.
        if (isJDK15())
            BYTE_TO_CHAR[0xaf] = 0x00af;    
    }

    static final int[][] CHAR_TO_BYTE = createInverseLookupTable(BYTE_TO_CHAR);

}
