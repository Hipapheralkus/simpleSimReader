package cz.muni.fi.uco359952.simplesimreader;

import cz.muni.fi.uco359952.simplesimreader.exceptions.WrongPINException;
import cz.muni.fi.uco359952.simplesimreader.exceptions.NoReaderFoundException;
import cz.muni.fi.uco359952.simplesimreader.exceptions.WrongPINCharactersException;
import cz.muni.fi.uco359952.simplesimreader.exceptions.WrongLengthOfPINException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.smartcardio.*;

/**
 * Maintains connection do terminal and card itself, verifies PIN and sends APDU
 * commands created by class ApduWorker.
 *
 * @author Andrej Simko
 */
public class CardManager {

    private static CardTerminal m_terminal = null;
    private static CardChannel m_channel = null;
    private static Card m_card = null;
    /**
     * Stores Answer to Response from card.
     */
    public static byte m_ATR[] = null;

    /**
     * Connects to card in first terminal that is found and sends reset to card.
     *
     * @return true if connection to card was successful, false if there was
     * problem
     * @throws NoReaderFoundException if no PC/SC reader was found
     * @throws CardException if there was error that occurred during
     * communication with the Smart Card stack or the card itself
     * @throws CardNotPresentException if no card was found in first terminal
     */
    public boolean ConnectToCard() throws NoReaderFoundException, CardException, CardNotPresentException {
        List terminalList = null;
        boolean terminalFound = false;
        try {
            terminalList = GetReaderList();
            terminalFound = true;

        } catch (NoReaderFoundException e) {
            throw e;
        }

        //List numbers of Card readers
        boolean cardFound = false;
        for (int i = 0; i < terminalList.size(); i++) {
            //    System.out.println("Terminal n." + i + " : " + terminalList.get(i));
            m_terminal = (CardTerminal) terminalList.get(i);
            if (!m_terminal.isCardPresent()) {
                throw new CardNotPresentException("No card present");
            } else {
                m_card = m_terminal.connect("*");
                m_channel = m_card.getBasicChannel();
                m_ATR = m_card.getATR().getBytes(); //resets the card
                if (m_card != null) {
                    cardFound = true;
                }
                if (m_card == null) {
                    throw new CardNotPresentException("No card present");
                }
            }
        }

        return cardFound;
    }

    /**
     * Transmits APDU commands to card and gets Response APDU from card's
     * response.
     *
     * @param apdu Command APDU created in ApduWoker class
     * @return ResponseAPDU as response to transmitted Command APDU
     * @throws CardException if there was error in communicating with card.
     */
    public ResponseAPDU sendAPDU(byte apdu[]) throws CardException {
        CommandAPDU commandAPDU = new CommandAPDU(apdu);
        ResponseAPDU responseAPDU = m_channel.transmit(commandAPDU);
        return (responseAPDU);
    }

    /**
     * Disconnects session from card.
     *
     * @throws CardException if there was error that occurred during
     * communication with the Smart Card stack or the card itself
     */
    public void DisconnectFromCard() throws CardException {
        if (m_card != null) {
            m_card.disconnect(false);
            m_card = null;
        }
    }

    /**
     * Returns List of all avaliable PC/SC readers connected to computer if
     * there are any, throws NoReaderFoundException if there are none.
     *
     * @return List of all avaliable PC/SC readers connected to computer.
     * @throws NoReaderFoundException if no device is found to be connected to
     * computer.
     */
    public List GetReaderList() throws NoReaderFoundException {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List readersList = factory.terminals().list();
            if (readersList.isEmpty()) {
                throw new NoReaderFoundException();
            }
            return readersList;
        } catch (CardException | NoReaderFoundException ex) {
            throw new NoReaderFoundException();
        }
    }

    /**
     * Verifies PIN from String
     *
     * @param pin String of PIN characters
     * @return true if PIN verification was successful, false otherwise
     * @throws WrongLengthOfPINException if length of PIN characters isn't 4-8
     * characters long
     * @throws WrongPINCharactersException if at least 1 character is not
     * supported - is different than numerical character
     * @throws CardException if there was error that occurred during
     * communication with the Smart Card stack or the card itself
     * @throws WrongPINException if PIN was wrong and counter of PIN tries left
     * has been decremented
     * @throws UnsupportedEncodingException if encoding isn't supported
     */
    public boolean VerifyPin(String pin) throws UnsupportedEncodingException, WrongLengthOfPINException, WrongPINCharactersException, CardException, WrongPINException {
        byte[] pinBytes = pin.getBytes("ISO-8859-1");

        return VerifyPin(pinBytes);
    }

    /**
     * Verifies PIN from byte array
     *
     * @param pin byte array of PIN characters
     * @return true if PIN verification was successful, false otherwise
     * @throws WrongLengthOfPINException if length of PIN characters isn't 4-8
     * characters long
     * @throws WrongPINCharactersException if at least 1 character is not
     * supported - is different than numerical character
     * @throws CardException if there was error that occurred during
     * communication with the Smart Card stack or the card itself
     * @throws WrongPINException if PIN was wrong and counter of PIN tries left
     * has been decremented
     */
    public boolean VerifyPin(byte pin[]) throws WrongLengthOfPINException, WrongPINCharactersException, CardException, WrongPINException {

        if ((pin.length < 4) || (pin.length > 8)) {
            throw new WrongLengthOfPINException();
        }
        String password = new String(pin);
        if (!password.matches("(\\d)*")) {
            throw new WrongPINCharactersException();
        }


        byte apduToVerifyPin[] = new byte[13];
        byte prefix[] = {(byte) 0xa0, (byte) 0x20, (byte) 0x00, (byte) 0x01, (byte) 0x08};
        System.arraycopy(prefix, 0, apduToVerifyPin, 0, prefix.length);
        System.arraycopy(pin, 0, apduToVerifyPin, 5, pin.length);


        for (int i = pin.length + 5; i <= 12; i++) {
            apduToVerifyPin[i] = (byte) 0xff;
        }

        ResponseAPDU response = sendAPDU(apduToVerifyPin);
        if (response.getSW() == 0x9000) {
            return true;
        }
        throw new WrongPINException();
    }

    /**
     * Getter for m_card
     *
     * @return card
     */
    public static Card getCard() {
        return m_card;
    }

    /**
     * Getter for m_channel
     *
     * @return channel
     */
    public static CardChannel getChannel() {
        return m_channel;
    }

    /**
     * Getter for m_terminal
     *
     * @return terminal
     */
    public static CardTerminal getTerminal() {
        return m_terminal;
    }
}
