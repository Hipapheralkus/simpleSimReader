package cz.muni.fi.uco359952.simplesimreader.exceptions;

/**
 * Thrown when used PIN code isn't 4-8 characters long.
 *
 * @author Andrej Simko
 */
public class WrongLengthOfPINException extends Exception {

    /**
     * Constructs a WrongLengthOfPINException with no detail message.
     */
    public WrongLengthOfPINException() {
        super("Wrong length of PIN - PIN must be 4-8 characters long");
    }
}
