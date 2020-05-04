/*
 * DosItem.java
 *
 * Created on 7. juni 2007, 15:47
 * @author Truben
 *
 */
package no.truben.dbox.model;

import no.truben.dbox.util.ImageHandlerer;
import java.io.Serializable;
import javax.swing.ImageIcon;
import no.truben.dbox.Main;

public class ApplicationBean implements Serializable {

    private String name = "";
    private String path = "";
    private String gamefile = "";
    private String installer = "";
    private String icon = "";
    private int cycles = 8000;
    private int frameskip = 0;
    private String cdrom = "";
    private String cdromLetter = "D";
    private String cdromLabel = "";
    private String floppy = "";
    private String extra = "";
    private String genre = Main.pref.getGenres()[Main.pref.getGenres().length - 1];
    private String keywords = "";
    private boolean star = false;
    private ImageIcon imageIcon = ImageHandlerer.getDefaultIcon();
    private int size = Main.pref.getIconHeight();
    private String uniqueID;
    private String year = "";
    private String developer = "";
    private String publisher = "";

    public String getCdromLetter() {
        return cdromLetter;
    }

    public void setCdromLetter(String cdromLetter) {
        this.cdromLetter = cdromLetter;
    }
    
    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getCdromLabel() {
        return cdromLabel;
    }

    public void setCdromLabel(String cdromLabel) {
        this.cdromLabel = cdromLabel;
    }

    public ImageIcon getImageIcon() {
        if(size != Main.pref.getIconHeight())
            imageIcon = ImageHandlerer.getDefaultIcon();
        return imageIcon;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public int getFrameskip() {
        return frameskip;
    }

    public void setFrameskip(int frameskip) {
        this.frameskip = frameskip;
    }

    public String getFloppy() {
        return floppy;
    }

    public void setFloppy(String floppy) {
        this.floppy = floppy;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    /** Creates a new instance of DosItem */
    public ApplicationBean() {
        // Generate unique id
        setUniqueID((int)(Math.random() * 1000000) + "");
    }

    public String getCdrom() {
        return cdrom;
    }

    public void setCdrom(String cdrom) {
        this.cdrom = cdrom;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public String getName() {
        return name;
    }

    public void setName(String nname) {
        name = nname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String name) {
        if (!this.icon.equals(name) || size != Main.pref.getIconHeight()) {
            this.icon = name;
            size = Main.pref.getIconHeight();
            this.imageIcon = ImageHandlerer.getImageIcon(name);
        }
    }

    public String toString() {
        return getName();
    }

    public String getGame() {
        return gamefile;
    }

    public void setGame(String game) {
        this.gamefile = game;
    }

    public String getInstaller() {
        return installer;
    }

    public void setInstaller(String installer) {
        this.installer = installer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getKeywords() {
        return keywords;
    }

    public String toConfigString() {
        return "start game" + "\n" +
                "  uniqueid := " + getUniqueID() + "\n" +
                "  name := " + getName() + "\n" +
                "  path := " + getPath() + "\n" +
                "  game := " + getGame() + "\n" +
                "  genre := " + getGenre() + "\n" +
                "  keywords := " + getKeywords() + "\n" +
                "  installer := " + getInstaller() + "\n" +
                "  floppy := " + getFloppy() + "\n" +
                "  cdrom := " + getCdrom() + "\n" +
                "  cdromlabel := " + getCdromLabel() + "\n" +
                "  cdromletter := " + getCdromLetter() + "\n" +
                "  extra :=" + getExtra() + "\n" +
                "  icon := " + getIcon() + "\n" +
                "  cycles := " + getCycles() + "\n" +
                "  frameskip := " + getFrameskip() + "\n" +
                "  favorite := " + isStar() + "\n" +
                "  year := " + getYear() + "\n" +
                "  developer := " + getDeveloper() + "\n" +
                "  publisher := " + getPublisher() + "\n" +
                "end game\n\n";
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public boolean equals(ApplicationBean d) {
        if (d.getCycles() != cycles) {
            return false;
        }
        if (!d.getGame().equals(gamefile)) {
            return false;
        }
        if (!d.getIcon().equals(icon)) {
            return false;
        }
        if (!d.getPath().equals(path)) {
            return false;
        }
        if (!d.getCdrom().equals(cdrom)) {
            return false;
        }
        if (!d.getFloppy().equals(floppy)) {
            return false;
        }
        if (!d.getExtra().equals(extra)) {
            return false;
        }
        if (!d.getGenre().equals(genre)) {
            return false;
        }
        if (!d.getKeywords().equals(keywords)) {
            return false;
        }
        return true;
    }
}
