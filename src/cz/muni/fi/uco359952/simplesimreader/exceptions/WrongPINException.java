package cz.muni.fi.uco359952.simplesimreader.exceptions;

/**
 * Thrown when used PIN was incorrect.
 *
 * @author Andrej Simko
 */
public class WrongPINException extends Exception {

    /**
     * Constructs a WrongPINException with no detail message.
     */
    public WrongPINException() {
        super("Wrong PIN Entered");
    }
}
