package no.truben.dbox.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import no.truben.dbox.Main;

/**
 *
 * @author truben
 */
public class Updater {
    
    public static void CheckForUpdate(boolean notify) {
        URL updateURL;
        {
            BufferedReader in = null;
            try {

                updateURL = new URL(Constants.CURRENT_URL);
                in = new BufferedReader(new InputStreamReader(updateURL.openStream()));
                int[] version = parseVersion(in.readLine());
                boolean isNewerAvailable = isNewestVersion(version);
                if (isNewerAvailable) {

                    JCheckBox check = new JCheckBox("Do not show this message again.");

                    String message = "There is a newer version of D-Box available!\nThe newest version is " +
                                     version[0] + "." + version[1] + " and you have version " + Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION +
                                     ".\nGo to D-Box' homepage and download?\n";
                    Object[] params;
                    if(!notify)
                        params = new Object[]{message, check};
                    else
                        params =  new Object[]{message};
                        
                    int answer = JOptionPane.showConfirmDialog(null, params, "Newer version available", JOptionPane.YES_NO_OPTION);
                    Main.pref.setCheckForUpdates(!check.isSelected());

                    if (answer == JOptionPane.YES_OPTION) {
                        BrowserControl.openUrl(Constants.DBOX_HOME_URL);
                    }
                } else if (notify) {
                    JOptionPane.showMessageDialog(null, "You have the newest version (" + Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION +
                            ")! Congratulations!", "No need to update", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (IOException ex) {
                if (notify) JOptionPane.showMessageDialog(null, "Unable to connect to... \n" + Constants.CURRENT_URL);
            } catch (NumberFormatException ex) {
                if (notify) JOptionPane.showMessageDialog(null, "Unable to check for new version beacuse of server-side problems.");
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    private static int[] parseVersion(String version) {
        String[] numbers = version.split("\\.");
        if (numbers.length != 2) {
            throw new NumberFormatException("Wrong number of dots");
        }
        return new int[]{Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1])};
    }

    private static boolean isNewestVersion(int[] version) {
        if (version[0] > Constants.MAJOR_VERSION) {
            return true;
        }
        return version[1] > Constants.MINOR_VERSION;
    }
}
