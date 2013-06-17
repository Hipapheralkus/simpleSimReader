package cz.muni.fi.uco359952.simplesimreader.exceptions;

import javax.smartcardio.CardException;

/**
 * Thrown when an incorrect APDU was used and if card has returned Status Word
 * indicating process aborted.
 *
 * @author Andrej Simko
 */
public class BadStatusWordException extends CardException {

    /**
     * Constructs a BadStatusWordException with no detail message.
     */
    public BadStatusWordException() {
        super("Bad Status Word - process aborted");
    }

    /**
     * Constructs a BadStatusWordException with the specified detail message.
     * @param sWmeaning StatusWord meaning
     */
    public BadStatusWordException(String sWmeaning) {
        super("Bad Status Word - process aborted: " + sWmeaning);
    }
}
