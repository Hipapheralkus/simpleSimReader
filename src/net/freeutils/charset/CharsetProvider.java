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

import java.nio.charset.Charset;
import java.util.*;

/**
 * The <b>CharsetProvider</b> class is a Charset Provider implementation.
 *
 * @author  Amichai Rothman
 * @since   2005-06-10
 */
public class CharsetProvider extends java.nio.charset.spi.CharsetProvider {

    static Map name2charset;
    static Collection charsets;

    /**
     * Retrieves a charset for the given charset name. </p>
     *
     * @param  charsetName
     *         The name of the requested charset; may be either
     *         a canonical name or an alias
     *
     * @return  A charset object for the named charset,
     *          or <tt>null</tt> if the named charset
     *          is not supported by this provider
     */
    public Charset charsetForName(String charsetName) {
        if (name2charset == null)
            init();

        // get charset instance for given name (case insensitive)
        Charset c = (Charset)name2charset.get(charsetName.toLowerCase());
        try {
            if (c != null)
                c = (Charset)c.getClass().newInstance();
        } catch (Exception e) {
            // if we can't get an instance, we don't.
            c = null;
        }
        return c;
    }

    /**
     * Creates an iterator that iterates over the charsets supported by this
     * provider.  This method is used in the implementation of the {@link
     * java.nio.charset.Charset#availableCharsets Charset.availableCharsets}
     * method. </p>
     *
     * @return  The new iterator
     */
    public Iterator charsets() {
        if (charsets == null)
            init();

        return charsets.iterator();
    }

    /**
     * Initializes this charset provider's data.
     */
    void init() {
        // prepare supported charsets
        Charset[] cs = new Charset[] {
            new UTF7Charset(), new UTF7OptionalCharset(),
            new SCGSMCharset(), new CCGSMCharset(),
            new SCPackedGSMCharset(), new CCPackedGSMCharset(),
            new HPRoman8Charset(), new KOI8UCharset(),
            new ISO88598Charset(), new ISO88596Charset()};

        // initialize charset collection
        charsets = Collections.unmodifiableCollection(Arrays.asList(cs));

        // initialize name to charset map
        Map n2c = new HashMap();
        for (int i = 0; i < cs.length; i++) {
            Charset c = cs[i];
            n2c.put(c.name().toLowerCase(),c);
            for (Iterator a = c.aliases().iterator(); a.hasNext(); )
                n2c.put(((String)a.next()).toLowerCase(),c);
        }
        name2charset = n2c;
    }

}
