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
 * The <b>CCGSMCharset</b> class handles the encoding and decoding of the
 * GSM default encoding charset. In this variant, byte 0x09 is mapped
 * to the LATIN CAPITAL LETTER C WITH CEDILLA character.
 *
 * The encoding and decoding are based on the mapping at
 * http://www.unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT
 *
 * @author  Amichai Rothman
 * @since   2007-03-26
 */
public class CCGSMCharset extends GSMCharset {
    
    static final String NAME = "CCGSM";
    
    static final String[] ALIASES = { };
    
    /**
     * Constructs an instance of the CCGSMCharset.
     */
    public CCGSMCharset() {
        super(NAME,ALIASES,
              BYTE_TO_CHAR_CAPITAL_C_CEDILLA,BYTE_TO_CHAR_ESCAPED_DEFAULT,
              CHAR_TO_BYTE_CAPITAL_C_CEDILLA,CHAR_TO_BYTE_ESCAPED_DEFAULT);
    }
    
}
