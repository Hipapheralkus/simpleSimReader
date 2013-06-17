package cz.muni.fi.uco359952.simplesimreader.data;

import cz.muni.fi.uco359952.simplesimreader.Converter;

/**
 * Holds all information about SMS and has methods to print them in human
 * readable interpretation.
 *
 * @author Andrej Simko
 */
public class SMS {

    private String originalHex = "";
    private int type = -1;
    private String shortMessageServiceCenter = "";
    private String dialingNumberOfSender = "";
    private String controlInformation = "";
    private String protocolTag = "";
    private String dataCoding = "";
    private String SMSCtimeStamp = "";
    private String message = "";
    private boolean messageIsEmpty = true;
    private String offset = "";
    private int index = -1;
    private int numberOfCharacters = -1;
    private byte[] messageHEX = new byte[140];
    int sizeOfMessageInBytesWithoutFFs = 0;

    /**
     * Constructor for SMS. Creates SMS from input byte array record that has
     * been obtained with "READ RECORD" APDU command.
     *
     * @param record result of "READ RECORD" APDU command after reading one
     * record in EF_SMS
     */
    public SMS(byte[] record) {
        int i = 0;

        for (int j = 0; j < record.length; j++) {
            originalHex = originalHex.concat(Converter.byteToHex(record[j]));
        }

        if (originalHex.substring(2).matches("F{" + ((record.length - 1) * 2) + "}")) {
            return;
        } else {
            messageIsEmpty = false;
        }
        type = record[0];

        int telefoneLength = record[1];
        if (Converter.byteToHex(record[2]).equals("91")) {
            shortMessageServiceCenter = shortMessageServiceCenter.concat("00");
        }
        for (int j = 3; j < (2 + telefoneLength); j++) {
            String b = Converter.byteToHex(record[j]);
            String c = Converter.swapString(b);
            shortMessageServiceCenter = shortMessageServiceCenter.concat(c);
            i = j;
        }
        if (shortMessageServiceCenter.contains("F")) {
            shortMessageServiceCenter = shortMessageServiceCenter.replace("F", "");
        }

        controlInformation = Converter.byteToHex(record[i + 1]);
        telefoneLength = record[i + 2];
        i = i + 3;
        if (Converter.byteToHex(record[i]).equals("91")) {
            dialingNumberOfSender = dialingNumberOfSender.concat("00");
        }
        i++;
        for (int j = i; j < (i + Math.round((double) telefoneLength / 2.0)); j++) {
            String b = Converter.byteToHex(record[j]);
            String c = Converter.swapString(b);
            dialingNumberOfSender = dialingNumberOfSender.concat(c);
        }
        if (dialingNumberOfSender.contains("F")) {
            dialingNumberOfSender = dialingNumberOfSender.replace("F", "");
        }

        i = i + (int) Math.round((double) telefoneLength / 2.0);
        protocolTag = protocolTag.concat(Converter.byteToHex(record[i]));
        i++;
        dataCoding = dataCoding.concat(Converter.byteToHex(record[i]));
        i++;
        for (int j = i; j < i + 7; j++) {
            String b = Converter.byteToHex(record[j]);
            String c = Converter.swapString(b);
            SMSCtimeStamp = SMSCtimeStamp.concat(c);
        }
        i = i + 7;
        if (record[i] < 0) {
            numberOfCharacters = 256 + record[i];
        } else {
            numberOfCharacters = record[i];
        }
        i++;

        System.arraycopy(record, i, messageHEX, 0, 140);
        message = message.concat(org.marre.sms.SmsPduUtil.readSeptets(messageHEX, numberOfCharacters));

        if (isMultipart()) {
            offset = message.substring(0, 7);
            message = message.substring(7);
        }

    }

    /**
     * Returns String with information about time of sending/receiving SMS.
     * Example: "Year: 12, month: 10, day: 28, hour: 09, minute: 59, second: 32,
     * time zone: 01",
     *
     * @return String with exact time of sending/receiving SMS in human readable
     * format.
     */
    public String getExactTime() {
        return "Year: " + SMSCtimeStamp.substring(0, 2)
                + ", month: " + SMSCtimeStamp.substring(2, 4)
                + ", day: " + SMSCtimeStamp.substring(4, 6)
                + ", hour: " + SMSCtimeStamp.substring(6, 8)
                + ", minute: " + SMSCtimeStamp.substring(8, 10)
                + ", second: " + SMSCtimeStamp.substring(10, 12)
                + ", time zone: " + SMSCtimeStamp.substring(12, 14);
    }

