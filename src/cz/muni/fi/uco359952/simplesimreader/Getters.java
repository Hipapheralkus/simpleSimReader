package cz.muni.fi.uco359952.simplesimreader;

import cz.muni.fi.uco359952.simplesimreader.data.DatabaseOfEF;
import cz.muni.fi.uco359952.simplesimreader.data.SMS;
import cz.muni.fi.uco359952.simplesimreader.data.EF;
import cz.muni.fi.uco359952.simplesimreader.data.ICCID;
import cz.muni.fi.uco359952.simplesimreader.data.Contact;
import cz.muni.fi.uco359952.simplesimreader.data.LOCI;
import cz.muni.fi.uco359952.simplesimreader.data.IMSI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ensures data in human readable, or HEX representation. Uses ApduWorker class,
 * which is on logically lower level and uses WriteIntoSystemOut.
 *
 * @author Andrej Simko
 */
public class Getters {

    /**
     * Stores ApduWorker instance for further use.
     */
    public static ApduWorker worker;

    /**
     * Creates ApduWorker and stores is for further use.
     */
    public Getters() {
        worker = new ApduWorker();
    }

    /**
     * Returns Phase in human readable interpretation.
     *
     * @return Phase in human readable interpretation.
     */
    public String getPhase() {
        byte[] input = getEFBytes(DatabaseOfEF.EF_PHASE);//,1

        if (input[0] == (byte) 0x00) {
            return "Phase 1";
        }
        if (input[0] == (byte) 0x02) {
            return "Phase 2";
        }
        if (input[0] == (byte) 0x03) {
            return "Phase 2+ (with profile download required)";
        }
        return "Reserved for specification by ETSI TC SMG";
    }

    /**
     * Returns Ciphering Key in HEX.
     *
     * @return Ciphering Key in HEX.
     */
    public String getKCKeyString() {
        byte[] input = getEFBytes(DatabaseOfEF.EF_KC);
        byte[] KC_KEY = new byte[9];

        System.arraycopy(input, 0, KC_KEY, 0, 9);

        return Converter.bytesToHex(KC_KEY);
    }

    /**
     * Returns GPRS Ciphering Key in HEX.
     *
     * @return GPRS Ciphering Key in HEX.
     */
    public String getKCGPRSKeyString() {
        byte[] input = getEFBytes(DatabaseOfEF.EF_KCGPRS);
        byte[] KC_KEY = new byte[9];

        System.arraycopy(input, 0, KC_KEY, 0, 9);

        return Converter.bytesToHex(KC_KEY);
    }

    /**
     * Returns Service Provider Name in human readable interpretation.
     *
     * @return Service Provider Name in human readable interpretation.
     */
    public String getSPNString() {
        byte[] input = getEFBytes(DatabaseOfEF.EF_SPN);
        byte[] SPN = new byte[input.length - 3];
        String SPNString = "";
        System.arraycopy(input, 1, SPN, 0, input.length - 3);
        int i = 0;
        while ((SPN[i] != (byte) 0xff) && (i < SPN.length)) {
            SPNString = SPNString.concat(Character.toString((char) (int) SPN[i]));
            i++;
        }

        return SPNString;
    }

    /**
     * Returns International Mobile Subscriber Identity (IMSI) as IMSI class.
     *
     * @return International Mobile Subscriber Identity (IMSI) as IMSI class.
     */
    public IMSI getIMSI() {
        byte[] input = getEFBytes(DatabaseOfEF.EF_IMSI);
        String IMSIString = "";

        for (int i = 0; i < input.length - 2; i++) {
            String b = Converter.byteToHex(input[i]);
            String c = Converter.swapString(b);
            IMSIString = IMSIString.concat(c);
        }

        return new IMSI(IMSIString);
    }

