/*
 * MainWindow.java
 *
 * Created on July 26, 2007, 8:54 PM
 * @author Truben
 */
package no.truben.dbox.GUI.windows;

import no.truben.dbox.GUI.windows.About;
import no.truben.dbox.util.Updater;
import no.truben.dbox.model.ApplicationList;
import no.truben.dbox.model.ApplicationBean;
import no.truben.dbox.GUI.floppyFlow.*;
import no.truben.dbox.GUI.windows.EditApplication;
import no.truben.dbox.util.FileChooserFilter;
import no.truben.dbox.util.OSXAdapter;
import no.truben.dbox.util.onlineresource.TrubenResource;
import no.truben.dbox.util.HelperClass;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import no.truben.dbox.GUI.windows.listRenderers.GameListRenderer;
import no.truben.dbox.util.BrowserControl;
import no.truben.dbox.util.FileDrop;
import no.truben.dbox.Main;
import no.truben.dbox.util.Constants;

public class MainWindow extends javax.swing.JFrame implements FocusListener {

    public static ApplicationList bl = new ApplicationList();
    BufferedImage[] images;
    //images
    final Icon fileEnabled;
    final Icon fileDisabled;
    final Icon runEnabled;
    final Icon runDisabled;
    final Icon prefEnabled;
    final Icon prefDisabled;
    final Icon searchArrow;
    final Icon searchArrowDisabled;
    protected JPanel coverflow;
    private CDShelf cdshelf;
    Point point = new Point();

    public void about() {
        mnuAboutActionPerformed(null);
    }

    public void preferences() {
        mnuPreferencesActionPerformed(null);
    }

    /** Creates new form MainWindow */
    public MainWindow() throws IOException {
        this.addFocusListener(this);
        setUndecorated(!Main.theme.isShowWindowDecoration());

        if (Main.theme.isUnifiedToolbar()) {
            getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);


        }
        bl = deSerialize(Main.gameFile);

        //pref.readConfig(Main.configFile);

        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/no/truben/dbox/img/ikon.gif")).getImage());

        try {
            initComponents();

        } catch (java.lang.ClassCastException e) {
            pack();
        }


        // Genre filter menu
        if (true) {
            JMenuItem m = new JMenuItem();
            m.setText("Favorites");
            m.addActionListener(new Filter(this));
            searchMenu.add(m);
            searchMenu.addSeparator();
        }

        for (String s : Main.pref.getGenres()) {
            JMenuItem m = new JMenuItem();
            m.setText(s);
            m.addActionListener(new Filter(this));
            searchMenu.add(m);

            JCheckBoxMenuItem menu = new JCheckBoxMenuItem();
            menu.setText(s);
            menu.addActionListener(new SetGenre(this, s));
            menu.setVisible(true);
            mnuListSetGenre.add(menu);
        }

        // Set up images
        runEnabled = new javax.swing.ImageIcon(Main.theme.getPlayActiveImage());
        runDisabled = new javax.swing.ImageIcon(Main.theme.getPlayInactiveImage());
        fileEnabled = new javax.swing.ImageIcon(Main.theme.getEditActiveImage());
        fileDisabled = new javax.swing.ImageIcon(Main.theme.getEditInactiveImage());
        prefEnabled = new javax.swing.ImageIcon(Main.theme.getToolsActiveImage());
        prefDisabled = new javax.swing.ImageIcon(Main.theme.getToolsInactiveImage());
        searchArrow = new javax.swing.ImageIcon(Main.theme.getSearchActiveImage());
        searchArrowDisabled = new javax.swing.ImageIcon(Main.theme.getSearchInactiveImage());

