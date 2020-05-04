package nl.ikarus.nxt.priv.imageio.icoreader.obj;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.spi.IIORegistry;
import java.util.*;
import java.nio.*;
import nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOReader;

public class BitmapImageIO extends Bitmap {
  public BitmapImageIO(IconEntry pEntry) throws IOException {
    super(pEntry);
  }

  private final boolean DEBUG = Boolean.valueOf(System.getProperty(ICOReader.PROPERTY_NAME_PREFIX+"debug",Boolean.toString(false)));
  protected BufferedImage createImage()  throws IOException {

//    com.sun.imageio.plugins.bmp.BMPImageReaderSpi x;


    ByteArrayOutputStream tmpout = new ByteArrayOutputStream();
    //write bmp fileheader
    ImageOutputStream out = new MemoryCacheImageOutputStream(tmpout);
    out.setByteOrder(ByteOrder.LITTLE_ENDIAN);

//    DataOutputStream out=new DataOutputStream(tmpout);
    //magic bytes
    out.writeByte(0x42);
    out.writeByte(0x4d);
  //  System.out.println("Convert: " + entry);


    //filesize (4bytes)
//    System.out.println("ImageData: " + entry.getImageData().length);
    out.writeInt((entry.getImageData().length) + 14);
    //2 reserved fields (4 bytes total)
    out.writeShort(0x0);
    out.writeShort(0x0);
    //bitmap offset (4 bytes) -> should point to the data
    if (false && this.biBitCount <= 8) {
      //skip color table
      int coltablelength = (int) Math.pow(2,this.biBitCount);
      out.writeInt(14 + 40 + coltablelength);
    } else {
      out.writeInt(14 + 40);
    }
//    System.out.println("new offset: " +(14 + _bytesInHeader));
  //  System.out.println("calc: " +((14 + _bytesInHeader) - 14 - entry.getBytesInResource()));
//    this.biSizeImage = (entry.getImageData().length - biSize);
//    out.flush();
 //   tmpout.write(entry.getImageData());


    //start official bmp header
    out.writeInt(biSize); //size needed for struct

    out.writeInt(entry.getWidth());
    out.writeInt(entry.getHeight());
    out.writeShort(biPlanes);
    out.writeShort(biBitCount);
    out.writeInt(biCompression);
     out.writeInt(biSizeImage);
    //out.writeInt((entry.getImageData().length - biSize)); //biSizeImage
    out.writeInt(biXPelsPerMeter);
    out.writeInt(biYPelsPerMeter);
    out.writeInt(biColorsUsed);
    out.writeInt(biColorsImportant);


    out.write(entry.getImageData(),biSize, entry.getImageData().length - biSize);

 //       out.write(entry.getImageData());

    out.flush();
out.close();

    ByteArrayInputStream in = new ByteArrayInputStream(tmpout.toByteArray());
    ImageInputStream input = ImageIO.createImageInputStream(in);
    BufferedImage res=null;
    // System.out.println("BitmapImageIO: started...");
    IOException exToThrow = null;
    Iterator<ImageReader> it  =ImageIO.getImageReaders(input);
    Class bmpimagereaderClass=null;
    try {
      bmpimagereaderClass = Class.forName("com.sun.imageio.plugins.bmp.BMPImageReader");
    } catch (ClassNotFoundException ex1) {
    }
    while(it.hasNext()) {
      boolean isBMPImageReader=false;
      try {
        ImageReader r = it.next();
        isBMPImageReader = (r.getClass() == bmpimagereaderClass);
        if (DEBUG) System.out.println("BitmapImageIO: Trying reader: " + r.getClass().getName());
        r.setInput(input);

        res = r.read(0);
        break;
      } catch (IOException ex) {
        res = null;
        if (isBMPImageReader) exToThrow = ex;
        in = new ByteArrayInputStream(tmpout.toByteArray());
        input = ImageIO.createImageInputStream(in);
      }
    }
    if (exToThrow != null && res == null) throw exToThrow;
//    com.sun.imageio.plugins.bmp.BMPImageReader rr=null;
    return res;
  }


}
