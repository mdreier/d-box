package nl.ikarus.nxt.priv.imageio.icoreader.obj;

/**
 * ICOReader (ImageIO compatible class for reading ico files)
 * Copyright (C) 2005 J.B. van der Burgh
 * contact me at: icoreader (at) vdburgh.tmfweb.nl
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
import java.io.*;

import java.awt.image.*;

public abstract class RgbBitmap extends Bitmap {
  protected int amountOfBytesToRead = 3;
  protected RgbBitmap(IconEntry pEntry, int pAmountOfBytesToRead) throws IOException {
    this(pEntry);
    this.amountOfBytesToRead = pAmountOfBytesToRead;
  }

  protected RgbBitmap(IconEntry pEntry) throws IOException {
    super(pEntry);
  }

  /**
   * createImage
   *
   * @return BufferedImage
   */
  protected BufferedImage createImage() throws IOException {
    int w = entry.getHeight();
    int h = entry.getWidth();
    //boolean debuginf = amountOfBytesToRead > 3 && w == 32;
    int[] pixeldata = new int[h * w];
    for (int rijNr = 0; rijNr < w; rijNr++) {
      byte[] rij = reader.readBytes(h * amountOfBytesToRead);
      int rByte = 0;
      int oPos = (w - rijNr - 1) * h;
      for (int colNr = 0; colNr < h; colNr++) {
        // BGR -> RGB
        int pos = oPos++;
/*        if (false && amountOfBytesToRead > 2) {
          int r = rij[rByte + 2] & 0xFF;
          int g = rij[rByte + 1] & 0xFF;
          int b = rij[rByte] & 0xFF;

          java.awt.Color c;
          if (amountOfBytesToRead > 3) {
            int a = rij[rByte + 3] & 0xFF;
            c = new java.awt.Color(r, g, b, a);
          } else {
            c = new java.awt.Color(r, g, b, 255);
          }
          pixeldata[pos] = c.getRGB();
          rByte += amountOfBytesToRead;
        } else {
 */
          //added &0xFF to every byte read... this seems to solve all the trouble
          //I had with the 32bit icons
          pixeldata[pos] =
              (rij[rByte++] & 0xFF); //8bit pixel data (blue)
          if (amountOfBytesToRead > 1) //16bit pixel data
            pixeldata[pos] += ( (rij[rByte++] & 0xFF) << 8); //greeen
          else
            pixeldata[pos] += (0 << 8);
          if (amountOfBytesToRead > 2) //24bit pixel data
            pixeldata[pos] += ( (rij[rByte++] & 0xFF) << 16); //red
          else
            pixeldata[pos] += (0 << 16);

          if (amountOfBytesToRead > 3) //32bit alpha channel
            pixeldata[pos] += ( (rij[rByte++] & 0xFF) << 24);
          // else if (pixeldata[pos] == 0)
          //   pixeldata[pos] += (0 << 24); //<!-- bugfix if the alphamask is not present, it should be set to transparent, not to fully visible. note: this is not the way to do it, now all black pixels become transparent
          else
            pixeldata[pos] += ( (255) << 24);
        }
      //}
    }
    BufferedImage bIm;
    // if (amountOfBytesToRead>3){
    bIm = new BufferedImage(entry.getWidth(), entry.getHeight(), BufferedImage.TYPE_INT_ARGB);
    // }  else{
    //  bIm = new BufferedImage(entry.getWidth(), entry.getHeight(), BufferedImage.TYPE_INT_RGB);
    //}

    bIm.setRGB(0, 0, h, w, pixeldata, 0, h);
    return bIm;
  }

}
