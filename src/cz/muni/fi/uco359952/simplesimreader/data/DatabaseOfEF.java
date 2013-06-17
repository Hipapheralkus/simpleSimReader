package cz.muni.fi.uco359952.simplesimreader.data;

/**
 * Contains database of DFs and EFs with their detailed information.
 *
 * @author Andrej Simko
 */
public class DatabaseOfEF {

    public static final String CHV1 = "CHV1";
    public static final String ADM = "ADM";
    public static final String always = "always";
    public static final String never = "never";
    public static final String transparent = "transparent";
    public static final String linearFixed = "linear fixed";
    public static final String cyclic = "cyclic";
    public static final DF DF_TELECOM = new DF("Telecom", new byte[]{(byte) 0x7F, (byte) 0x10});
    public static final DF DF_GSM = new DF("GSM", new byte[]{(byte) 0x7F, (byte) 0x20});
    public static final DF MF = new DF("MF", new byte[]{(byte) 0x3F, (byte) 0x00});
    public static final EF EF_IMSI = new EF("International mobile subscriber identity", "IMSI", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x07}, CHV1, ADM, transparent/*
             * , 9
             */);
    public static final EF EF_ADN = new EF("Abbreviated dialing numbers", "ADN", DF_TELECOM, new byte[]{(byte) 0x6F, (byte) 0x3A}, CHV1, CHV1, linearFixed/*
             * , 14
             */);
    public static final EF EF_KC = new EF("Ciphering key", "KC", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x20}, CHV1, CHV1, transparent/*
             * , 9
             */);
    public static final EF EF_SPN = new EF("Service provider name", "SPN", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x46}, always, ADM, transparent/*
             * , 17
             */);
    public static final EF EF_PHASE = new EF("Phase", "PHASE", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0xAE}, always, ADM, transparent/*
             * , 1
             */);
    public static final EF EF_LOCI = new EF("Location information", "LOCI", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x7E}, CHV1, CHV1, transparent/*
             * , 11
             */);
    public static final EF EF_ICCID = new EF("Integrated Circuit Card Identification", "ICCID", MF, new byte[]{(byte) 0x2F, (byte) 0xE2}, always, never, transparent/*
             * , 10
             */);
    public static final EF EF_MSISDN = new EF("Mobile station ISDN number", "MSISDN", DF_TELECOM, new byte[]{(byte) 0x6F, (byte) 0x40}, CHV1, CHV1, linearFixed/*
             * , 14
             */);
    public static final EF EF_LND = new EF("Last Number Dialled", "LND", DF_TELECOM, new byte[]{(byte) 0x6F, (byte) 0x44}, CHV1, CHV1, cyclic/*
             * , 14
             */);
    public static final EF EF_HPLMN = new EF("Home public land mobile network search period", "HPLMN", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x31}, CHV1, ADM, transparent/*
             * , 1
             */);
    public static final EF EF_LP = new EF("Language preferences", "LP", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x05}, always, CHV1, transparent/*
             * , 1
             */);
//    public static final EF EF_PLMNsel = new EF("Public land mobile network selector", "PLMNsel", DF_GSM, new byte[] {(byte) 0x6F, (byte) 0x30}, CHV1, CHV1, transparent, 8);
    public static final EF EF_KCGPRS = new EF("GPRS ciphering key", "KCGPRS", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x52}, CHV1, CHV1, transparent/*
             * , 9
             */);
    public static final EF EF_SST = new EF("SIM service table", "SST", DF_GSM, new byte[]{(byte) 0x6F, (byte) 0x38}, CHV1, ADM, transparent/*
             * , 2
             */);
    public static final EF EF_SMS = new EF("Short message service", "SMS", DF_TELECOM, new byte[]{(byte) 0x6F, (byte) 0x3C}, CHV1, CHV1, linearFixed/*
             * , 176
             */);
    public static final byte EF_CHV1[] = {(byte) 0x00, (byte) 0x00};
    public static final byte EF_CHV2[] = {(byte) 0x01, (byte) 0x00};
}