        try {
            Main.pref.writeConfig(Main.configFile);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.setSize(Main.pref.getWindowWidth(), Main.pref.getWindowHeight());

        centerScreen();
        updateList();
        applicationList.requestFocus();
        jPanel1.setBorder(jTextField1.getBorder());

        // Show the getting started screen
        if (Main.pref.isFirstStart()) {
            GettingStarted h = new GettingStarted(this, true);
            h.setVisible(true);
            Main.pref.setFirstStart(false);
            Main.pref.writeConfig(Main.configFile);
        }

        // if the dosbox path is undefined, we try to find a built in
        if (Main.pref.getDosBoxPath().equals("")) {
            try {
                File dosbox = new File("." + File.separatorChar + "Dosbox");

                String pathen = "";

                File[] files = dosbox.listFiles();
                for (File f : files) {
                    String s = f.getName().toLowerCase();
                    if (s.equals("dosbox.exe")) { // win32
                        pathen = f.getAbsolutePath();
                        break;
                    } else if (s.equals("dosbox.app")) { // mac
                        pathen = f.getAbsolutePath() + "/Contents/MacOS/DOSBox";
                        break;
                    }
                }
                Main.pref.setDosBoxPath(pathen);
            } catch (Exception e) {
            }
        }

        // If we're on mac
        if (Main.pref.getDosBoxPath().equals("")) {
            try {
                File dosbox = new File("./D-Box.app/Contents/Resources/Java");

                String pathen = "";

                File[] files = dosbox.listFiles();
                for (File f : files) {
                    String s = f.getName().toLowerCase();
                    if (s.equals("dosbox.app")) { // mac
                        pathen = f.getAbsolutePath() + "/Contents/MacOS/DOSBox";
                        break;
                    }
                }
                Main.pref.setDosBoxPath(pathen);
            } catch (Exception e) {
            }
        }

        // drag & drop
        createDropTarget(applicationList);

        // OSX stuff
        if (HelperClass.getOS() == HelperClass.MACOS) {
            //buildOSXMenues();
            try {
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("dispose", (Class[]) null));
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
                OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[]) null));
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Theme

        setBackground(Main.theme.getBackgroundColor());

        if (!Main.theme.isShowBorders()) {
            jPanel1.setBorder(null);
        }

        if (Main.pref.isStartWithFloppyFlow()) {
            toggleView();

        }
    }

    /**
     * Dynammicaly creates a floppyflow
     */
    private void createCoverFlow() {
        if (cdshelf != null) {
            coverflow.remove(cdshelf);
            cdshelf = null;
        }
        coverflow = new JPanel();
        coverflow.setLayout(new StackLayout());
        cdshelf = new CDShelf(this);
        coverflow.add(new GradientPanel(), StackLayout.BOTTOM);
        coverflow.add(cdshelf, StackLayout.TOP);
        coverflow.setPreferredSize(new Dimension(200, 200));

        createDropTarget(coverflow);
    }

    /**
     * Makes a component ready to get files dropped from the OS
     * @param c the component that we want to be droppable
     */
    private void createDropTarget(Component c) {
        new FileDrop(c, new FileDrop.Listener() {

            public void filesDropped(java.io.File[] files) {
                if (files.length != 1) {
                    int s = JOptionPane.showConfirmDialog(null, "You have dropped " + files.length + " files. Are you sure you want to continue?", "Add multiple applications", JOptionPane.YES_NO_OPTION);
                    if (s != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                for (File f : files) {
                    createNewMagicProfile(f.getAbsoluteFile());
                }
            }
        });
    }

    /**
     * A metod that tries to insert an application into
     * dbox' database using a file or directory
     *
     * @param file the main executable
     */
    private void createNewMagicProfile(File file) {
        if (file.isDirectory()) {
            File dirfiles[] = file.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    return !pathname.isHidden();
                }
            });


            while (true) {
                if (dirfiles.length == 1 && dirfiles[0].isDirectory()) {
                    dirfiles = dirfiles[0].listFiles();
                } else {
                    break;
                }

            }
            int count = 0;
            for (File f : dirfiles) {
                if (f.toString().toLowerCase().indexOf("setup") == -1 && f.toString().toLowerCase().indexOf("install") == -1 && (f.toString().toLowerCase().endsWith("pif") || f.toString().toLowerCase().endsWith("exe") || f.toString().toLowerCase().endsWith("com") || f.toString().toLowerCase().endsWith("bat"))) {
                    count++;
                }

            }
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Can't find any suitable executable files.", "Select another directory.", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] possible = new String[count];

            count = 0;
            for (File f : dirfiles) {
                if (f.toString().toLowerCase().indexOf("setup") == -1 && f.toString().toLowerCase().indexOf("install") == -1 && (f.toString().toLowerCase().endsWith("pif") || f.toString().toLowerCase().endsWith("exe") || f.toString().toLowerCase().endsWith("com") || f.toString().toLowerCase().endsWith("bat"))) {
                    possible[count++] = f.toString().substring(f.toString().lastIndexOf(File.separator) + 1);
                }

            }
            String chosen;
            if (count == 1) {
                chosen = possible[0];

            } else {
                chosen = (String) JOptionPane.showInputDialog(
                        null, "You have dropped a directory. What is the main executable?",
                        "What's the name of the game?",
                        JOptionPane.QUESTION_MESSAGE,
                        null, possible, possible[0]);
                if (chosen == null) {
                    return;
                }
            }

            for (File f : dirfiles) {
                if (f.toString().endsWith(chosen)) {
                    file = f;
                }
            }
        }
        if (file.getName().toLowerCase().endsWith("dat")) {
            int s = JOptionPane.showConfirmDialog(this, "You are about to add the games from the game list '" + file.getName() + "' to your library. Continue?", "Merge gamelist", JOptionPane.YES_NO_OPTION);
            if (s != JOptionPane.YES_OPTION) {
                return;


            }
            deSerialize(file.getAbsolutePath());
            updateList();

            return;
        } else if (!file.getName().toLowerCase().endsWith("exe") && !file.getName().toLowerCase().endsWith("com") && !file.getName().toLowerCase().endsWith("bat") && !file.getName().toLowerCase().endsWith("pif")) {
            int s = JOptionPane.showConfirmDialog(this, "This doesn't look like an executable file. Executable files normally ends with .bat, .exe or .com.\n\nDo you still want to continue?", "You're almost there...", JOptionPane.YES_NO_OPTION);
            if (s != JOptionPane.YES_OPTION)
                return;
        }

        try {
            ApplicationBean d = new ApplicationBean();
            d.setGame(file.getName());
            d.setPath(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separatorChar)));

            File[] files = file.getParentFile().listFiles();
            for (File f : files) {
                String s = f.getName().toLowerCase();
                if (s.endsWith("ico")) {
                    d.setIcon(f.getAbsolutePath());
                } else if (s.endsWith("exe") || s.endsWith("bat") || s.endsWith("com")) {
                    if (s.indexOf("setup") != -1 || s.indexOf("install") != -1) {
                        d.setInstaller(f.toString().substring(f.toString().lastIndexOf(File.separator) + File.separator.length()));
                    }

                }
            }

            String hash = TrubenResource.getMD5(file.getAbsolutePath());
            TrubenResource or = new TrubenResource(hash);
            if(or.isValid()) {
                d = or.fillInInformation(d);
            }
            else {
                String[] choice = new String[3];

                choice[0] = file.getParentFile().getAbsolutePath().substring(file.getParentFile().getAbsolutePath().lastIndexOf(File.separator) + 1);
                choice[1] = file.getName().substring(0, 1).toUpperCase() + file.getName().substring(1, file.getName().lastIndexOf('.')).toLowerCase();
                choice[2] = "Something else...";

                String input = (String) JOptionPane.showInputDialog(
                        null, "What is the title of the application? Select one of the proposals,\n" +
                        "or select \"Something else...\" to type your own.",
                        "What's the name of the game?",
                        JOptionPane.QUESTION_MESSAGE,
                        null, choice, choice[0]);

                if (input == null) {
                    return;
                }

                if (input.equals(choice[2])) {
                    d.setName(JOptionPane.showInputDialog("Type the name of the application", choice[1]));

                }
                else {
                    d.setName(input);
                }
            }

            bl.addGame(d);
            updateList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Something wrong happened. You have to add the application the hard way.", "Sorry...", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        writeApplicationDatabase(Main.gameFile);
        try {
            Main.pref.setWindowHeight(this.getHeight());
            Main.pref.setWindowWidth(this.getWidth());
            Main.pref.writeConfig(Main.configFile);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        Main.requestClose();
    }

    /**
     * Loads a application database
     * @param name The name of the database
     * @return the database
     */
    private ApplicationList deSerialize(String name) {
        String config = "";
        Scanner s = null;

        try {
            s = new Scanner(new File(name));
        } catch (FileNotFoundException ex) {
            return new ApplicationList();
        }

        while (s.hasNext()) {
            config += s.nextLine() + "\n";
        }
        bl.readConfig(config);
        return bl;
    }

    /**
     * Writes the application database to a file
     * @param fileName The filename that we write to
     */
    public void writeApplicationDatabase(String fileName) {
        FileWriter fstream = null;
        try {
            fstream = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fstream);
            writer.write(bl.toConfigString());
            //Close the output stream
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Toggles between standard view and "floppy view"
     */
    private void toggleView() {
        if (coverflow != null && coverflow.isVisible()) {
            coverflow.setVisible(false);
            applicationList.setVisible(true);
            jScrollPane1.setViewportView(applicationList);
            applicationList.requestFocus();
            Main.pref.setStartWithFloppyFlow(false);
        } else {
            coverflow = new JPanel();
            coverflow.setVisible(true);
            applicationList.setVisible(false);
            updateList();
            jScrollPane1.setViewportView(coverflow);
            coverflow.requestFocus();
            Main.pref.setStartWithFloppyFlow(true);
        }
        this.repaint();

    }

    /**
     * Putter boksen midt paa skjermen
     */
    public void centerScreen() {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
    }

    /**
     * Writes a DosBOX configuration file to a specific file
     * @param filename The name of the config file
     * @param cpuPref Preferences that belongs to the CPU section
     * @param renderPref Preferences that belongs to the RENDER section
     * @param sdlPref Preferences that belongs to the SDI section
     * @param dosPref Preferences that belongs to the DOS section
     * @param autoexecPref Preferences that belongs to the AUTOEXEC section
     */
    private void writeConfig(String filename, HashMap<String, HashMap<String, String>> pref, ArrayList<String> autoexec) {

        String ut = "";

        for (String s : pref.keySet()) {
            ut += "[" + s + "]\n";
            HashMap<String, String> properties = pref.get(s);
            for (String p : properties.keySet()) {
                ut += p + " = " + properties.get(p) + "\n";
            }
            ut += "\n";
        }

        ut += "[AUTOEXEC]\n";
        for (String s : autoexec) {
            ut += s + "\n";
        }

        try {
            java.io.FileWriter fw = new java.io.FileWriter(filename);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            bw.write(ut);
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    private void updateList() {
        int s = applicationList.getSelectedIndex();
        applicationList.setModel(new javax.swing.AbstractListModel() {

            String[] strings = bl.getGameList();

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                if (strings[i].equals("")) {
                    return "(untitled)";
                }
                else {
                    return strings[i];
                }
            }
        });

        applicationList.setSelectedIndex(s);

        if (coverflow != null && coverflow.isVisible()) {
            createCoverFlow();
            coverflow.updateUI();
        }
    }

    private void updateList(String search) {
        final String s = search;
        applicationList.setModel(new javax.swing.AbstractListModel() {

            String[] strings = bl.getGameList(s);

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }

    public void updateListGenre(String search) {
        final String s = search;
        applicationList.setModel(new javax.swing.AbstractListModel() {

            String[] strings = bl.getGameListGenre(s);

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }

    public void updateListFavorite() {
        applicationList.setModel(new javax.swing.AbstractListModel() {

            String[] strings = bl.getFavoriteGameList();

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        runMenu = new javax.swing.JPopupMenu();
        mnuRun2 = new javax.swing.JMenuItem();
        mnuSetup2 = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JPopupMenu();
        mnuNew2 = new javax.swing.JMenuItem();
        mnuEdit2 = new javax.swing.JMenuItem();
        mnuDelete2 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        mnuShowScreenShot = new javax.swing.JMenuItem();
        prefMenu = new javax.swing.JPopupMenu();
        mnuPreferences = new javax.swing.JMenuItem();
        mnuView = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuHelpAd = new javax.swing.JMenuItem();
        mnuHelpScreen = new javax.swing.JMenuItem();
        mnuTools = new javax.swing.JMenu();
        mnuImport = new javax.swing.JMenuItem();
        mnuExport = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuClear = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuGettingStarted = new javax.swing.JMenuItem();
        mnuCheckNewest = new javax.swing.JMenuItem();
        mnuAbout = new javax.swing.JMenuItem();
        mnuWeb = new javax.swing.JMenu();
        mnuHome = new javax.swing.JMenuItem();
        mnuDosbox = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        mnuQuit = new javax.swing.JMenuItem();
        searchMenu = new javax.swing.JPopupMenu();
        jTextField1 = new javax.swing.JTextField();
        popupMenu = new javax.swing.JPopupMenu();
        mnuListRun = new javax.swing.JMenuItem();
        mnuListSetup = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuListEdit = new javax.swing.JMenuItem();
        mnuListRemove = new javax.swing.JMenuItem();
        mnuListSetGenre = new javax.swing.JMenu();
        mnuListFavorite = new javax.swing.JCheckBoxMenuItem();
        mnuViewScreenShot = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        applicationList = new javax.swing.JList();
        applicationList.setCellRenderer(new GameListRenderer());
        applicationList.setBackground(Main.theme.getGameBackgroundColor());
        panelControls = new javax.swing.JPanel()
        {
            ImageIcon backImage = new javax.swing.ImageIcon(Main.theme.getBackgroundImage());
            Image image = backImage.getImage();
            int w = backImage.getIconWidth();

            public void paintComponent (Graphics g) {
                for(int i=0;i<Main.theme.getBackgroundRepeat();i++)
                g.drawImage(image, w*i, 0, this);
                super.paintComponent(g);
            }
        }
        ;
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();

        mnuRun2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        mnuRun2.setFont(mnuRun2.getFont().deriveFont(mnuRun2.getFont().getStyle() | java.awt.Font.BOLD));
        mnuRun2.setText("Run");
        mnuRun2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRunActionPerformed(evt);
            }
        });
        runMenu.add(mnuRun2);

        mnuSetup2.setText("Setup");
        mnuSetup2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetupActionPerformed(evt);
            }
        });
        runMenu.add(mnuSetup2);

        mnuNew2.setText("New Game");
        mnuNew2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewActionPerformed(evt);
            }
        });
        editMenu.add(mnuNew2);

        mnuEdit2.setText("Edit Game");
        mnuEdit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditActionPerformed(evt);
            }
        });
        editMenu.add(mnuEdit2);

        mnuDelete2.setText("Remove Game");
        mnuDelete2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDeleteActionPerformed(evt);
            }
        });
        editMenu.add(mnuDelete2);
        editMenu.add(jSeparator5);

        mnuShowScreenShot.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
        mnuShowScreenShot.setText("Show Screenshot");
        mnuShowScreenShot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuShowScreenShotActionPerformed(evt);
            }
        });
        editMenu.add(mnuShowScreenShot);

        mnuPreferences.setFont(mnuPreferences.getFont().deriveFont(mnuPreferences.getFont().getStyle() | java.awt.Font.BOLD));
        mnuPreferences.setText("Preferences");
        mnuPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPreferencesActionPerformed(evt);
            }
        });
        prefMenu.add(mnuPreferences);

        mnuView.setText("Flow View");
        mnuView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViewActionPerformed(evt);
            }
        });
        prefMenu.add(mnuView);

        mnuHelp.setText("Help");

        mnuHelpAd.setText("How do I add an Application?");
        mnuHelpAd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHelpAdActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuHelpAd);

        mnuHelpScreen.setText("How do I Capture and view Screenshots?");
        mnuHelp.add(mnuHelpScreen);

        prefMenu.add(mnuHelp);

        mnuTools.setText("Application List Tools");

        mnuImport.setText("Import Application List");
        mnuImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportActionPerformed(evt);
            }
        });
        mnuTools.add(mnuImport);

        mnuExport.setText("Export Application List");
        mnuExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportActionPerformed(evt);
            }
        });
        mnuTools.add(mnuExport);
        mnuTools.add(jSeparator2);

        mnuClear.setText("Clear Application List");
        mnuClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClearActionPerformed(evt);
            }
        });
        mnuTools.add(mnuClear);

        prefMenu.add(mnuTools);
        prefMenu.add(jSeparator1);

        mnuGettingStarted.setText("Getting Started...");
        mnuGettingStarted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGettingStartedActionPerformed(evt);
            }
        });
        prefMenu.add(mnuGettingStarted);

        mnuCheckNewest.setText("Check for updates");
        mnuCheckNewest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCheckNewestActionPerformed(evt);
            }
        });
        prefMenu.add(mnuCheckNewest);

        mnuAbout.setText("About D-Box version " + Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION + "");
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        prefMenu.add(mnuAbout);

        mnuWeb.setText("Web links");

        mnuHome.setText("D-Box' homepage");
        mnuHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHomeActionPerformed(evt);
            }
        });
        mnuWeb.add(mnuHome);

        mnuDosbox.setText("DOSBox' Homepage");
        mnuDosbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDosboxActionPerformed(evt);
            }
        });
        mnuWeb.add(mnuDosbox);

        prefMenu.add(mnuWeb);
        prefMenu.add(jSeparator4);

        mnuQuit.setText("Quit D-Box");
        mnuQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuQuitActionPerformed(evt);
            }
        });
        prefMenu.add(mnuQuit);

        jTextField1.setVisible(false);

        mnuListRun.setText("Run");
        mnuListRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListRunActionPerformed(evt);
            }
        });
        popupMenu.add(mnuListRun);

        mnuListSetup.setText("Setup");
        mnuListSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListSetupActionPerformed(evt);
            }
        });
        popupMenu.add(mnuListSetup);
        popupMenu.add(jSeparator3);

        mnuListEdit.setText("Edit...");
        mnuListEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListEditActionPerformed(evt);
            }
        });
        popupMenu.add(mnuListEdit);

        mnuListRemove.setText("Remove...");
        mnuListRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListRemoveActionPerformed(evt);
            }
        });
        popupMenu.add(mnuListRemove);

        mnuListSetGenre.setText("Set Genre");
        popupMenu.add(mnuListSetGenre);

        mnuListFavorite.setSelected(true);
        mnuListFavorite.setText("Favorite");
        mnuListFavorite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListFavoriteActionPerformed(evt);
            }
        });
        popupMenu.add(mnuListFavorite);

        mnuViewScreenShot.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
        mnuViewScreenShot.setText("View Screenshot");
        mnuViewScreenShot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViewScreenShotActionPerformed(evt);
            }
        });
        popupMenu.add(mnuViewScreenShot);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("D-Box");
        setMinimumSize(new java.awt.Dimension(200, 200));

        jScrollPane1.setBorder(null);

        applicationList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        applicationList.setFocusCycleRoot(true);
        applicationList.setFocusTraversalPolicyProvider(true);
        applicationList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        applicationList.setNextFocusableComponent(txtSearch);
        applicationList.setVisibleRowCount(-1);
        applicationList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                applicationListMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Double(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                applicationListMouseEntered(evt);
            }
        });
        applicationList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                applicationListKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(applicationList);

        panelControls.setOpaque(false);
        panelControls.setPreferredSize(new java.awt.Dimension(684, 50));
        panelControls.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelControlsMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelControlsMouseClicked(evt);
            }
        });
        panelControls.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                panelControlsMouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelControlsMouseDragged(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(Main.theme.getEditInactiveImage()));
        jLabel2.setToolTipText("Game information");
        jLabel2.setIconTextGap(0);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editMenu, org.jdesktop.beansbinding.ObjectProperty.create(), jLabel2, org.jdesktop.beansbinding.BeanProperty.create("componentPopupMenu"));
        bindingGroup.addBinding(binding);

        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel2MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(Main.theme.getPlayInactiveImage() ));
        jLabel4.setToolTipText("Run Application");
        jLabel4.setIconTextGap(0);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel4MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel4MouseEntered(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(Main.theme.getToolsInactiveImage()));
        jLabel5.setToolTipText("Tools");
        jLabel5.setIconTextGap(0);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, prefMenu, org.jdesktop.beansbinding.ObjectProperty.create(), jLabel5, org.jdesktop.beansbinding.BeanProperty.create("componentPopupMenu"));
        bindingGroup.addBinding(binding);

        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel5MouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel5MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel5MouseEntered(evt);
            }
        });

        jPanel1.setBackground(Main.theme.getSearchBackgroundColor());
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblSearch.setFont(lblSearch.getFont());
        lblSearch.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.disabledText"));
        lblSearch.setIcon(new javax.swing.ImageIcon(Main.theme.getSearchInactiveImage()));
        lblSearch.setLabelFor(txtSearch);
        lblSearch.setToolTipText("Click to filter by genre");
        lblSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSearchMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblSearchMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblSearchMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblSearchMousePressed(evt);
            }
        });

        txtSearch.setBackground(Main.theme.getSearchBackgroundColor() );
        txtSearch.setForeground(Main.theme.getSearchInactiveColor()
        );
        txtSearch.setText("Search");
        txtSearch.setToolTipText("Search the gamelist");
        txtSearch.setBorder(null);
        txtSearch.setNextFocusableComponent(applicationList);
        txtSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtSearchMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtSearchMouseEntered(evt);
            }
        });
        txtSearch.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSearchCaretUpdate(evt);
            }
        });
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSearchFocusLost(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(lblSearch)
                .add(2, 2, 2)
                .add(txtSearch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(lblSearch)
                .add(txtSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout panelControlsLayout = new org.jdesktop.layout.GroupLayout(panelControls);
        panelControls.setLayout(panelControlsLayout);
        panelControlsLayout.setHorizontalGroup(
            panelControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelControlsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 409, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelControlsLayout.setVerticalGroup(
            panelControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelControlsLayout.createSequentialGroup()
                .add(panelControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, panelControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                            .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(panelControlsLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelControls, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(panelControls, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSearchMouseClicked
}//GEN-LAST:event_lblSearchMouseClicked

private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
    About a = new About(this, true);
    a.setVisible(true);
    a = null;
}//GEN-LAST:event_mnuAboutActionPerformed

private void Double(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Double
    if (evt.getClickCount() == 2 && !evt.isAltDown() && (evt.getModifiers() & evt.BUTTON1_MASK) != 0) {
        mnuRunActionPerformed(null);

    } else {
    }
}//GEN-LAST:event_Double

private void mnuRunDosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRunDosBoxActionPerformed
    String[] par = new String[3];
    par[0] = Main.pref.getDosBoxPath();
    par[1] = "-c";
    par[2] = "@echo Have fun! Best wishes from D-Box :)";

    try {
        Runtime.getRuntime().exec(par);
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}//GEN-LAST:event_mnuRunDosBoxActionPerformed

private void mnuPrefsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrefsActionPerformed
    Preferences prf = new Preferences();
    prf.setModal(true);
    prf.setVisible(true);
    prf = null;
    try {
        Main.pref.writeConfig(Main.configFile);
    } catch (IOException ex) {
        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
    }
    applicationList.repaint();
}//GEN-LAST:event_mnuPrefsActionPerformed

private void mnuSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSetupActionPerformed
    ApplicationBean di = bl.getGame((String) applicationList.getSelectedValue());
    runApplication(di.getInstaller());
}//GEN-LAST:event_mnuSetupActionPerformed

    private String getCurrentDir() {
        File dir1 = new File(".");
        try {
            return dir1.getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
        mnuRunActionPerformed(null);
    }

private void mnuRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRunActionPerformed
    ApplicationBean di = bl.getGame((String) applicationList.getSelectedValue());
    if (di == null) {
        return;

    }
    runApplication(di.getGame());
}//GEN-LAST:event_mnuRunActionPerformed

    private void runApplication(String program) {

        // Do nothing if no applications are selected
        if (applicationList.getSelectedIndex() == -1) {
            return;

            // If there are no applications in the application list

        }
        if (bl.getNrGames() == 0) {
            int answ = JOptionPane.showConfirmDialog(this,
                    "To make D-Box useful, you got to add a game or two. Do you want to add one now?",
                    null, JOptionPane.YES_NO_OPTION);
            if (answ == JOptionPane.YES_OPTION) {
                mnuNewActionPerformed(null);
            }
            return;
        }

        ApplicationBean di = bl.getGame((String) applicationList.getSelectedValue());

        //Create HashMaps for preferences
        HashMap<String, HashMap<String, String>> allProps = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> cpu = new HashMap<String, String>();
        HashMap<String, String> renderer = new HashMap<String, String>();
        HashMap<String, String> sdl = new HashMap<String, String>();
        HashMap<String, String> dos = new HashMap<String, String>();
        HashMap<String, String> serial = new HashMap<String, String>();
        HashMap<String, String> ipx = new HashMap<String, String>();
        HashMap<String, String> dosbox = new HashMap<String, String>();
        HashMap<String, String> midi = new HashMap<String, String>();
        HashMap<String, String> gus = new HashMap<String, String>();
        HashMap<String, String> mixer = new HashMap<String, String>();

        ArrayList<String> autoexec = new ArrayList<String>();

        String capturePath = getCaptureDirectory(di);
        File dir = new File(capturePath);

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Directory Created: " + capturePath);

            } else {
                System.out.println("Directory is not created");

            }
        }

        dosbox.put("captures", capturePath);

        // Split the extras string
        String[] properties = new String[0];
        String[][] finito = new String[0][0];
        if (!di.getExtra().equals("")) {
            //   Parse
            properties = di.getExtra().substring(0, di.getExtra().length() - 1).split(";");
            finito = new String[properties.length][3];
            if (!di.getExtra().equals("")) {
                for (int i = 0; i < properties.length; i++) {
                    int first = properties[i].indexOf(" => ");
                    int second = properties[i].indexOf(" = ");
                    if (first <= 0) {
                        continue;


                    }
                    finito[i][0] = properties[i].substring(0, first);
                    if (finito[i][0].toLowerCase().equals("autoexec")) {
                        finito[i][1] = properties[i].substring(first + 4);

                    } else {
                        finito[i][1] = properties[i].substring(first + 4, second);
                        finito[i][2] = properties[i].substring(second + 3);
                    }
                }
            }
        }


        // Add settings to the configuration file
        cpu.put("cycles", di.getCycles() + "");
        addOtherSettings(finito, "cpu", cpu);
        allProps.put("CPU", cpu);

        renderer.put("frameskip", di.getFrameskip() + "");
        addOtherSettings(finito, "renderer", renderer);
        allProps.put("RENDER", renderer);

        sdl.put("fullscreen", Main.pref.isFullScreen() + "");
        addOtherSettings(finito, "sdl", sdl);
        allProps.put("SDL", sdl);

        dos.put("keyboardlayout", Main.pref.getKeyboardCode());
        addOtherSettings(finito, "dos", dos);
        allProps.put("DOS", dos);

        addOtherSettings(finito, "serial", serial);
        allProps.put("SERIAL", serial);

        addOtherSettings(finito, "ipx", ipx);
        allProps.put("IPX", serial);

        addOtherSettings(finito, "dosbox", dosbox);
        allProps.put("DOSBOX", dosbox);

        addOtherSettings(finito, "mixer", mixer);
        allProps.put("MIXER", mixer);

        addOtherSettings(finito, "gus", gus);
        allProps.put("GUS", gus);

        addOtherSettings(finito, "midi", midi);
        allProps.put("MIDI", midi);

        int number = 0;
        if (!di.getCdrom().equals("")) { // If we should mount a CD ROM
            String cd = "mount " + di.getCdromLetter() + " \"" + di.getCdrom() + "\" -t cdrom ";
            if (!di.getCdromLabel().equals("")) {
                cd += "-label " + di.getCdromLabel();

            }
            autoexec.add(number++, cd);
        }
        autoexec.add(number++, "mount c \"" + di.getPath() + "\"");
        autoexec.add(number++, "C:");
        autoexec.add(number++, program);
        for (int i = 0; i < finito.length; i++) {
            if (finito[i][0].toLowerCase().equals("autoexec")) {
                autoexec.add(finito[i][1]);

                // Write configfile

            }

        }
        writeConfig(Main.appFolder + "dosbox.conf",
                allProps, autoexec);

        // Build execute command
        String[] par = new String[6];
        par[0] = Main.pref.getDosBoxPath();

        // If we should try to close the dosbox window or keep it open
        if (!Main.pref.isKeepOpen()) {
            par[1] = "-c";
            par[2] = "exit";
        } else {
            par[1] = "-c";
            par[2] = "@echo Keep on rockin' in the free world!";
        }

        par[3] = "-conf";
        par[4] = Main.appFolder + "dosbox.conf";

        if (Main.pref.isNoConcole()) {
            par[5] = "-noconsole";

        } else {
            par[5] = "";

            // try to execute from the path if no dosbox path is present

        }
        if (Main.pref.getDosBoxPath().equals("")) {
            par[0] = "dosbox";
        }

        // Try to execute
        try {
            Runtime.getRuntime().exec(par);
        } catch (IOException ex) {
            // What to do if no dosbox path is available
            if (Main.pref.getDosBoxPath().equals("")) {
                String[] choices = {
                    "Let me show you where DOSBox is!",
                    "Please take me to DOSBox' homepage so I can download!",
                    "Get me out of here!"
                };
                String input = (String) JOptionPane.showInputDialog(
                        null, "D-Box needs DOSBox to work, but currently the path to DOSBox is set to nothing.\nIf you have DOSBox installed, please locate it for me. If not, please download and\ninstall DOSBox before continuing.\n\nPlease select your next step:",
                        "Can't find DOSBox!",
                        JOptionPane.QUESTION_MESSAGE,
                        null, choices, choices[0]);
                if (input.equals(choices[0])) {
                    mnuPrefsActionPerformed(null);

                } else if (input.equals(choices[1])) {
                    BrowserControl.openUrl(Constants.DOSBOX_DOWNLOAD_URL);
                    return;
                } else {
                    return;

                }
            } else {
                ex.printStackTrace();

            }
        }
    }

    private void addOtherSettings(String[][] finito, String section, HashMap<String, String> props) {
        for (int i = 0; i < finito.length; i++) {
            if (finito[i][0].toLowerCase().equals(section.toLowerCase())) {
                props.put(finito[i][1], finito[i][2]);
            }
        }
    }

    private String getCaptureDirectory(ApplicationBean di) {
        return Main.appFolder + "captures" + File.separator + di.getUniqueID() + File.separator;
    }

private void mnuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditActionPerformed
    String gm = "";
    if (applicationList.getSelectedIndex() == -1) {
        return;


    }
    if (!((String) applicationList.getSelectedValue()).equals("(untitled)")) {
        gm = (String) applicationList.getSelectedValue();


    }
    EditApplication ui = new EditApplication(bl.removeGame(gm), this);
    ui.setVisible(true);
    ui = null;
    updateList();
}//GEN-LAST:event_mnuEditActionPerformed

private void mnuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewActionPerformed
    EditApplication ui = new EditApplication(this);
    ui.setVisible(true);
    ui = null;
    updateList();
}//GEN-LAST:event_mnuNewActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (txtSearch.getText().equals("Search")) {
            updateList();
        } else {
            updateList(txtSearch.getText());
        }
}//GEN-LAST:event_txtSearchKeyReleased

