/*
 * BoxListe.java
 *
 * Created on 8. juni 2007, 01:18
 *
 * @author Truben
 */

package no.truben.dbox.model;

import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

public class ApplicationList implements Serializable {
    
    private final List<ApplicationBean> gamelist;
    
    /** Creates a new instance of BoxListe */
    public ApplicationList() {
        gamelist = new ArrayList<ApplicationBean>();
    }
    
    /**
     * Searches through the list of games, and tries to find the game.
     * The name is case insensitive.
     *
     * @param name The name of the game
     * @return The name of the game, null if no game is found
     **/
    public ApplicationBean getGame(String name) {
        if(name == null)
            return null;
        
        name = name.toLowerCase();
        for(ApplicationBean item : gamelist) {
            if(item.getName().toLowerCase().equals(name)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Removes the game specified by the name
     * @param name The name of the game we want to remove
     * @return The game that we removed
     **/
    public ApplicationBean removeGame(String name) {
        name = name.toLowerCase();
        for(ApplicationBean item : gamelist) {
            if(item.getName().toLowerCase().equals(name)) {
                gamelist.remove(item);
                return item;
            }
        }
        return null;
    }

    public  void clearList() {
        gamelist.clear();
    }
    
    /**
     * Adds the game to the list
     * @param di The game we want to add
     **/
    public void addGame(ApplicationBean di) {
        gamelist.add(di);
    }
    
    /**
     * Get a list of game titles.
     * @return List of game titles in the db
     **/
    public String[] getGameList() {
        String[] temp = new String[gamelist.size()];
        int count=0;
        for(ApplicationBean item : gamelist) {
            temp[count++] = item.getName();
        }
        Arrays.sort(temp);
        return temp;
    }
    
    /**
     * Get a list of game icons.
     * @return List of game titles in the db
     **/
    public String[] getIconList() {
        String[] temp = new String[gamelist.size()];
        int count=0;
        for(ApplicationBean item : gamelist) {
            temp[count++] = item.getIcon();
        }
        return temp;
    }
    
    
    /**
     * Get a list of game titles. Separated by commas.
     * @param searchstr game name must include this
     * @return List of game titles in the db
     **/
    public String[] getGameList(String searchstr) {
        int count=0;
        int counter=0;
        searchstr=searchstr.toLowerCase();
        
        for(ApplicationBean item : gamelist) {
            if(item.getName().toLowerCase().indexOf(searchstr) != -1 || 
               item.getKeywords().toLowerCase().indexOf(searchstr) != -1 ||
               item.getGenre().toLowerCase().indexOf(searchstr) != -1  ||
               item.getYear().toLowerCase().indexOf(searchstr) != -1  ||
               item.getDeveloper().toLowerCase().indexOf(searchstr) != -1  ||
               item.getPublisher().toLowerCase().indexOf(searchstr) != -1)
                
                count++;
        }
        
        String[] temp = new String[count];
            
        for(ApplicationBean item : gamelist) {
            if(item.getName().toLowerCase().indexOf(searchstr) != -1 ||
               item.getKeywords().toLowerCase().indexOf(searchstr) != -1 ||
               item.getGenre().toLowerCase().indexOf(searchstr) != -1  ||
               item.getYear().toLowerCase().indexOf(searchstr) != -1  ||
               item.getDeveloper().toLowerCase().indexOf(searchstr) != -1  ||
               item.getPublisher().toLowerCase().indexOf(searchstr) != -1) {
                temp[counter++] = item.getName();
            }
        }
        Arrays.sort(temp);
        return temp;
    }

    public String[] getFavoriteGameList() {
        int count=0;
        int counter=0;

        for(ApplicationBean item : gamelist)
            if(item.isStar())
                count++;

        String[] temp = new String[count];

        for(ApplicationBean item : gamelist) {
            if(item.isStar())
                temp[counter++] = item.getName();
        }
        Arrays.sort(temp);
        return temp;
    }

    /**
     * Get a list of game titles.
     * @param searchstr the genre
     * @return List of game titles in the db
     **/
    public String[] getGameListGenre(String searchstr) {

        if(searchstr.equals("")) {
            return getGameList();
        }

        int count=0;
        int counter=0;
        searchstr=searchstr.toLowerCase();

        for(ApplicationBean item : gamelist)
            if(item.getGenre().toLowerCase().equals(searchstr.toLowerCase()))
                count++;

        String[] temp = new String[count];

        for(ApplicationBean item : gamelist) {
            if(item.getGenre().toLowerCase().equals(searchstr.toLowerCase())) {
                temp[counter++] = item.getName();
            }
        }
        Arrays.sort(temp);
        return temp;
    }
    
    /**
     * Find out how many games we have in our db
     * 
     * @return Number of games
     **/
    public int getNrGames() {
        return gamelist.size();
    }

    public String toConfigString() {
        String out = "## D-Box Game file. Do not edit if you don't know what you're doing! ##\n\n";
        for (ApplicationBean dosItem : gamelist) {
            out += dosItem.toConfigString();
        }
        return out;
    }

    public void readConfig(String config) {
        long times = System.currentTimeMillis();
        Scanner s = new Scanner(config);
        ApplicationBean d = null;
        int counter = 0;
        boolean isInGame = false;
        while(s.hasNextLine()) {
            counter++;
            String linje = s.nextLine().trim();

            if(linje.equals("start game")) {
                d = new ApplicationBean();
                isInGame = true;
                continue;
            }
            else if(linje.equals("end game")) {
                if(d == null) {
                    System.out.println("Error in gamefile! No 'start game' before 'end game': " + counter);
                    continue;
                }
                gamelist.add(d);
                isInGame = false;
                continue;
            }
            else if(isInGame) {
                try {
                    // remove comments Removed... to slow
                    //int start = linje.indexOf("#");

                    //if(start != -1)
                    //    linje = linje.substring(0, start);

                    int start = linje.indexOf(":=");

                    final String keyword;
                    final String value;

                    if(start != -1) { 
                        keyword = linje.substring(0, start).trim();
                        value = linje.substring(start + 2).trim();
                    }
                    else
                        continue;

                    if(keyword.equals("genre"))
                        d.setGenre(value);
                    else if(keyword.equals("name"))
                        d.setName(value);
                    else if(keyword.equals("path"))
                        d.setPath(value);
                    else if(keyword.equals("year"))
                        d.setYear(value);
                    else if(keyword.equals("developer"))
                        d.setDeveloper(value);
                    else if(keyword.equals("publisher"))
                        d.setPublisher(value);
                    else if(keyword.equals("game"))
                        d.setGame(value);
                    else if(keyword.equals("keywords"))
                        d.setKeywords(value);
                    else if(keyword.equals("installer"))
                        d.setInstaller(value);
                    else if(keyword.equals("floppy"))
                        d.setFloppy(value);
                    else if(keyword.equals("cdrom"))
                        d.setCdrom(value);
                    else if(keyword.equals("cdromlabel"))
                        d.setCdromLabel(value);
                    else if(keyword.equals("uniqueid"))
                        d.setUniqueID(value);
                    else if(keyword.equals("cdromletter"))
                        d.setUniqueID(value); 
                    else if(keyword.equals("icon"))
                        d.setIcon(value);
                    else if(keyword.equals("extra"))
                        d.setExtra(value);
                    else if(keyword.equals("cycles"))
                        d.setCycles(Integer.parseInt(value));
                    else if(keyword.equals("frameskip"))
                        d.setFrameskip(Integer.parseInt(value));
                    else if(keyword.equals("favorite"))
                        d.setStar(Boolean.parseBoolean(value));
                }
                catch(Exception e) {
                    int answer = JOptionPane.showConfirmDialog(null, "Something is wrong with the game list! Have you edited it?\n\nLine " +
                         counter + ": \n" + linje + "\n\nI can continue, but beware that I will overwrite the value with something " +
                         "legal. I can also quit so you can edit the file back to correct condition.\n\nDo you want me to quit?","Error" +
                         " in game file",JOptionPane.YES_NO_OPTION);
                    if(answer == JOptionPane.YES_OPTION)
                        System.exit(0);
                }
            }
        }
        System.out.println("Gamelist read in " + (System.currentTimeMillis() - times) + " milliseconds.");
    }
}
