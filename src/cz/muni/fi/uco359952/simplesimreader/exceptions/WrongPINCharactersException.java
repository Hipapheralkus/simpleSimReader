package cz.muni.fi.uco359952.simplesimreader.exceptions;

/**
 * Thrown when there was illegal character used in PIN code.
 *
 * @author Andrej Simko
 */
public class WrongPINCharactersException extends Exception {

    /**
     * Constructs a WrongPINCharactersException with no detail message.
     */
    public WrongPINCharactersException() {
        super("Some characters are not allowed in PIN - only numerical characters are allowed.");
    }
}