    /**
     * Getter of SMS Service Center
     *
     * @return SMS Service Center
     */
    public String getShortMessageServiceCenter() {
        return shortMessageServiceCenter;
    }

    /**
     * Interprets Type of SMS into human readable form. <p>Example:
     * "free/deleted record" from type=0
     *
     * @return Type of SMS Service in human readable form.
     */
    public String getType() {
        switch (type) {
            case (0):
                return "free/deleted record";
            case (1):
                return "message coming from the network and read";
            case (3):
                return "message coming from the network and still to be read";
            case (5):
                return "message sent to the network";
            case (7):
                return "message to be sent to the network";
            default:
                return "unknown";
        }
    }

    @Override
    public String toString() {
        if (!getMessageIsEmpty()) {
            return index + ".: " + getType() + "; YYMMDD: " + getSMSCtimeStamp().substring(0, 6) + ", HHMMSS: " + getSMSCtimeStamp().substring(6, 12) + "; from: " + getDialingNumberOfSender() + "; " + getMultipartStatus() + "; " + getMessage();
        } else {
            return index + ".: No message";
        }
    }

    /**
     * Getter of SMS Time Stamp
     *
     * @return time of sending/receiving SMS in format YYMMDDHHMMSSZZ
     */
    public String getSMSCtimeStamp() {
        return SMSCtimeStamp;
    }

    /**
     * Getter for Control Information
     *
     * @return Control Information
     */
    public String getControlInformation() {
        return controlInformation;
    }

    /**
     * Deduces if SMS is multi-part SMS.
     *
     * @return TRUE if SMS is multi-part; FALSE if SMS isn't multi-part
     */
    public boolean isMultipart() {
        if (getControlInformation().equals("44") || getControlInformation().equals("64")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns String with human readable information whether SMS is multi-part
     * SMS.
     *
     * @return "single-part" if SMS isn't multi-part; "multi-part with offset: "
     * + offset, if SMS is multi-part
     */
    public String getMultipartStatus() {
        if (isMultipart()) {
            return "multi-part with offset \"" + getOffset() + "\"";
        } else {
            return "single-part";
        }
    }

    /**
     * Getter for Offset, if the SMS is multi-part
     *
     * @return Offset
     */
    public String getOffset() {
        return offset;
    }

    /**
     * Getter for Dada Coding
     *
     * @return Data Coding
     */
    public String getDataCoding() {
        return dataCoding;
    }

    /**
     * Getter for Dialing Number of Sender
     *
     * @return Dialing Number of Sender
     */
    public String getDialingNumberOfSender() {
        return dialingNumberOfSender;
    }

    /**
     * Getter for text of SMS message itself
     *
     * @return text of SMS
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter of HEX interpretation of text of SMS message
     *
     * @return HEX interpretation of text of SMS message
     */
    public byte[] getMessageHEX() {
        return messageHEX;
    }

    /**
     * Getter of Number of Characters in message
     *
     * @return Number of Characters in message
     */
    public int getNumberOfCharacters() {
        return numberOfCharacters;
    }

    /**
     * Getter of HEX interpretation of entire SMS structure
     *
     * @return HEX interpretation of entire SMS structure
     */
    public String getOriginalHex() {
        return originalHex;
    }

    /**
     * Getter for Protocol Tag
     *
     * @return Protocol Tag
     */
    public String getProtocolTag() {
        return protocolTag;
    }

    /**
     * Getter for Size of Message in Bytes without 'FF' bytes
     *
     * @return count of bytes of real text information in message
     */
    public int getSizeOfMessageInBytesWithoutFFs() {
        return sizeOfMessageInBytesWithoutFFs;
    }

    /**
     * Deduces if text of SMS message is empty or not.
     *
     * @return TRUE if message is Empty (has only 'FF' characters); FALSE if
     * message has text
     */
    public boolean getMessageIsEmpty() {
        return messageIsEmpty;
    }

    /**
     * Getter for Index of SMS message.
     *
     * @return Index of SMS message in EF_SMS.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setter for Index of current SMS in EF_SMS structure.
     *
     * @param index Index of current SMS in EF_SMS structure.
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
