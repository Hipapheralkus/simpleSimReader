package cz.muni.fi.uco359952.simplesimreader;

import java.util.Arrays;
import cz.muni.fi.uco359952.simplesimreader.data.DatabaseOfEF;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import net.freeutils.charset.*;

/**
 * Contains static methods, which converts or computes something from input.
 *
 * @author Andrej Simko
 */
public class Converter {

    /**
     * Converts byte into it's HEX representation in String form. <p> For
     * example, converts "31" into "1F"
     *
     * @param data input byte that will be converted into HEX form
     * @return HEX representation of data in UpperCase
     */
    public static String byteToHex(byte data) {
        StringBuilder buf = new StringBuilder();
        buf.append(intToHexChar((data >>> 4) & 0x0F));
        buf.append(intToHexChar(data & 0x0F));
        return buf.toString().toUpperCase();
    }

    /**
     * Converts integer to HEX represented by character. <p> For example,
     * converts number 13 into 'D'
     *
     * @param input integer number that will be converted into HEX char
     * @return HEX representation of integer
     */
    public static char intToHexChar(int input) {
        if ((0 <= input) && (input <= 9)) {
            return (char) ('0' + input);
        } else {
            return (char) ('a' + (input - 10));
        }
    }

    /**
     * Converts array of bytes into it's HEX representation in String form,
     * separated by space. <p> For example, converts ["59", "18"] into "3B 12".
     *
     * @param data Input array of bytes that will be converted into HEX form.
     * @return HEX representation of data in UpperCase, separated by space.
     */
    public static String bytesToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            buf.append(byteToHex(data[i]));
            buf.append(" ");
        }
        return (buf.toString());
    }

    /**
     * Swaps characters in String that has length = 2. <p> For example, converts
     * [59, 18] into "3B 12".
     *
     * @param input Input array of bytes that will be converted into HEX form.
     * @return HEX Representation of data in UpperCase, separated by space.
     */
    public static String swapString(String input) {
        String temp = "";
        temp = temp.concat(Character.toString(input.charAt(1)));
        temp = temp.concat(Character.toString(input.charAt(0)));
        return temp;
    }
    /*
     * public static byte replaceNationalCharacters(byte character) { if
     * (character == (byte) 0xe1) { return (byte) 'á'; } if (character == (byte)
     * 0xe0) { return (byte) 'Š'; } return character;
     *
     * }
     */

    /**
     * Returns Status Word meaning in human readable String from its HEX
     * representation from response APDU. <p> For example, converts SW=9000 into
     * "Command successfully executed".
     *
     * @param data Input byte array of Status Word.
     * @return String with human readable explanation of SW.
     */
    public static String getSWmeaning(byte[] data) {
        byte SW[] = new byte[2];
        SW[0] = data[data.length - 2];
        SW[1] = data[data.length - 1];

        if (Arrays.equals(SW, new byte[]{(byte) 0x90, (byte) 0x00})) {
            return "Command successfully executed";
        }
        if (SW[0] == (byte) 0x9F) {
            return "Command successfully executed; ‘" + (int) SW[1] + "’ bytes of data are available and can be requested using GET RESPONSE";
        }
        if (Arrays.equals(SW, new byte[]{(byte) 0x67, (byte) 0x00})) {
            return "Length incorrect";
        }
        if (Arrays.equals(SW, new byte[]{(byte) 0x94, (byte) 0x02})) {
            return "Address range exceeded";
        }


        return "Unknown/unimplemented code: " + bytesToHex(SW);
    }

    /**
     * Returns language name from it's numerical representation according to GSM
     * 03.38 standard. <p> For example "Czech" from 32, or "English" from 1.
     *
     * @param number Integer value of language.
     * @return Language name String.
     */
    public static String getLanguage(int number) {
        switch (number) {
            case (0):
                return "German";
            case (1):
                return "English";
            case (2):
                return "Italian";
            case (3):
                return "French";
            case (4):
                return "Spanish";
            case (5):
                return "Dutch";
            case (6):
                return "Swedish";
            case (7):
                return "Danish";
            case (8):
                return "Portuguese";
            case (9):
                return "Finnish";
            case (10):
                return "Norwegian";
            case (11):
                return "Greek";
            case (12):
                return "Turkish";
            case (13):
                return "Hungarian";
            case (14):
                return "Polish";
            case (15):
                return "Language unspecified";
            case (32):
                return "Czech";
            case (33):
                return "Hebrew";
            case (34):
                return "Arabic";
            case (35):
                return "Russian";
            case (36):
                return "Icelandic";
            default:
                return "unknown";
        }
    }

    /**
     * Returns service name from it's numerical representation according to GSM
     * 11.11 standard. <p> For example returns "Short Message Storage (SMS)"
     * from 4. <p> See
     * http://www.3gpp.org/ftp/Specs/archive/11_series/11.11/1111-8e0.zip
     *
     * @param hexOrder Integer value of service.
     * @return Name of service from GSM 11.11 standard.
     */
    public static String getService(int hexOrder) {

        switch (hexOrder) {
            case (1):
                return "CHV1 disable function";
            case (2):
                return "Abbreviated Dialling Numbers (ADN)";
            case (3):
                return "Fixed Dialling Numbers (FDN)";
            case (4):
                return "Short Message Storage (SMS)";
            case (5):
                return "Advice of Charge (AoC)";
            case (6):
                return "Capability Configuration Parameters (CCP)";
            case (7):
                return "PLMN selector";
            case (8):
                return "RFU";
            case (9):
                return "MSISDN";
            case (10):
                return "Extension1";
            case (11):
                return "Extension2";
            case (12):
                return "SMS Parameters";
            case (13):
                return "Last Number Dialled (LND)";
            case (14):
                return "Cell Broadcast Message Identifier";
            case (15):
                return "Group Identifier Level 1";
            case (16):
                return "Group Identifier Level 2";
            case (17):
                return "Service Provider Name";
            case (18):
                return "Service dialing numbers (SDN)";
            case (19):
                return "Extension 3";
            case (20):
                return "RFU";
            case (21):
                return "VGCS Group Identifier List (EFVGCS and EFVGCSS)";
            case (22):
                return "VBS Group Identifier List (EFVBS and EFVBSS)";
            case (23):
                return "enhanced Multi Level Precedence and Pre emption Service";
            case (24):
                return "Automatic Answer for eMLPP";
            case (25):
                return "Data download via SMS CB";
            case (26):
                return "Data download via SMS PP";
            case (27):
                return "Menu selection";
            case (28):
                return "Call control";
            case (29):
                return "Proactive SIM";
            case (30):
                return "Cell Broadcast Message Identifier Ranges";
            case (31):
                return "Barred Dialling Numbers (BDN)";
            case (32):
                return "Extension4";
            case (33):
                return "De personalization Control Keys";
            case (34):
                return "Co operative Network List";
            case (35):
                return "Short Message Status Reports";
            case (36):
                return "Network's indication of alerting in the MS";
            case (37):
                return "Mobile Originated Short Message control by SIM";
            case (38):
                return "GPRS";
            case (39):
                return "Image (IMG)";
            case (40):
                return "SoLSA (Support of Local Service Area)";
            case (41):
                return "USSD string data object supported in Call Control";
            case (42):
                return "RUN AT COMMAND command";
            case (43):
                return "User controlled PLMN Selector with Access Technology";
            case (44):
                return "Operator controlled PLMN Selector with Access Technology";
            case (45):
                return "HPLMN Selector with Access Technology";
            case (46):
                return "CPBCCH Information";
            case (47):
                return "Investigation Scan";
            case (48):
                return "Extended Capability Configuration Parameters";
            case (49):
                return "MExE";
            //    case (50):
            //        return "Reserved and shall be ignored";                
            default:
                return "unknown";
        }
    }

    /**
     * Computes 2 sizes and 1 data count from APDU response: size of entire EF;
     * size of one record; number of records.
     *
     * @param response Response APDU after command GET RESPONSE in byte array.
     * @return array of 3 integers: <p>[0] == size of entire EF <p>[1] == size
     * of one record <p>[2] == number of records
     */
    public static int[] getSizes(byte[] response) {
        String sizeOfEntireEF = "";
        for (int j = 0; j <= 3; j++) {
            sizeOfEntireEF = sizeOfEntireEF.concat(Converter.byteToHex(response[j]));
        }
        int sizeOfEntireEFint = Integer.parseInt(sizeOfEntireEF, 16);
        int sizeOfOneRecord = Integer.parseInt(Converter.byteToHex(response[response.length - 3]), 16);
        int numberOfRecords = 0;
        if (sizeOfOneRecord != 0) {
            numberOfRecords = sizeOfEntireEFint / sizeOfOneRecord;
        }
        return new int[]{sizeOfEntireEFint, sizeOfOneRecord, numberOfRecords};
    }

    /**
     * Discovers and fills sizes to Elementary Files.
     */
    public static void fillEFSizes() {
        DatabaseOfEF.EF_ADN.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_ADN));
        DatabaseOfEF.EF_HPLMN.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_HPLMN));
        DatabaseOfEF.EF_ICCID.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_ICCID));
        DatabaseOfEF.EF_IMSI.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_IMSI));
        DatabaseOfEF.EF_KC.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_KC));
        DatabaseOfEF.EF_KCGPRS.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_KCGPRS));
        DatabaseOfEF.EF_LND.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_LND));
        DatabaseOfEF.EF_LOCI.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_LOCI));
        DatabaseOfEF.EF_LP.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_LP));
        DatabaseOfEF.EF_MSISDN.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_MSISDN));
        DatabaseOfEF.EF_PHASE.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_PHASE));
        DatabaseOfEF.EF_SMS.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_SMS));
        DatabaseOfEF.EF_SPN.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_SPN));
        DatabaseOfEF.EF_SST.setSizes(WriteIntoSystemOut.getters.getResponsesFromEFandSelectDF(DatabaseOfEF.EF_SST));
    }

    /**
     * From given one record of ADN (Abbreviated Dialing Numbers), or at least
     * first 14 bytes of that record, returns String in UTF-16 representation of
     * name of the contact in human readable form, no matter encoding -
     * GSM_DEFAULT or UCS2 (according GSM 11.11 standard).
     *
     * @param input byte[] with one entire ADN record, or at least with first 14
     * bytes with name of contact.
     * @return String in UTF-16 representation of name of the contact from ADN
     * in human readable form
     */
    public static String getNameFromADNEntry(byte[] input) {
        if ((input[0] & 0xff) == 0x80) {
            return UCS2toUTF16(input);
        }
        if ((input[0] & 0xff) == 0x81) {
            return UCS2toUTF16(input);
        }
        if ((input[0] & 0xff) == 0x82) {
            return UCS2toUTF16(input);
        }

        int i = 0;
        CharsetProvider gsmProvider = new CharsetProvider();
        Charset gsmCharset = gsmProvider.charsetForName("GSM-default-alphabet");
        byte[] actualName;
        while ((input[i] != (byte) 0xff) && (i < 14)) {
            i++; //finds out correct length of name in input record
        }
        actualName = new byte[i];
        System.arraycopy(input, 0, actualName, 0, i);

        return new String(actualName, gsmCharset);
    }

    /**
     * Transforms byte[] in UCS2 representation into UTF-16 String
     * representation in human readable form (following GSM 11.11 standard).
     *
     * @param input byte[] in UCS2 encoding.
     * @return String in UTF-16 encoding in human readable form.
     */
    public static String UCS2toUTF16(byte[] input) {
        if ((input[0] & 0xff) == 0x80) {
            String outputInUTF16 = "";
            int i = 1;
            while (((input[i] & 0xff) != 0xff) && ((input[i + 1] & 0xff) != 0xff)) {
                byte[] segment = {input[i], input[i + 1]};
                int intValue = Integer.parseInt(bytesToHex(segment).replace(" ", ""), 16);
                outputInUTF16 = outputInUTF16.concat(Character.toString((char) intValue));
                i = i + 2;
            }

            return outputInUTF16;
        }

        if ((input[0] & 0xff) == 0x81) {
            String outputInUTF16 = "";
            int numberOfCharacters = input[1];
            int segmentUpper = input[2] / 16;
            int segmentLower = input[2] % 16;//bity 15-8 na int previest
            segmentUpper = segmentUpper << 11;
            segmentLower = segmentLower << 7;
            for (int i = 3; i <= numberOfCharacters + 2; i++) {
                if (input[i] < 0 /*
                         * 8th bit is set to '0'
                         */) {
                    int offset = (input[i] & 0xff) - 128;
                    int codeWord = segmentUpper + segmentLower + offset;
                    outputInUTF16 = outputInUTF16.concat(Character.toString((char) codeWord));
                } else {
                    outputInUTF16 = outputInUTF16.concat(Character.toString((char) (int) input[i]));
                }
            }
            return outputInUTF16;
        }

        if ((input[0] & 0xff) == 0x82) {
            String outputInUTF16 = "";
            int numberOfCharacters = input[1];
            byte[] segment = {input[2], input[3]};
            int intValue = Integer.parseInt(bytesToHex(segment).replace(" ", ""), 16);
            for (int i = 4; i <= numberOfCharacters + 3; i++) {
                if (input[i] < 0 /*
                         * 8th bit is set to '0'
                         */) {
                    int offset = (input[i] & 0xff) - 128;
                    int codeWord = intValue + offset;
                    outputInUTF16 = outputInUTF16.concat(Character.toString((char) codeWord));
                } else {
                    outputInUTF16 = outputInUTF16.concat(Character.toString((char) (int) input[i]));
                }
            }
            return outputInUTF16;
        }

        return "";
    }
}
