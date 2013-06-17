package cz.muni.fi.uco359952.simplesimreader;

import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import cz.muni.fi.uco359952.simplesimreader.CardManager.*;
import cz.muni.fi.uco359952.simplesimreader.exceptions.BadStatusWordException;

/**
 * Creates APDUs that are send into CardManager. Supports named-APDU functions,
 * for example select(). All functions return byte[] that is used and
 * interpreted in Getters class.
 *
 * @author Andrej Simko
 */
public class ApduWorker {

    /**
     * Template for SELECT command APDU, defined in ISO 7816-4.
     */
    public static final byte SELECT[] = {(byte) 0xA0, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02};
    /**
     * Template for GET RESPONSE command APDU, defined in ISO 7816-4.
     */
    public static final byte GET_RESPONSE[] = {(byte) 0xA0, (byte) 0xC0, (byte) 0x00, (byte) 0x00};
    /**
     * Template for READ BINARY command APDU, defined in ISO 7816-4.
     */
    public static final byte READ_BINARY[] = {(byte) 0xA0, (byte) 0xB0, (byte) 0x00, (byte) 0x00};
    /**
     * Template for READ RECORD command APDU, defined in ISO 7816-4.
     */
    public static final byte READ_RECORD[] = {(byte) 0xA0, (byte) 0xB2, (byte) 0x01, (byte) 0x04, (byte) 0x1E};
    /**
     * Stores CardManager instance for further use.
     */
    public static CardManager manager;

    /**
     * Creates CardManager instance and stores is for further use.
     */
    public ApduWorker() {
        manager = new CardManager();
    }

    /**
     *
     * @param FID File ID of DF/EF that is to be selected
     * @return Response APDU after SELECT command
     * @throws CardException if there was problem communicating with card
     * @throws BadStatusWordException if card has returned Status Word
     * indicating process aborted.
     */
    public byte[] select(byte[] FID) throws CardException, BadStatusWordException {
        byte APDU[] = new byte[7];
        System.arraycopy(SELECT, 0, APDU, 0, SELECT.length);
        System.arraycopy(FID, 0, APDU, 5, FID.length);
        ResponseAPDU response = manager.sendAPDU(APDU);

        if (response.getSW1() != 0x9f) {
            throw new BadStatusWordException(Converter.getSWmeaning(response.getBytes())); //command was not successfully executed
        }
        return response.getBytes();
    }

    /**
     *
     * @param FID File ID of DF/EF that is to be selected
     * @return Response APDU after SELECT command
     * @throws CardException if there was problem communicating with card
     * @throws BadStatusWordException if card has returned Status Word
     * indicating process aborted.
     */
    public byte[] select(int FID) throws CardException, BadStatusWordException {
        byte FIDbyte[] = {(byte) Converter.intToHexChar(FID)};
        return select(FIDbyte);
    }

    /**
     *
     * @param rApduFromSelect byte[] with Response APDU after SELECT command
     * @return Response APDU after GET RESPONSE command
     * @throws CardException if there was problem communicating with card
     */
    public byte[] getResponse(byte[] rApduFromSelect) throws CardException {
        byte APDU[] = new byte[5];
        System.arraycopy(GET_RESPONSE, 0, APDU, 0, GET_RESPONSE.length);
        APDU[4] = rApduFromSelect[1];
        ResponseAPDU response = manager.sendAPDU(APDU);
        Converter.getSWmeaning(response.getBytes());

        return response.getBytes();
    }

    /**
     *
     * @param rApduFromGetResponse byte[] with Response APDU after GET REPONSE
     * command
     * @return Response APDU after READ BINARY command
     * @throws CardException if there was problem communicating with card
     */
    public byte[] readBinary(byte[] rApduFromGetResponse) throws CardException {
        byte APDU[] = new byte[5];
        System.arraycopy(READ_BINARY, 0, APDU, 0, READ_BINARY.length);
        APDU[4] = rApduFromGetResponse[3];
        ResponseAPDU response = manager.sendAPDU(APDU);
        Converter.getSWmeaning(response.getBytes());
        return response.getBytes();
    }

    /**
     *
     * @param numberOfRecord sequential number of record that is to be read
     * @param rApduFromGetResponse byte[] with Response APDU after GET REPONSE
     * command
     * @return Response APDU after READ RECORD command
     * @throws CardException if there was problem communicating with card
     * @throws BadStatusWordException if card has returned Status Word
     * indicating process aborted.
     */
    public byte[] readRecord(int numberOfRecord, byte[] rApduFromGetResponse) throws CardException, BadStatusWordException {
        if (rApduFromGetResponse.length == 0) {
            throw new NullPointerException("input can't be null");
        }
        byte APDU[] = new byte[5];
        System.arraycopy(READ_RECORD, 0, APDU, 0, READ_RECORD.length);
        APDU[2] = (byte) numberOfRecord;
        APDU[3] = (byte) 0x04;//0x04 == absolute mode; 0x02 == next;
        APDU[4] = rApduFromGetResponse[rApduFromGetResponse.length - 3];
        ResponseAPDU response = manager.sendAPDU(APDU);

        if (response.getSW1() != 0x90) {
            throw new BadStatusWordException(Converter.getSWmeaning(response.getBytes()));
        }

        return response.getData();
    }
}
