package cz.muni.fi.uco359952.simplesimreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import cz.muni.fi.uco359952.simplesimreader.data.DatabaseOfEF;
import cz.muni.fi.uco359952.simplesimreader.data.ICCID;
import cz.muni.fi.uco359952.simplesimreader.data.IMSI;
import cz.muni.fi.uco359952.simplesimreader.data.LOCI;

/**
 * Writes data on System.out while using class Getters
 *
 * @author Andrej Simko
 */
public class WriteIntoSystemOut {

    /**
     * Stores variable with class Getters
     */
    public static Getters getters;

    /**
     * Constructor for WriteIntoSystemOut - creates one instance of Getters for
     * further use.
     */
    public WriteIntoSystemOut() {
        getters = new Getters();
    }

    /**
     * Writes complex information about all data that can be obtained from SIM
     * without PIN authentication, on System.out. <p>Writes ATR, ICCID, SPN,
     * PHASE, LP and number of passwords (PIN1/2 and PUK1/2) and their
     * initialization status.
     */
    public void writeDataWithoutAuthentication() {

        System.out.println("\nWITHOUT PIN:\n");

        System.out.println("ATR: " + getters.getATR());

        ICCID iccid = getters.getICCID();
        System.out.println(iccid.writeAllInfoIntoOutputStream());

        System.out.println(DatabaseOfEF.EF_SPN.getLongName() + ": " + getters.getSPNString() + "\n");
        System.out.println(DatabaseOfEF.EF_PHASE.getLongName() + ": " + getters.getPhase() + "\n");
        writeLP();
        System.out.println();
        numberOfPasswordTriesLeftAndTheirInitialization();
    }

    /**
     * Returns OutputStream with human readable information about password
     * initialization and number of their tries left.
     */
    public void numberOfPasswordTriesLeftAndTheirInitialization() {
        boolean[] arePasswordsInitialised = getters.arePasswordsInitialised();
        int[] numberOfPasswordTriesLeft = getters.numberOfPasswordTriesLeftArray();

        for (int i = 0; i < 4; i++) {
            String typeOfCurrentPassword = "";
            switch (i) {
                case (0):
                    typeOfCurrentPassword = "PIN1";
                    break;
                case (1):
                    typeOfCurrentPassword = "PUK1";
                    break;
                case (2):
                    typeOfCurrentPassword = "PIN2";
                    break;
                case (3):
                    typeOfCurrentPassword = "PUK2";
                    break;
            }

            if (arePasswordsInitialised[i] == false) {
                System.out.print(typeOfCurrentPassword + " not initialised, ");
            } else {
                System.out.print(typeOfCurrentPassword + " initialised, ");
            }

            System.out.println(numberOfPasswordTriesLeft[i] + " tries left");

        }


    }

