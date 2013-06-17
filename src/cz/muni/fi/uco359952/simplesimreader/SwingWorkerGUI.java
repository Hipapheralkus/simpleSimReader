package cz.muni.fi.uco359952.simplesimreader;

import javax.swing.SwingWorker;

/**
 * SwingWorkerGUI is Swing Worker and computes either writing data with or
 * without authentication on System.out.
 *
 * @author Andrej Simko
 */
public class SwingWorkerGUI extends SwingWorker<Object, Integer> {

    private int type;

    /**
     * Constructor for Swing Worker
     *
     * @param type if type = 0 then it means writing data without authentication
     * to System.out. <p>If type = 1 then it means writing data with
     * authentication to System.out.
     */
    public SwingWorkerGUI(int type) {
        this.type = type;
    }

    @Override
    protected Object doInBackground() {
        synchronized (GUI.class) {
            switch (type) {
                case (0):
                    GUI.writer.writeDataWithoutAuthentication();
                    break;
                case (1):
                    GUI.writer.writeDataWithAuthentication();
                    break;
            }
            return null;
        }
    }
}
