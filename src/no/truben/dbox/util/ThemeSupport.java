package no.truben.dbox.util;

import no.truben.dbox.Main;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author truben
 */
public class ThemeSupport {
    
    private String name   = "Default theme";
    private String author = "Truben";
    private String url    = "http://www.truben.no";

    private URL playInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "media-playback-start-disabled.png");
    private URL playActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "media-playback-start.png");

    private URL editInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "utilities-terminal-disabled.png");
    private URL editActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "utilities-terminal.png");

    private URL toolsInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "emblem-system-disabled.png");
    private URL toolsActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "emblem-system.png");

    private URL searchInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "down-arrow-disabled.png");
    private URL searchActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "down-arrow.png");

    private URL gameFavoriteImage    = getClass().getResource(Constants.IMAGE_FOLDER + "emblem-favorite.png");
    private URL gameNotFavoriteImage = getClass().getResource(Constants.IMAGE_FOLDER + "emblem-notfavorite.png");

    private boolean showWindowDecoration = true;
    private boolean showBorders = true;

    private boolean unifiedToolbar = true;

    private URL backgroundImage = getClass().getResource(Constants.IMAGE_FOLDER + "bg.jpg");
    private int backgroundRepeat;
    private Color backgroundColor = java.awt.SystemColor.window;

    private Color gameBackgroundColor = java.awt.SystemColor.text;
    private Color gameForegroundColor = java.awt.SystemColor.textText;
    private Color gameSelectedBackgroundColor = java.awt.SystemColor.textHighlight; // TODO: Get the real color
    private Color gameSelectedForegroundColor = java.awt.SystemColor.textHighlightText;

    private URL defaultGame = null;

    private Color searchBackgroundColor = java.awt.SystemColor.text;
    private Color searchForegroundColor = java.awt.SystemColor.textText;
    private Color searchInactiveColor = java.awt.SystemColor.textInactiveText;


    private int numberOfSettings = 0;

    public boolean success = true;



    private String filePath;

    @Override
    public String toString() {
        return "Theme Info\n==========\nName: " + name + "\nAuthor: " + author + "\nWeb: " + url + "\nNumber of settings: " + numberOfSettings;
    }

    private void fixIfMac() {
        if(HelperClass.getOS() == HelperClass.MACOS) {
            //backgroundRepeat = 0;
            playInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "mac/play.png");
            playActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "mac/play_select.png");

            editInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "mac/edit.png");
            editActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "mac/edit_select.png");

            toolsInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "mac/system.png");
            toolsActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "mac/system_select.png");

            searchInactiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "down-arrow-disabled.png");
            searchActiveImage = getClass().getResource(Constants.IMAGE_FOLDER + "down-arrow.png");

            gameFavoriteImage    = getClass().getResource(Constants.IMAGE_FOLDER + "mac/favorite.png");
            gameNotFavoriteImage = getClass().getResource(Constants.IMAGE_FOLDER + "emblem-notfavorite.png");

            //backgroundRepeat = 0;
        }
        else
            backgroundRepeat = 170;


    }



    public ThemeSupport() {
        fixIfMac();
    }
    
    

    public ThemeSupport(File f) {
        fixIfMac();
        String lines ="";
        try {
            Scanner s = new Scanner(f);
            filePath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator)+1);
            while(s.hasNextLine()) {
                lines = s.nextLine();
                String[] line = parseLine(lines);
                if(line == null)
                    return; // if there are errors in the file, just quit
                else if(line.length == 0)
                    continue; // empty line
                else {
                    setSetting(line);
                    numberOfSettings++;
                }

            }
        } catch (FileNotFoundException ex) {
            Messages.showErrorMessage("Not able to read theme file.\n" + f.getAbsolutePath());
        }
        catch (MalformedURLException ex) {
            Messages.showErrorMessage("Bad filepath:\n" + lines);
        }
        catch (NumberFormatException ex) {
            Messages.showErrorMessage("Problem interpreting number in line:\n" + lines);
        }
        catch (Exception ex) {
            Messages.showErrorMessage("Bad line:\n" + lines);
        }


    }

    public ThemeSupport(File f, boolean silent) {
        fixIfMac();
        String lines ="";
        success = false;
        try {
            Scanner s = new Scanner(f);
            filePath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator)+1);
            while(s.hasNextLine()) {
                lines = s.nextLine();
                String[] line = parseLine(lines);
                if(line == null)
                    return; // if there are errors in the file, just quit
                else if(line.length == 0)
                    continue; // empty line
                else {
                    setSetting(line);
                    numberOfSettings++;
                }

            }
            success = true;
        } catch (FileNotFoundException ex) {
        }
        catch (MalformedURLException ex) {
        }
        catch (NumberFormatException ex) {
        }
        catch (Exception ex) {
        }


    }

    private String[] parseLine(String line) {
        // Remove comments
        if(line.indexOf("//") != -1) {
            line = line.substring(0,line.indexOf("//"));
        }

        line = line.trim();
        
        // Check if the line is empty
        if(line.equals(""))
            return new String[0];

        // Parse
        String[] parts = line.split(":=");

        // Check if we have the correct number of substrings
        if(parts.length != 2) {
            Messages.showErrorMessage("Error parsing theme. Check this line:\n\n" + line);
            return null;
        }

        // remove spaces
        parts[0] = parts[0].trim().toLowerCase();
        parts[1] = parts[1].trim();

        return parts;

    }

    private void setSetting(String[] s) throws MalformedURLException,NumberFormatException {
        if      (s[0].equals("name"))
            name = s[1];
        else if (s[0].equals("author"))
            author = s[1];
        else if (s[0].equals("url"))
            url = s[1];
        else if (s[0].equals("header-background-image"))
            backgroundImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-background-repeat"))
            backgroundRepeat = Integer.parseInt(s[1]);
        else if (s[0].equals("window-background-color"))
            backgroundColor = decodeColor(s[1]);
        else if (s[0].equals("gamelist-background-color"))
            gameBackgroundColor = decodeColor(s[1]);
        else if (s[0].equals("gamelist-foreground-color"))
            gameForegroundColor = decodeColor(s[1]);
        else if (s[0].equals("gamelist-selected-background-color"))
            gameSelectedBackgroundColor = decodeColor(s[1]);
        else if (s[0].equals("gamelist-selected-foreground-color"))
            gameSelectedForegroundColor = decodeColor(s[1]);
        else if (s[0].equals("gamelist-dafaultgame-image"))
            defaultGame = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("gamelist-favorite-image"))
            gameFavoriteImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("gamelist-notfavorite-image"))
            gameNotFavoriteImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("search-background-color"))
            searchBackgroundColor = decodeColor(s[1]);
        else if (s[0].equals("search-foreground-color"))
            searchForegroundColor = decodeColor(s[1]);
        else if (s[0].equals("search-inactive-color"))
            searchInactiveColor = decodeColor(s[1]);
        else if (s[0].equals("header-play-active-image"))
            playActiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-play-inactive-image"))
            playInactiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-edit-active-image"))
            editActiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-edit-inactive-image"))
            editInactiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-tools-active-image"))
            toolsActiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-tools-inactive-image"))
            toolsInactiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-search-active-image"))
            searchActiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("header-search-inactive-image"))
            searchInactiveImage = new File(filePath + s[1]).toURI().toURL();
        else if (s[0].equals("show-borders"))
            showBorders = Boolean.parseBoolean(s[1]);
        else if (s[0].equals("show-window-border"))
            showWindowDecoration = Boolean.parseBoolean(s[1]);
        else if (s[0].equals("show-unified-toolbar"))
            unifiedToolbar = Boolean.parseBoolean(s[1]);

    }

    private Color decodeColor(String s) {
        if(s.length() == 0)
            return Color.black;
        if(s.charAt(0) != '#')
            return Color.decode("#" + s);
        return Color.decode(s);
    }

    public String getAuthor() {
        return author;
    }

    public URL getBackgroundImage() {
        return backgroundImage;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getBackgroundRepeat() {
        return backgroundRepeat;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getGameBackgroundColor() {
        return gameBackgroundColor;
    }

    public Color getSearchBackgroundColor() {
        return searchBackgroundColor;
    }

    public Color getGameForegroundColor() {
        return gameForegroundColor;
    }

    public Color getSearchForegroundColor() {
        return searchForegroundColor;
    }

    public Color getSearchInactiveColor() {
        return searchInactiveColor;
    }

    public Color getGameSelectedBackgroundColor() {
        return gameSelectedBackgroundColor;
    }

    public Color getGameSelectedForegroundColor() {
        return gameSelectedForegroundColor;
    }

    public URL getPlayActiveImage() {
        return playActiveImage;
    }

    public URL getPlayInactiveImage() {
        return playInactiveImage;
    }

    public URL getEditActiveImage() {
        return editActiveImage;
    }

    public URL getEditInactiveImage() {
        return editInactiveImage;
    }

    public URL getSearchActiveImage() {
        return searchActiveImage;
    }

    public URL getSearchInactiveImage() {
        return searchInactiveImage;
    }

    public URL getToolsActiveImage() {
        return toolsActiveImage;
    }

    public URL getToolsInactiveImage() {
        return toolsInactiveImage;
    }

    public boolean isShowBorders() {
        return showBorders;
    }

    public boolean isShowWindowDecoration() {
        return showWindowDecoration;
    }

    public URL getDefaultGame() {

        if(defaultGame == null) {
            int width = Main.pref.getIconWidth();
            int height = Main.pref.getIconHeight();
            String ext = "";
            if(width <=16)
                ext="-16";
            else if(width >= 17 && width <= 24)
                ext="-24";
            else if((double)width/(double)height == 1.6 && width <= 160)
                ext="-160x100";
            else if((double)width/(double)height == 1.6 && width >= 161)
                ext="-320x200";
            else
                ext="-32";
            return getClass().getResource(Constants.IMAGE_FOLDER + "gameIcons/application-x-executable" + ext + ".png");
        }
        else {
            return defaultGame;
        }
    }

    public URL getGameFavoriteImage() {
        return gameFavoriteImage;
    }

    public URL getGameNotFavoriteImage() {
        return gameNotFavoriteImage;
    }

    public boolean isUnifiedToolbar() {
        return unifiedToolbar;
    }

    



}
