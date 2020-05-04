package no.truben.dbox.util.onlineresource;

import no.truben.dbox.util.onlineresource.OnlineResource;
import no.truben.dbox.model.ApplicationBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author truben
 */
public class TrubenResource implements OnlineResource {

    private final String SEARCH_URL = "http://dbox.truben.no/GameDB/search.php?query=";

    private String name = "";
    private String publisher = "";
    private String developer = "";
    private String genre = "";
    private String year = "";
    private boolean valid = false;

    public TrubenResource(String hash) {
        try {
            URL updateURL = new URL(SEARCH_URL + hash);
            BufferedReader in = new BufferedReader(new InputStreamReader(updateURL.openStream()));
            while(true) {
                String line = in.readLine();
                if(line == null)
                    break;
                if(line.equals("No entry found"))
                    break;
                else {
                    String[] segmenst = line.split(":=");

                    valid = true;

                    if(segmenst.length != 2)
                        break;
                    else if(segmenst[0].trim().equals("name"))
                        name = segmenst[1].trim();
                    else if(segmenst[0].trim().equals("year"))
                        year = segmenst[1].trim();
                    else if(segmenst[0].trim().equals("developer"))
                        developer = segmenst[1].trim();
                    else if(segmenst[0].trim().equals("genre"))
                        genre = segmenst[1].trim();
                    else if(segmenst[0].trim().equals("publisher"))
                        publisher = segmenst[1].trim();

                }

            }
        } catch (IOException ex) {
            Logger.getLogger(TrubenResource.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static String getMD5(String filename) {
        {
            InputStream is = null;
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                File f = new File(filename);
                is = new FileInputStream(f);
                byte[] buffer = new byte[8192];
                int read = 0;
                try {
                    while ((read = is.read(buffer)) > 0) {
                        digest.update(buffer, 0, read);
                    }
                    byte[] md5sum = digest.digest();
                    BigInteger bigInt = new BigInteger(1, md5sum);
                    String ret = bigInt.toString(16);
                    if(ret.length() == 31)
                        ret = "0" + ret;
                    if(ret.length() == 30)
                        ret = "00" + ret;
                    if(ret.length() == 29)
                        ret = "000" + ret;
                    return ret;
                } catch (IOException e) {
                    throw new RuntimeException("Unable to process file for MD5", e);
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TrubenResource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(TrubenResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(TrubenResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return "";
        }

    }

    @Override
    public ApplicationBean fillInInformation(ApplicationBean di) {
        if(this.isValid()) {
            di.setGenre(genre);
            di.setDeveloper(developer);
            di.setPublisher(publisher);
            di.setName(name);
            di.setYear(year);
        }
        return di;

    }



    @Override
    public String getDeveloper() {
        return developer;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPublisher() {
        return publisher;
    }

    @Override
    public String getYear() {
        return year;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

}