private void mnuDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDeleteActionPerformed
    if (applicationList.getSelectedIndex() == -1) {
        return;
    }
    int a = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove " +
            applicationList.getSelectedValue() + " from the list?", "Please Confirm",
            JOptionPane.YES_NO_OPTION);
    if (a == JOptionPane.NO_OPTION) {
        return;
    }
    String gm = "";
    if (!((String) applicationList.getSelectedValue()).equals("(untitled)")) {
        gm = (String) applicationList.getSelectedValue();
    }

    bl.removeGame(gm);
    updateList();
}//GEN-LAST:event_mnuDeleteActionPerformed

private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
    System.exit(0);
}//GEN-LAST:event_mnuExitActionPerformed

private void applicationListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_applicationListKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        mnuRunActionPerformed(null);
    }
    if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
        showScreenCaptures();
    }
    if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        mnuDeleteActionPerformed(null);
    }
}//GEN-LAST:event_applicationListKeyPressed

    public void showScreenCaptures() {
        File files[] = getCaptureFiles();
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(this, "To view screen captures for this game, you got to capture some screenshots first. Press Ctrl + F5 while your playing!");
        } else {
            new ScreenShot(files);
        }
    }

    private File[] getCaptureFiles() {
        if(applicationList.getSelectedValue() == null)
            return new File[]{};

        ApplicationBean di = bl.getGame((String) applicationList.getSelectedValue());
        File files[] = new File(getCaptureDirectory(di)).listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return (pathname.getAbsolutePath().toLowerCase().endsWith(".png") ||
                        pathname.getAbsolutePath().toLowerCase().endsWith(".jpg"));
            }
        });
        return files;

    }