    /**
     *
     * @param alsoShowUnknown if true, method also writes unknown services. If
     * false, it only writes known.
     * @param showOnlyAllocatedAndActivated if true, writes writes only
     * allocated and activated services. If false, writes also non-allocated and
     * non-activated services.
     * @return OutputStream with human readable information about SIM Services,
     * with information about their allocation and activation.
     */
    public OutputStream writeSST(boolean alsoShowUnknown, boolean showOnlyAllocatedAndActivated) {
        OutputStream output = new ByteArrayOutputStream();
        try {
            output.write((DatabaseOfEF.EF_SST.getLongName() + ": \n").getBytes());
            byte[] input = getters.getEFBytes(DatabaseOfEF.EF_SST);
            byte[] SST = new byte[input.length - 2];
            System.arraycopy(input, 0, SST, 0, input.length - 2);
            for (int i = 1; i <= SST.length; i++) {
                int toConvertToBinary = ((int) SST[i - 1] << 24) >>> 24;
                String binary = Integer.toBinaryString(toConvertToBinary);
                if (binary.length() < 8) {
                    String temp = "";
                    while (temp.length() + binary.length() != 8) {
                        temp = temp.concat("0");
                    }
                    binary = temp.concat(binary);
                }
                int doubles = 1;
                for (int j = 7; j >= 0; j--) {

                    if (alsoShowUnknown || !Converter.getService(i * 4 + doubles - 4).equals("unknown")) {
                        String toWrite = "";
                        toWrite = toWrite.concat(Converter.getService(i * 4 + doubles - 4) + ": ");

                        if (binary.charAt(j) == '1') {
                            toWrite = toWrite.concat("allocated");
                        } else {
                            toWrite = toWrite.concat("NOT allocated");
                        }

                        if (binary.charAt(j - 1) == '1') {
                            toWrite = toWrite.concat(" & activated");
                        } else {
                            toWrite = toWrite.concat(" & NOT activated");
                        }
                        if (showOnlyAllocatedAndActivated) {
                            if (!toWrite.contains("NOT")) {
                                output.write((toWrite + "\n").getBytes());
                            }
                        } else {
                            output.write((toWrite + "\n").getBytes());
                        }
                        doubles++;
                        j--;
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(WriteIntoSystemOut.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    /**
     * Prints information about Language Preferences in human readable form, in
     * their descending order of preference on System.out.
     */
    public void writeLP() {

        byte[] input = getters.getEFBytes(DatabaseOfEF.EF_LP);
        byte[] LP = new byte[input.length - 2];
        System.arraycopy(input, 0, LP, 0, input.length - 2);

        System.out.println("Preferred languages:");
        for (int i = 0; i < LP.length; i++) {
            System.out.println(i + ": " + Converter.getLanguage((int) LP[i]));
        }

    }

    /**
     * Writes human readable information about Last Numbers Dialed on
     * System.out.
     *
     * @param writeAlsoInHex if true, writes also data in their HEXdecimal
     * interpretation. If false, writes only human readable form.
     */
    public void WriteAllLND(boolean writeAlsoInHex) {
        System.out.println("Last numbers dialed: ");

        for (int i = 1; i <= DatabaseOfEF.EF_LND.getNumberOfEntries(); i++) {
            if (!getters.getLNDString(i, false).isEmpty()) { //print only valid data, not FF
                if (writeAlsoInHex) {
                    System.out.println(i + " in hex: " + getters.getLNDString(i, true));
                }
                System.out.println(i + " : " + getters.getLNDString(i, false));
            }
        }
    }

    /**
     * Writes human readable information about Mobile Station ISDN number
     * (telephone number of given SIM card) on System.out.
     *
     * @param writeAlsoInHex if true, writes also data in their HEXdecimal
     * interpretation. If false, writes only human readable form.
     */
    public void writeAllMSISDNData(boolean writeAlsoInHex) {
        for (int i = 1; i <= DatabaseOfEF.EF_MSISDN.getNumberOfEntries(); i++) {
            if (!getters.getMSISDNString(i, false).isEmpty()) {
                if (writeAlsoInHex) {
                    System.out.println("MSISDN n." + i + " in hex: " + getters.getMSISDNString(i, true));
                }
                System.out.println("MSISDN n." + i + " : " + getters.getMSISDNString(i, false));
            }
        }
    }

    /**
     * Writes complex information about all data that can be obtained from SIM
     * with knowledge of correct PIN, on System.out. <p>Writes IMSI, LOCI,
     * MSISDN, LND, KC, KCGPRS, HPLMN, SST, ADN and SMS in human readable form.
     */
    public void writeDataWithAuthentication() {
        System.out.println("\nWITH PIN:\n");
        IMSI imsi = getters.getIMSI();
        System.out.println(imsi.writeAllInfoIntoOutputStream() + "\n");
        LOCI loci = getters.getLOCI();
        System.out.println(loci.writeAllInfoIntoOutputStream() + "\n");
        writeAllMSISDNData(true);
        System.out.println();
        WriteAllLND(true);
        System.out.println();

        System.out.println((DatabaseOfEF.EF_KC.getLongName() + ": " + getters.getKCKeyString() + "\n"));
        System.out.println((DatabaseOfEF.EF_KCGPRS.getLongName() + ": " + getters.getKCGPRSKeyString() + "\n"));
        System.out.println((DatabaseOfEF.EF_HPLMN.getLongName() + ": " + getters.getHPLMN() + " minutes\n"));
        writeSST(true, false);
        System.out.println();

        //System.out.println(DatabaseOfEF.EF_PLMNsel.getLongName() + ": " );
        getters.getListOfTelephoneBookRecord();
        System.out.println("\nSMS: ");
        getters.getListOfSMS();
    }
}
