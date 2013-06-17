package cz.muni.fi.uco359952.simplesimreader.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores parsed information about LOCI, contains also methods to print them in
 * human readable form.
 *
 * @author Andrej Simko
 *
 */
public class LOCI {

    private String LOCI = "";
    private String TMSI = "";
    private String LAI = "";
    private String TMSI_TIME = "";
    private String locationUpdateStatus = "";
    private String mobileCountryCode = ""; //MCC
    private String mobileNetworkCode = ""; //MNC
    private String locationAreaCode = ""; //LAC

    /**
     * Constructor that creates LOCI from String
     *
     * @param input Input String.
     */
    public LOCI(String input) {
        LOCI = input;
        TMSI = LOCI.substring(0, 8);
        LAI = LOCI.substring(8, 18);
        TMSI_TIME = LOCI.substring(18, 20);
        locationUpdateStatus = LOCI.substring(20, 22);
        if (LAI.charAt(3) == 'F') {
            mobileNetworkCode = LAI.substring(4, 6);
        } else {
            mobileNetworkCode = LAI.substring(3, 6);
        }
        mobileCountryCode = LAI.substring(0, 3);
        locationAreaCode = LAI.substring(6, 10);
    }

    /**
     * Getter of entire unparsed LOCI number.
     *
     * @return entire unparsed LOCI number.
     */
    public String getLOCI() {
        return LOCI;
    }

    /**
     * Getter of Local Area Information (LAI).
     *
     * @return Local Area Information (LAI)
     */
    public String getLAI() {
        return LAI;
    }

    /**
     * Getter of Temporary Mobile Subscriber Identity (TMSI)
     *
     * @return Temporary Mobile Subscriber Identity (TMSI)
     */
    public String getTMSI() {
        return TMSI;
    }

    /**
     * Getter of TMSI Time
     *
     * @return TMSI Time
     */
    public String getTMSI_TIME() {
        return TMSI_TIME;
    }

    /**
     * Getter of Location Update Status
     *
     * @return Location Update Status
     */
    public String getLocationUpdateStatus() {
        return locationUpdateStatus;
    }

    /**
     * Returns Location Update Status in human readable form. <p> Example:
     * returns "Updated" from Location Update Status == 00.
     *
     * @return Location Update Status in human readable form.
     */
    public String getLocationUpdateStatusString() {
        switch (locationUpdateStatus) {
            case "00":
                return "Updated";
            case "01":
                return "Not updated";
            case "02":
                return "Forbidden PLMN";
            case "03":
                return "Forbidden location area";

            default:
                return "unknown";
        }
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
     * Getter of Mobile Network Code (MNC)
     *
     * @return Mobile Network Code (MNC)
     */
    public String getMobileNetworkCode() {
        return mobileNetworkCode;
    }

    /**
     * Getter of Location Area Code
     *
     * @return Location Area Code
     */
    public String getLocationAreaCode() {
        return locationAreaCode;
    }

    /**
     * Prints all avaliable data about LOCI into OutputStream.
     *
     * @return OutputStream with all avaliable data about LOCI
     */
    public OutputStream writeAllInfoIntoOutputStream() {
        OutputStream output = new ByteArrayOutputStream();
        try {
            output.write(("Location information (LOCI): " + getLOCI() + "\n").getBytes());
            output.write(("Temporary mobile subscriber identity (TMSI): " + getTMSI() + "\n").getBytes());
            output.write(("Location area information (LAI): " + getLAI() + "\n").getBytes());
            output.write(("Mobile Country Code (MCC): " + getMobileCountryCode() + "\n").getBytes());
            output.write(("Mobile Network Code (MNC): " + getMobileNetworkCode() + "\n").getBytes());
            output.write(("Location Area Code (LOC): " + getLocationAreaCode() + " (hex) = " + Integer.parseInt(getLocationAreaCode(), 16) + " (int)\n").getBytes());

            if (getTMSI_TIME().equals("FF")) {
                output.write(("TMSI TIME: " + getTMSI_TIME() + " (not used)\n").getBytes());
            } else {
                output.write(("TMSI TIME: " + getTMSI_TIME() + "\n").getBytes());
            }
            output.write(("Location update status: " + getLocationUpdateStatusString() + "\n").getBytes());


        } catch (IOException ex) {
            Logger.getLogger(LOCI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
}