    /**
     * Returns Last Numbers Dialed in human readable interpretation.
     *
     * @param position Entry position of LND that is to be read.
     * @param showAllHex if true, returns HEX representation of data. If false,
     * returns normal representation of data.
     * @return Last Numbers Dialed in human readable interpretation.
     */
    public String getLNDString(int position, boolean showAllHex) {
        byte[] LND = null;
        String LNDString = "";
        String contactName = "";
        try {
            worker.getResponse(worker.select(DatabaseOfEF.DF_TELECOM.getFID()));
            LND = worker.readRecord(position, worker.getResponse(worker.select(DatabaseOfEF.EF_LND.getFID())));

            contactName = contactName.concat(Converter.bytesToHex(LND).substring(0, 3 * (LND.length - 14)));
            if (!contactName.matches("[FF ]+")) {
                for (int i = 0; i < LND.length - 14; i++) {
                    LNDString = LNDString.concat(Character.toString((char) (int) LND[i]));
                }
                LNDString = LNDString.concat("; ");
            }
            if (LND[LND.length - 14 + 1] == (byte) 0x91) {
                LNDString = LNDString.concat("00");
            }
            for (int i = LND.length - 14 + 2; i <= LND.length - 14 + LND[LND.length - 14]; i++) {
                String b = Converter.byteToHex(LND[i]);
                String c = Converter.swapString(b);
                LNDString = LNDString.concat(c);
            }
        } catch (Exception ex) {
            Logger.getLogger(CardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (showAllHex) {
            return Converter.bytesToHex(LND);
        } else {
            return LNDString;
        }
    }

    /**
     * Returns Mobile Station ISDN Number (telephone number of given SIM card)
     *
     * @param position Entry position of MSISDN that is to be read.
     * @param showAllHex if true, returns HEX representation of data. If false,
     * returns normal representation of data.
     * @return Mobile Station ISDN Number (telephone number of given SIM card)
     * in human readable interpretation.
     */
    public String getMSISDNString(int position, boolean showAllHex) {
        byte[] MSISDN = null;
        String MSISDNString = "";
        try {
            worker.getResponse(worker.select(DatabaseOfEF.DF_TELECOM.getFID()));
            MSISDN = worker.readRecord(position, worker.getResponse(worker.select(DatabaseOfEF.EF_MSISDN.getFID())));
            if (MSISDN[14] != (byte) 0xff) {
                for (int i = 16; i <= 14 + MSISDN[14]; i++) {
                    String b = Converter.byteToHex(MSISDN[i]);
                    String c = Converter.swapString(b);
                    MSISDNString = MSISDNString.concat(c);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (showAllHex) {
            return Converter.bytesToHex(MSISDN);
        } else {
            return MSISDNString;
        }
    }

    /**
     *
     * Returns Location Information as LOCI class.
     *
     * @return Location Information as LOCI class.
     */
    public LOCI getLOCI() {
        byte[] input = getEFBytes(DatabaseOfEF.EF_LOCI);//,11
        String loci = "";

        for (int i = 0; i < input.length - 2; i++) {
            String b = Converter.byteToHex(input[i]);
            if ((i > 3) && i <= 8) { //swap only LAI
                String c = Converter.swapString(b);
                loci = loci.concat(c);
            } else {
                loci = loci.concat(b);
            }
        }

        return new LOCI(loci);
    }

    /**
     *
     * Returns Integrated Circuit Card Identification as ICCID class.
     *
     * @return Returns Integrated Circuit Card Identification as ICCID class.
     */
    public ICCID getICCID() {
        byte[] input = getEFBytes(DatabaseOfEF.EF_ICCID);
        String ICCIDString = "";
        for (int i = 0; i < input.length - 2; i++) {
            String b = Converter.byteToHex(input[i]);
            String c = Converter.swapString(b);
            ICCIDString = ICCIDString.concat(c);
        }

        if (ICCIDString.toUpperCase().contains("F")) {
            ICCIDString = ICCIDString.substring(0, ICCIDString.length() - 1);
        }
        return new ICCID(ICCIDString);
    }

    /**
     * Returns List of all Contacts in telephone book (ADN = Abbreviated Dialing
     * Numbers) and prints them to System.out in human readable form.
     *
     * @return List of all Contacts in telephone book (ADN = Abbreviated Dialing
     * Numbers) in human readable form.
     */
    public List<Contact> getListOfTelephoneBookRecord() {
        System.out.println("Telephone book: ");
        List listOfContacts = new ArrayList();
        try {
            byte[] resp = getResponsesFromEFandSelectDF(DatabaseOfEF.EF_ADN);
            for (int i = 1; i <= Converter.getSizes(resp)[2]; i++) {
                Contact contact = new Contact(worker.readRecord(i, worker.getResponse(worker.select(DatabaseOfEF.EF_ADN.getFID()))));
                contact.setIndex(i);
                if (!contact.getPhoneNumber().equals("")) {
                    System.out.println(contact);
                    listOfContacts.add(contact); //add only relevant data to container
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listOfContacts;
    }

    /**
     * Returns List of all SMS and prints them to System.out in human readable
     * form.
     *
     * @return List of all SMS and prints them to System.out in human readable
     * form.
     */
    public List<SMS> getListOfSMS() {

        List listOfSMS = new ArrayList();
        byte[] resp = getResponsesFromEFandSelectDF(DatabaseOfEF.EF_SMS);

        for (int i = 1; i <= Converter.getSizes(resp)[2]; i++) {
            SMS sms;
            try {
                sms = new SMS(worker.readRecord(i, worker.getResponse(worker.select(DatabaseOfEF.EF_SMS.getFID()))));
                sms.setIndex(i);
                System.out.println(sms);
                listOfSMS.add(sms);
            } catch (Exception ex) {
                Logger.getLogger(CardManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listOfSMS;
    }

    /**
     * Returns Home Public Land Mobile Network search period in minutes.
     *
     * @return Home Public Land Mobile Network search period in minutes.
     */
    public String getHPLMN() {
        return Converter.byteToHex(getEFBytes(DatabaseOfEF.EF_HPLMN)[0]);
    }

    /**
     * Determines password tries left.
     *
     * @return array of integers with number of password tries left - array[0] =
     * PIN1; array[1] = PUK1; array[2] = PIN2; array[3] = PUK2.
     */
    public int[] numberOfPasswordTriesLeftArray() {
        int[] numberOfPasswordTriesLeft = new int[4];
        byte[] response = null;

        try {
            response = worker.getResponse(worker.select(DatabaseOfEF.MF.getFID()));

            for (int i = 0; i < 4; i++) {
                String binaryForm;
                int j = response[18 + i]; //position of PIN1/PUK1/PIN2/PUK2
                binaryForm = Integer.toBinaryString(j).substring(24);
                numberOfPasswordTriesLeft[i] = Integer.parseInt(binaryForm.substring(4), 2);
            }
        } catch (Exception ex) {
            Logger.getLogger(CardManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return numberOfPasswordTriesLeft;
    }

    /**
     * Determines if passwords are initialized. If password is initialized,
     * returns true.
     *
     * @return array of booleans - array[0] = PIN1; array[1] = PUK1; array[2] =
     * PIN2; array[3] = PUK2. If password is initialized, it has value true.
     */
    public boolean[] arePasswordsInitialised() {
        boolean[] arePasswordsInitialised = new boolean[4];
        byte[] response = null;

        try {
            response = worker.getResponse(worker.select(DatabaseOfEF.MF.getFID()));

            for (int i = 0; i < 4; i++) {
                String binaryForm;
                int j = response[18 + i]; //position of PIN1/PUK1/PIN2/PUK2
                binaryForm = Integer.toBinaryString(j).substring(24);
                if (binaryForm.charAt(0) == '0') {
                    arePasswordsInitialised[i] = false;
                } else {
                    arePasswordsInitialised[i] = true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CardManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return arePasswordsInitialised;
    }

    /**
     * Returns Answer To Reset in HEX.
     *
     * @return Answer To Reset in HEX.
     */
    public String getATR() {
        byte[] ATR = new byte[CardManager.m_ATR.length - 2];
        System.arraycopy(CardManager.m_ATR, 0, ATR, 0, CardManager.m_ATR.length - 2);

        return Converter.bytesToHex(ATR);
    }

    /**
     * Returns Response APDU from READ BINARY command after selecting given EF
     * and getting it's response.
     *
     * @param EF to be read using READ BINARY command.
     * @return Response APDU from READ BINARY command.
     */
    public byte[] getEFBytes(EF EF) {
        byte[] response = null;
        try {
            response = worker.readBinary(getResponsesFromEFandSelectDF(EF));
            return response;
        } catch (Exception ex) {
            Logger.getLogger(cz.muni.fi.uco359952.simplesimreader.CardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    /**
     * Selects DF to given EF and returns Response APDU after selecting given EF
     * and getting it's GET RESPONSE command.
     *
     * @param EF to be read using READ BINARY command.
     * @return Response APDU of given EF using GET RESPONSE command.
     */
    public byte[] getResponsesFromEFandSelectDF(EF EF) {
        byte[] response = null;
        try {
            worker.select(EF.getDF().getFID());
            response = worker.getResponse(worker.select(EF.getFID()));
            return response;
        } catch (Exception ex) {
            Logger.getLogger(cz.muni.fi.uco359952.simplesimreader.CardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
}
