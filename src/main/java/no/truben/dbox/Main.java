package no.truben.dbox;

import no.truben.dbox.GUI.windows.MainWindow;
import no.truben.dbox.model.PreferencesBean;
import no.truben.dbox.util.Updater;
import no.truben.dbox.util.OSXAdapter;
import no.truben.dbox.util.ThemeSupport;
import no.truben.dbox.util.HelperClass;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import no.truben.dbox.util.Constants;

/**
 * @author Truben
 */

public class Main {
    
    public static String appFolder = HelperClass.getWorkingDirectory("dbox").getAbsolutePath() + File.separator;
    public static String configFile = appFolder  + "dbox.config";
    public static String gameFile = appFolder + "gamelist.dat";
    public final static PreferencesBean pref = new PreferencesBean();
    public static MainWindow n;
    public static ThemeSupport theme;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {



        try {
            // Read command line config
            for(int i = 0; i < args.length; i++) {
                if(args[i].toLowerCase().equals("-config")) {
                    configFile = args[++i];
                    System.out.println("Use config file " + configFile);
                }
                if(args[i].toLowerCase().equals("-gamefile")) {
                    gameFile = args[++i];
                    System.out.println("Use gamefile file " + gameFile);
                }
                if(args[i].toLowerCase().startsWith("-ver")) {
                    System.out.println("D-Box " + Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION);
                    return;
                }
            }

            System.setProperty("apple.laf.useScreenMenuBar","true");
            java.lang.System.setProperty("com.apple.mrj.application.apple.menu.about.name", "D-Box");

            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

            pref.readConfig(configFile);

            // Load theme
            if(!pref.getTheme().equals(""))
                theme = new ThemeSupport(new File(pref.getTheme()));
            else
                theme = new ThemeSupport();

            n = new MainWindow();
            
            n.setVisible(true);

            if(pref.isCheckForUpdates())
                Updater.CheckForUpdate(false);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    public static void requestClose() {
            if(!n.isVisible()) {
                n.bl.clearList();
                n = null;
                main(new String[]{});
            }
            else
                System.exit(0);
    }

    static void fixOldVersion() {
        File configNew = new File(configFile);
        // Check if the new file exists, and if it does, get out of here
        if(!configNew.exists()) {
            File configOld = new File("dbox.config");
            if(configOld.exists()) {
                try {
                    HelperClass.copyFile(configOld, configNew);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            File gameOld = new File("gamelist.dat");
            File gameNew = new File(gameFile);
            if(gameOld.exists() && !gameNew.exists()) {
                try {
                    HelperClass.copyFile(gameOld, gameNew);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
