package no.truben.dbox.util;

import java.io.File;

/**
 * Filter class for swing filechooser
 *
 * @author pederskeidsvoll
 */
public class FileChooserFilter extends javax.swing.filechooser.FileFilter {

    public final static int NO_FILTER   = 0;
    public final static int DIRECTORIES = 1;
    public final static int EXTENSIONS  = 2;
    public final static int STARTS_WITH = 3;


    private int mode;
    private String[] searchfor;
    private String description;


    public FileChooserFilter(int mode, String[] searchfor, String description) {
        assert mode >= 0 && mode <= 3 : "mode should always be between 0 and 3";
        this.mode = mode;
        this.searchfor = searchfor;
        this.description = description;
    }


    @Override
    public boolean accept(File file) {
        if(file.isDirectory())
                    return true;

        String filename = file.getName().toLowerCase();

        switch(mode) {
            case NO_FILTER:
                return true;
            case DIRECTORIES:
                if(file.isDirectory())
                    return true;
            case EXTENSIONS:
                for(String s : searchfor)
                    if(filename.endsWith(s))
                        return true;
            case STARTS_WITH:
                for(String s : searchfor)
                    if(filename.startsWith(s))
                        return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