private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MousePressed
    editMenu.show(jLabel2, 0, jLabel2.getHeight() - 10);

}//GEN-LAST:event_jLabel2MousePressed

private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseReleased
}//GEN-LAST:event_jLabel2MouseReleased

private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
}//GEN-LAST:event_jLabel2MouseClicked

private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
    if(evt != null && evt.getButton() == MouseEvent.BUTTON1) {
        mnuRunActionPerformed(null);
    }
    else {
        runMenu.show(jLabel4, 0, jLabel4.getHeight() - 10);
    }
}//GEN-LAST:event_jLabel4MousePressed

private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseReleased
}//GEN-LAST:event_jLabel4MouseReleased

private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
}//GEN-LAST:event_jLabel4MouseClicked

private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MousePressed
    if (applicationList.isVisible()) {
        mnuView.setText("Floppy Flow (Experimental)");
    } else {
        mnuView.setText("List View (Classic)");
    }

    prefMenu.show(jLabel5, 0, jLabel5.getHeight() - 10);
}//GEN-LAST:event_jLabel5MousePressed

private void jLabel5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseReleased
}//GEN-LAST:event_jLabel5MouseReleased

private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
}//GEN-LAST:event_jLabel5MouseClicked

private void applicationListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_applicationListMouseReleased
    if (evt.getButton() == MouseEvent.BUTTON3) {//MouseEvent.BUTTON3) {
        if (applicationList.getSelectedIndex() != -1) {
            final String gamename = applicationList.getSelectedValue().toString();

            // Set correct name
            mnuListRun.setText("Run '" + gamename + "'");

            //is it marked as a favorite
            mnuListFavorite.setSelected(bl.getGame(gamename).isStar());

            //get the genre
            final String genre = bl.getGame(gamename).getGenre();

            for (Component c : mnuListSetGenre.getMenuComponents()) {
                JCheckBoxMenuItem jc = (JCheckBoxMenuItem) c;
                jc.setSelected(jc.getText().equals(genre));
            }
            popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
}//GEN-LAST:event_applicationListMouseReleased

private void jLabel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseEntered
    jLabel4.setIcon(runEnabled);
    if (prefMenu.isVisible() || editMenu.isVisible()) {
        jLabel4MousePressed(null);
    }
}//GEN-LAST:event_jLabel4MouseEntered

