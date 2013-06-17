package cz.muni.fi.uco359952.simplesimreader.data;

import cz.muni.fi.uco359952.simplesimreader.Converter;

/**
 * Holds all information about Contact in ADN (Abbreviated Dialing Numbers =
 * telephone book) and has methods to print them in human readable
 * interpretation.
 *
 * @author Andrej Simko
 */
public class Contact {

    private String name = "";
    private String phoneNumber = "";
    private int index = -1;

    /**
     * Constructor for Contact. Creates Contact from input byte array record
     * that has been obtained with "READ RECORD" APDU command.
     *
     * @param record result of "READ RECORD" APDU command after reading one
     * record in EF_ADN
     */
    public Contact(byte[] record) {

        name = name.concat(Converter.getNameFromADNEntry(record));

        int telefoneLength = record[14];
        if (record[15] == (byte) 0x91) {
            phoneNumber = phoneNumber.concat("00");
        }
        for (int j = 16; j < (15 + telefoneLength); j++) {

            String b = Converter.byteToHex(record[j]);
            String c = Converter.swapString(b);
            phoneNumber = phoneNumber.concat(c);
        }

        if (phoneNumber.contains("F")) {
            phoneNumber = phoneNumber.replace("F", "");
        }
        if (phoneNumber.contains("A")) {
            phoneNumber = phoneNumber.replace("A", "*");
        }
        if (phoneNumber.contains("B")) {
            phoneNumber = phoneNumber.replace("B", "#");
        }

    }

    /**
     * Getter for phone number of contact.
     *
     * @return phone number of contact.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return index + ".: " + name + ", " + phoneNumber;
    }

    /**
     * Getter for Index of ADN entry.
     *
     * @return Index of ADN entry in EF_ADN.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setter for Index of current ADN record in EF_ADN structure.
     *
     * @param index Index of current ADN record in EF_ADN structure.
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
