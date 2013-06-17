package cz.muni.fi.uco359952.simplesimreader.data;

/**
 * Contains information about Dedicated File - name and File ID (FID).
 *
 * @author Andrej Simko
 */
public class DF {

    private String name = "";
    private byte[] FID = null;

    /**
     *
     * @param name name of Dedicated File
     * @param FID File ID of Dedicated File
     */
    public DF(String name, byte[] FID) {
        this.name = name;
        this.FID = FID;
    }

    /**
     * Getter of Dedicated File name
     *
     * @return Dedicated File name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter of File ID of Dedicated File
     *
     * @return File ID of Dedicated File
     */
    public byte[] getFID() {
        return FID;
    }
}