private void jLabel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseEntered
    jLabel2.setIcon(fileEnabled);
    if (prefMenu.isVisible() || runMenu.isVisible()) {
        jLabel2MousePressed(null);
    }
}//GEN-LAST:event_jLabel2MouseEntered

private void jLabel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseExited
    jLabel4.setIcon(runDisabled);
}//GEN-LAST:event_jLabel4MouseExited

private void jLabel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseExited
    jLabel2.setIcon(fileDisabled);
}//GEN-LAST:event_jLabel2MouseExited

private void jLabel5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseEntered
    jLabel5.setIcon(prefEnabled);
    if (runMenu.isVisible() || editMenu.isVisible()) {
        jLabel5MousePressed(null);
    }
}//GEN-LAST:event_jLabel5MouseEntered

private void jLabel5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseExited
    jLabel5.setIcon(prefDisabled);
}//GEN-LAST:event_jLabel5MouseExited

private void mnuPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPreferencesActionPerformed
    mnuPrefsActionPerformed(null);
}//GEN-LAST:event_mnuPreferencesActionPerformed

private void mnuHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHomeActionPerformed
    BrowserControl.openUrl(Constants.DBOX_HOME_URL);
}//GEN-LAST:event_mnuHomeActionPerformed

private void mnuDosboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDosboxActionPerformed
    BrowserControl.openUrl(Constants.DOSBOX_HOME_URL);
}//GEN-LAST:event_mnuDosboxActionPerformed

