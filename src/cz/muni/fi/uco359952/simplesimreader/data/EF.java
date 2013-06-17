package cz.muni.fi.uco359952.simplesimreader.data;

import cz.muni.fi.uco359952.simplesimreader.Converter;

/**
 * Contains information about Elementary File - superior Dedicated File, long
 * name, short name, File ID (FID), read access rights, write access rights,
 * structure, size of entire Elementary File, size of one entry and size of all
 * entries.
 *
 * @author Andrej Simko
 */
public class EF {

    private DF DF = null;
    private byte[] FID = null;
    private String longName = "";
    private String shortName = "";
    private String readAccess = "";
    private String writeAccess = "";
    private String structure = "";
    private int sizeOfEntireEF = -1;
    private int sizeOfOneEntry = -1;
    private int numberOfEntries = -1;

    /**
     *
     * @param longName long name of Elementary File (for example "International
     * mobile subscriber identity")
     * @param shortName short name of Elementary File (for example "IMSI")
     * @param DF superior Dedicated File that Elementary File belongs to
     * @param FID File ID of Elementary File
     * @param readAccess access rights to read Elementary File
     * @param writeAccess access rights to write Elementary File
     * @param structure structure of Elementary File - transparent, linear fixed
     * or cyclic
     */
    public EF(String longName, String shortName, DF DF, byte[] FID, String readAccess, String writeAccess, String structure) {
        this.longName = longName;
        this.shortName = shortName;
        this.DF = DF;
        this.FID = FID;
        this.readAccess = readAccess;
        this.writeAccess = writeAccess;
        this.structure = structure;

    }

    /**
     * Getter of superior Dedicated File that Elementary File belongs to.
     *
     * @return superior Dedicated File
     */
    public DF getDF() {
        return DF;
    }

    /**
     * Setter of sizes of entire Elementary File; one entry and number of
     * entries.
     *
     * @param sizes byte[], where byte[0] is size of entire Elementary File;
     * byte[1] is size of one entry and byte[2] is number of entries
     */
    public void setSizes(byte[] sizes) {
        sizeOfEntireEF = Converter.getSizes(sizes)[0];
        sizeOfOneEntry = Converter.getSizes(sizes)[1];
        numberOfEntries = Converter.getSizes(sizes)[2];
    }

    /**
     * Getter of File ID of Elementary File.
     *
     * @return File ID of Elementary File
     */
    public byte[] getFID() {
        return FID;
    }

    /**
     * Getter of size of entire Elementary File.
     *
     * @return size of entire Elementary File
     */
    public int getSizeOfEntireEF() {
        return sizeOfEntireEF;
    }

    /**
     * Getter of size of one entry in Elementary File.
     *
     * @return size of one entry in Elementary File
     */
    public int getSizeOfOneEntry() {
        return sizeOfOneEntry;
    }

    /**
     * Getter of number of entries in Elementary File.
     *
     * @return number of entries in Elementary File
     */
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    /**
     * Getter of long name (for example "International mobile subscriber
     * identity") of Elementary File
     *
     * @return long name of Elementary File
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Getter of access right to read in Elementary File.
     *
     * @return access right to read in Elementary File
     */
    public String getReadAccess() {
        return readAccess;
    }

    /**
     * Getter of short name (for example "IMSI") of Elementary File.
     *
     * @return short name of Elementary File.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Getter of structure of Elementary File.
     *
     * @return structure of Elementary File
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Getter of access right to write in Elementary File.
     *
     * @return access right to write in Elementary File
     */
    public String getWriteAccess() {
        return writeAccess;
    }
}
