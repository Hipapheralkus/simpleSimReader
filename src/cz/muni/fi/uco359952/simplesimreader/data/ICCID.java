package cz.muni.fi.uco359952.simplesimreader.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds all information about ICCID number and has methods to print them in
 * human readable interpretation.
 *
 * @author Andrej Simko
 */
public class ICCID {

    private String ICCID = "";
    private String majorIndustryIdentifier = "";
    private String mobile_country_code = "";
    private String mobile_network_code = "";
    private String check_digit = "";
    private String issuerIdentificationNumber = "";
    private String individualAccountIdentificationNumber = "";

    /**
     * Constructor of ICCID number from String.
     *
     * @param input String with ICCID to parse.
     */
    public ICCID(String input) {
        ICCID = input;
        majorIndustryIdentifier = input.substring(0, 2);
        mobile_country_code = input.substring(2, 5);
        mobile_network_code = input.substring(5, 7);
        issuerIdentificationNumber = issuerIdentificationNumber.concat(majorIndustryIdentifier).concat(mobile_country_code).concat(mobile_network_code);
        individualAccountIdentificationNumber = input.substring(7, 18);
        check_digit = input.substring(18, 19);
    }

    /**
     * Getter of ICCID.
     *
     * @return ICCID
     */
    public String getICCID() {
        return ICCID;
    }

    /**
     * Getter of Major Industry Identifier (MII).
     *
     * @return Major Industry Identifier
     */
    public String getMajorIndustryIdentifier() {
        return majorIndustryIdentifier;
    }

    /**
     * Getter of Mobile Country Code (MCC).
     *
     * @return Mobile Country Code
     */
    public String getMobileCountryCode() {
        return mobile_country_code;
    }

    /**
     * Getter of Mobile Network Code (MNC)
     *
     * @return Mobile Network Code
     */
    public String getMobileNetworkCode() {
        return mobile_network_code;
    }

    /**
     * Getter of Issuer Identification Number, which consists of MII + MCC + MNC
     *
     * @return Issuer Identification Number
     */
    public String getIssuerIdentificationNumber() {
        return issuerIdentificationNumber;
    }

    /**
     * Getter of Check digit computed with Luhn algorithm
     *
     * @return Check digit
     */
    public String getCheckDigit() {
        return check_digit;
    }

    /**
     * Getter of Individual Account Identification Number
     *
     * @return Individual Account Identification Number
     */
    public String getIndividualAccountIdentificationNumber() {
        return individualAccountIdentificationNumber;
    }

    @Override
    public String toString() {
        return getICCID();
    }

    /**
     * Prints all avaliable data about ICCID into OutputStream
     *
     * @return OutputStream with all avaliable data about ICCID
     */
    public OutputStream writeAllInfoIntoOutputStream() {
        OutputStream output = new ByteArrayOutputStream();
        try {
            output.write(("Integrated Circuit Card ID (ICCID): " + getICCID() + "\n").getBytes());
            if (getMajorIndustryIdentifier().equals("89")) {
                output.write(("Major Industry Identifier (MII): " + getMajorIndustryIdentifier() + " (Telecommunications Industry)\n").getBytes());
            } else {
                output.write(("Major Industry Identifier (MII): " + getMajorIndustryIdentifier() + "\n").getBytes());
            }
            output.write(("Mobile Country code (MCC): " + getMobileCountryCode() + "\n").getBytes());
            output.write(("Mobile Network Code (MNC): " + getMobileNetworkCode() + "\n").getBytes());
            output.write(("Issuer Identification Number (IIN): " + getIssuerIdentificationNumber() + "\n").getBytes());
            output.write(("Individual Account Identification Number: " + getIndividualAccountIdentificationNumber() + "\n").getBytes());
            output.write(("Check digit: " + getCheckDigit() + "\n").getBytes());

        } catch (IOException ex) {
            Logger.getLogger(ICCID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
}