private void txtSearchMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchMouseEntered
}//GEN-LAST:event_txtSearchMouseEntered

private void txtSearchMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchMouseExited
}//GEN-LAST:event_txtSearchMouseExited

private void txtSearchCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSearchCaretUpdate
}//GEN-LAST:event_txtSearchCaretUpdate

private void lblSearchMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSearchMousePressed
    searchMenu.show(lblSearch, 0, lblSearch.getHeight());
    applicationList.requestFocus();
    txtSearch.setText("Search");
    txtSearchKeyReleased(null);

}//GEN-LAST:event_lblSearchMousePressed

private void lblSearchMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSearchMouseEntered
    lblSearch.setIcon(searchArrow);
}//GEN-LAST:event_lblSearchMouseEntered

private void lblSearchMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSearchMouseExited
    lblSearch.setIcon(searchArrowDisabled);
}//GEN-LAST:event_lblSearchMouseExited

private void txtSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSearchFocusGained
    txtSearch.setText("");
    txtSearch.setForeground(Main.theme.getSearchForegroundColor());
    updateList();
}//GEN-LAST:event_txtSearchFocusGained

private void txtSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSearchFocusLost
    if (txtSearch.getText().equals("")) {
        txtSearch.setText("Search");
    }
    txtSearch.setForeground(Main.theme.getSearchInactiveColor());
}//GEN-LAST:event_txtSearchFocusLost

