package no.truben.dbox.GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author truben
 */
public class PanelImage extends JPanel {

    private Image backgroundImage;

    public PanelImage(URL url) {
      try {
         this.backgroundImage = ImageIO.read(url);
      }
      catch(Exception e) {
         throw new RuntimeException(e);
      }
  }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
    

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(backgroundImage,0,0,null);
    }
    

}
