package cz.muni.fi.uco359952.simplesimreader.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds all information about IMSI number and has methods to print them in
 * human readable interpretation.
 *
 * @author Andrej Simko
 */
public class IMSI {

    private String IMSI = "";
    private String mobileCountryCode = "";
    private String mobileNetworkCode = "";
    private String mobileSubscriptionIdentificationNumber = "";

    /**
     * Constructor that creates IMSI from String
     *
     * @param input input IMSI string
     */
    public IMSI(String input) {
        IMSI = input.substring(3, input.length());
        mobileCountryCode = IMSI.substring(0, 3);
        mobileNetworkCode = IMSI.substring(3, 5);
        mobileSubscriptionIdentificationNumber = IMSI.substring(5, IMSI.length());
    }

    /**
     * Getter of IMSI code
     *
     * @return IMSI code
     */
    public String getIMSI() {
        return IMSI;
    }

    /**
     * Getter of Mobile Country Code (MCC)
     *
     * @return Mobile Country Code (MCC)
     */
    public String getMobileCountryCode() {
        return mobileCountryCode;
    }

    /**
     * Getter of Mobile Subscription Identification Number (MSIN)
     *
     * @return Mobile Subscription Identification Number (MSIN)
     */
    public String getMobileSubscriptionIdentificationNumber() {
        return mobileSubscriptionIdentificationNumber;
    }

    /**
     * Getter of Mobile Network Code (MNC)
     *
     * @return Mobile Network Code (MNC)
     */
    public String getMobileNetworkCode() {
        return mobileNetworkCode;
    }

    /**
     * Returns name of the Country according MCC <p> Example: returns "Czech
     * republic" when MCC == 230
     *
     * @return name of the Country according MCC
     */
    public String getCountry() {
        switch (mobileCountryCode) {
            case ("231"):
                return "Slovakia";
            case ("230"):
                return "Czech republic";
            default:
                return "unknown";
        }
    }

    /**
     * Returns name of the country operator according MCC and MNC <p> Example:
     * if((MCC == 230) && (MNC == 02)) returns "Telefónica O2 Czech Republic,
     * a.s.". <p>See https://www.numberingplans.com/?page=plans&sub=imsinr
     *
     * @return name of the country operator according MCC and MNC
     *
     */
    public String getOperator() { //
        switch (mobileCountryCode) {
            case ("231"): { //Slovakia
                switch (mobileNetworkCode) {
                    case ("01"):
                        return "Orange Slovensko a.s.";
                    case ("02"):
                        return "T-Mobile Slovensko, a.s.";
                    case ("04"):
                        return "T-Mobile Slovensko, a.s.";
                    case ("05"):
                        return "Orange Slovensko a.s.";
                    case ("06"):
                        return "Telefónica O2 Slovakia, s.r.o.";
                    default:
                        return "unknown";
                }
            }
            case ("230"): { //Czech republic
                switch (mobileNetworkCode) {
                    case ("01"):
                        return "T-Mobile Czech Republic a.s.";
                    case ("02"):
                        return "Telefónica O2 Czech Republic, a.s.";
                    case ("03"):
                        return "Vodafone Czech Republic a.s.";
                    case ("99"):
                        return "R&D Centre - Český Mobil a.s.";
                    default:
                        return "unknown";
                }

            }
            default:
                return "unknown";

        }
    }

    /**
     * Prints all avaliable data about IMSI into OutputStream.
     *
     * @return OutputStream with all avaliable data about IMSI
     */
    public OutputStream writeAllInfoIntoOutputStream() {
        OutputStream output = new ByteArrayOutputStream();
        try {
            output.write(("International Mobile Subscriber Identity (IMSI): " + getIMSI() + "\n").getBytes());
            output.write(("Mobile Country Code (MCC): " + getMobileCountryCode() + "\n").getBytes());
            output.write(("Country: " + getCountry() + "\n").getBytes());
            output.write(("Mobile Network Code (MNC): " + getMobileNetworkCode() + "\n").getBytes());
            output.write(("Network operator: " + getOperator() + "\n").getBytes());
            output.write(("Mobile Subscription Identification Number (MSIN): " + getMobileSubscriptionIdentificationNumber() + "\n").getBytes());
        } catch (IOException ex) {
            Logger.getLogger(IMSI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
}