private void applicationListMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_applicationListMouseEntered
}//GEN-LAST:event_applicationListMouseEntered

private void mnuGettingStartedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGettingStartedActionPerformed
    GettingStarted h = new GettingStarted(this, true);
    h.setVisible(true);
}//GEN-LAST:event_mnuGettingStartedActionPerformed

private void mnuImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportActionPerformed
    String file = HelperClass.showFileChooser(this, "Select file",
            new FileChooserFilter(FileChooserFilter.EXTENSIONS, new String[]{".dat"},
            "D-Box Game Libraries (*.dat)"), false);

    if (file != null) {
        deSerialize(file);
        updateList();
    }
}//GEN-LAST:event_mnuImportActionPerformed

private void mnuExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportActionPerformed
    String file = HelperClass.showFileChooser(this, "Select file",
            new FileChooserFilter(FileChooserFilter.EXTENSIONS, new String[]{".dat"},
            "D-Box Game Libraries (*.dat)"), false);
    if (file != null) {
        if (file.indexOf(".") == -1) {
            file = file + ".dat";
        }
        writeApplicationDatabase(file);
    }
}//GEN-LAST:event_mnuExportActionPerformed

private void mnuClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuClearActionPerformed
    int s = JOptionPane.showConfirmDialog(this, "You are you sure you want to remove all games from the game list?",
            "Clear game list", JOptionPane.YES_NO_OPTION);
    if (s != JOptionPane.YES_OPTION) {
        return;
    }
    bl = new ApplicationList();
    updateList();
}//GEN-LAST:event_mnuClearActionPerformed

