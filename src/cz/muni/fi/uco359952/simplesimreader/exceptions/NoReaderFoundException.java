package cz.muni.fi.uco359952.simplesimreader.exceptions;

/**
 * Thrown when no PC/SC reader of smart cards was found.
 *
 * @author Andrej Simko
 */
public class NoReaderFoundException extends Exception {

    /**
     * Constructs a NoReaderFoundException with no detail message.
     */
    public NoReaderFoundException() {
        super("No card reader which supports PC/SC was found.");
    }
}
