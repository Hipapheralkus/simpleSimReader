package cz.muni.fi.uco359952.simplesimreader;

import cz.muni.fi.uco359952.simplesimreader.exceptions.NoReaderFoundException;
import cz.muni.fi.uco359952.simplesimreader.exceptions.WrongPINCharactersException;
import cz.muni.fi.uco359952.simplesimreader.exceptions.WrongLengthOfPINException;
import cz.muni.fi.uco359952.simplesimreader.exceptions.WrongPINException;
import java.io.Console;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardNotPresentException;

/**
 * Main main class for Terminal usage - prints data without authentication, asks
 * for PIN and prints data with authentication on System.out.
 *
 * @author Andrej Simko
 */
public class Main {

    /**
     * Stores WriteIntoSystemOut instance for further use.
     */
    public static WriteIntoSystemOut writer;


    /**
     * Gets PIN from user in terminal.
     *
     * @return user's PIN
     */
    public static String getCorrectPinFromUser() {
        String password = "";
        try {
            do {
                Console c = System.console();
                char[] passwordarray = c.readPassword("Password: ");
                password = String.valueOf(passwordarray);
                if (!password.matches("\\d{4,8}")) {
                    System.err.println("PIN must only have characters 0-9 from range 4 to 8.");
                } else {
                    return password;
                }
            } while (true);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return password;
    }

    /**
     * Main method for terminal usage.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        writer = new WriteIntoSystemOut();
        try {
            if (writer.getters.worker.manager.ConnectToCard()) {
                System.out.println("Successfully connected to card");
                writer.writeDataWithoutAuthentication();
                System.out.flush();

                System.out.println("\nEnter your PIN: ");
                if (writer.getters.worker.manager.VerifyPin(getCorrectPinFromUser())) {
                    Converter.fillEFSizes();
                    writer.writeDataWithAuthentication();
                }
            }
        } catch (NoReaderFoundException ex) {
            System.err.println("No terminal found");
        } catch (CardNotPresentException ex) {
            System.err.println("No card found");
        } catch (WrongPINException ex) {
            System.err.println("Wrong PIN entered");
        } catch (WrongLengthOfPINException ex) {
            System.err.println("Wrong PIN length entered: PIN must have 4-8 characters");
        } catch (WrongPINCharactersException ex) {
            System.err.println("PIN must consist only of numerical characters");
        } catch (Exception ex) {
            System.err.println("Exception : " + ex);
        } finally {
            try {
                writer.getters.worker.manager.DisconnectFromCard();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