private void mnuListFavoriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListFavoriteActionPerformed
    bl.getGame(applicationList.getSelectedValue().toString()).setStar(!bl.getGame(applicationList.getSelectedValue().toString()).isStar());
}//GEN-LAST:event_mnuListFavoriteActionPerformed

private void mnuListRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListRunActionPerformed
    mnuRunActionPerformed(evt);
}//GEN-LAST:event_mnuListRunActionPerformed

private void mnuListSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListSetupActionPerformed
    mnuSetupActionPerformed(evt);
}//GEN-LAST:event_mnuListSetupActionPerformed

private void mnuListEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListEditActionPerformed
    mnuEditActionPerformed(evt);
}//GEN-LAST:event_mnuListEditActionPerformed

private void mnuListRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListRemoveActionPerformed
    mnuDeleteActionPerformed(evt);
}//GEN-LAST:event_mnuListRemoveActionPerformed

private void mnuViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuViewActionPerformed
    toggleView();
}//GEN-LAST:event_mnuViewActionPerformed

private void mnuQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuQuitActionPerformed
    this.dispose();
}//GEN-LAST:event_mnuQuitActionPerformed

private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
}//GEN-LAST:event_txtSearchActionPerformed

private void panelControlsMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelControlsMouseDragged
    if (evt.getButton() == MouseEvent.BUTTON1) {
        Point p = getLocation();
        setLocation(p.x + evt.getX() - point.x, p.y + evt.getY() - point.y);
    }
}//GEN-LAST:event_panelControlsMouseDragged

private void panelControlsMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelControlsMouseMoved
}//GEN-LAST:event_panelControlsMouseMoved

private void panelControlsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelControlsMouseClicked
}//GEN-LAST:event_panelControlsMouseClicked

private void panelControlsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelControlsMousePressed
    if (evt.getClickCount() == 2) {
        if (getExtendedState() != MAXIMIZED_BOTH) {
            this.setExtendedState(MainWindow.MAXIMIZED_BOTH);
        } else {
            setExtendedState(NORMAL);
        }
    }

    point.x = evt.getX();
    point.y = evt.getY();
}//GEN-LAST:event_panelControlsMousePressed

private void mnuShowScreenShotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuShowScreenShotActionPerformed
    showScreenCaptures();
}//GEN-LAST:event_mnuShowScreenShotActionPerformed

private void mnuCheckNewestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCheckNewestActionPerformed
    Updater.CheckForUpdate(true);
}//GEN-LAST:event_mnuCheckNewestActionPerformed

private void mnuViewScreenShotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuViewScreenShotActionPerformed
    mnuShowScreenShotActionPerformed(evt);
}//GEN-LAST:event_mnuViewScreenShotActionPerformed

private void mnuHelpAdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHelpAdActionPerformed
    BrowserControl.openUrl("http://code.google.com/p/dbox/wiki/AddApplications");
}//GEN-LAST:event_mnuHelpAdActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JList applicationList;
    private javax.swing.JPopupMenu editMenu;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JMenuItem mnuCheckNewest;
    private javax.swing.JMenuItem mnuClear;
    private javax.swing.JMenuItem mnuDelete2;
    private javax.swing.JMenuItem mnuDosbox;
    private javax.swing.JMenuItem mnuEdit2;
    private javax.swing.JMenuItem mnuExport;
    private javax.swing.JMenuItem mnuGettingStarted;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuHelpAd;
    private javax.swing.JMenuItem mnuHelpScreen;
    private javax.swing.JMenuItem mnuHome;
    private javax.swing.JMenuItem mnuImport;
    private javax.swing.JMenuItem mnuListEdit;
    private javax.swing.JCheckBoxMenuItem mnuListFavorite;
    private javax.swing.JMenuItem mnuListRemove;
    private javax.swing.JMenuItem mnuListRun;
    private javax.swing.JMenu mnuListSetGenre;
    private javax.swing.JMenuItem mnuListSetup;
    private javax.swing.JMenuItem mnuNew2;
    private javax.swing.JMenuItem mnuPreferences;
    private javax.swing.JMenuItem mnuQuit;
    javax.swing.JMenuItem mnuRun2;
    private javax.swing.JMenuItem mnuSetup2;
    private javax.swing.JMenuItem mnuShowScreenShot;
    private javax.swing.JMenu mnuTools;
    private javax.swing.JMenuItem mnuView;
    private javax.swing.JMenuItem mnuViewScreenShot;
    private javax.swing.JMenu mnuWeb;
    private javax.swing.JPanel panelControls;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JPopupMenu prefMenu;
    private javax.swing.JPopupMenu runMenu;
    private javax.swing.JPopupMenu searchMenu;
    public javax.swing.JTextField txtSearch;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public void focusGained(FocusEvent e) {
        if (this.getFocusOwner() == this) {
            applicationList.requestFocus();
        }
    }

    public void focusLost(FocusEvent e) {
    }
}

class Filter implements ActionListener {

    MainWindow mw;

    Filter(MainWindow mw) {
        this.mw = mw;
    }

    public void actionPerformed(ActionEvent e) {
        mw.txtSearch.setText(((JMenuItem) e.getSource()).getText());
        String s = ((JMenuItem) e.getSource()).getText();
        if (s.equals("Favorites")) {
            mw.updateListFavorite();
        } else {
            mw.updateListGenre(s);
        }

        mw.applicationList.requestFocus();
    }
}

class SetGenre implements ActionListener {

    MainWindow mw;
    String genre;

    SetGenre(MainWindow mw, String genre) {
        this.mw = mw;
        this.genre = genre;
    }

    public void actionPerformed(ActionEvent e) {
        ApplicationBean di = MainWindow.bl.getGame(mw.applicationList.getSelectedValue().toString());
        di.setGenre(genre);

        mw.applicationList.requestFocus();
    }
}
