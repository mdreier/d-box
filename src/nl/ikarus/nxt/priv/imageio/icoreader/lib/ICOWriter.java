package nl.ikarus.nxt.priv.imageio.icoreader.lib;

import java.io.*;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.spi.*;
import com.sun.imageio.plugins.common.I18N;
import javax.imageio.stream.*;
import java.util.*;
import java.awt.image.*;
import java.nio.ByteOrder;
import nl.ikarus.nxt.priv.imageio.icoreader.obj.*;
/**
 *
 * @see: http://www.daubnet.com/formats/ICO.html
 * @see: http://www.daubnet.com/formats/BMP.html#BasicFileFormat
 *
 * <p>Copyright: Copyright (c) 2005 J.B. van der Burgh</p>
 * @author J.B. van der Burgh
 * @version 1.0
 */
public class ICOWriter extends ImageWriter {
  protected ICOWriter(ImageWriterSpi originatingProvider) {
    super(originatingProvider);
  }

  /**
   * Returns an <code>IIOMetadata</code> object that may be used for encoding
   * and optionally modified using its document interfaces or other interfaces
   * specific to the writer plug-in that will be used for encoding.
   *
   * @param inData an <code>IIOMetadata</code> object representing image
   *   metadata, used to initialize the state of the returned object.
   * @param imageType an <code>ImageTypeSpecifier</code> indicating the layout
   *   and color information of the image with which the metadata will be
   *   associated.
   * @param param an <code>ImageWriteParam</code> that will be used to encode
   *   the image, or <code>null</code>.
   * @return an <code>IIOMetadata</code> object, or <code>null</code> if the
   *   plug-in does not provide metadata encoding capabilities.
   * @todo Implement this javax.imageio.ImageTranscoder method
   */
  public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
    return null;
  }

  /**
   * Returns an <code>IIOMetadata</code> object that may be used for encoding
   * and optionally modified using its document interfaces or other interfaces
   * specific to the writer plug-in that will be used for encoding.
   *
   * @param inData an <code>IIOMetadata</code> object representing stream
   *   metadata, used to initialize the state of the returned object.
   * @param param an <code>ImageWriteParam</code> that will be used to encode
   *   the image, or <code>null</code>.
   * @return an <code>IIOMetadata</code> object, or <code>null</code> if the
   *   plug-in does not provide metadata encoding capabilities.
   * @todo Implement this javax.imageio.ImageTranscoder method
   */
  public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
    return null;
  }

  /**
   * Returns an <code>IIOMetadata</code> object containing default values for
   * encoding an image of the given type.
   *
   * @param imageType an <code>ImageTypeSpecifier</code> indicating the format
   *   of the image to be written later.
   * @param param an <code>ImageWriteParam</code> that will be used to encode
   *   the image, or <code>null</code>.
   * @return an <code>IIOMetadata</code> object.
   * @todo Implement this javax.imageio.ImageWriter method
   */
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
    return null;
  }

  /**
   * Returns an <code>IIOMetadata</code> object containing default values for
   * encoding a stream of images.
   *
   * @param param an <code>ImageWriteParam</code> that will be used to encode
   *   the image, or <code>null</code>.
   * @return an <code>IIOMetadata</code> object.
   * @todo Implement this javax.imageio.ImageWriter method
   */
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
    return null;
  }

  /**
   * Appends a complete image stream containing a single image and associated
   * stream and image metadata and thumbnails to the output.
   *
   * @param streamMetadata an <code>IIOMetadata</code> object representing
   *   stream metadata, or <code>null</code> to use default values.
   * @param image an <code>IIOImage</code> object containing an image,
   *   thumbnails, and metadata to be written.
   * @param param an <code>ImageWriteParam</code>, or <code>null</code> to use
   *   a default <code>ImageWriteParam</code>.
   * @throws IOException if an error occurs during writing.
   * @todo Implement this javax.imageio.ImageWriter method
   */
  public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
    ImageOutputStream stream = (ImageOutputStream)getOutput();
    if (stream == null) {
      throw new IllegalStateException("You must specify the output stream");
    }
    if (image == null) {
      throw new IllegalArgumentException("You must specify the image");
    }

    /** start icon writer stuff */
    //write
    ByteArrayOutputStream icon = new ByteArrayOutputStream(8192);
//    DataOutputStream out = new DataOutputStream(icon);
    ImageOutputStream out = ImageIO.createImageOutputStream(icon);
    out.setByteOrder(ByteOrder.LITTLE_ENDIAN);

    //write file header
    out.writeShort(0); //reserved
    out.writeShort(1); //1 = ico, 0 = bitmap
    out.writeShort(1); //amount of icons
    /** end icon writer stuff */


    /** start bmp stuff */
    byte[] bmpdata=null;
//    com.sun.imageio.plugins.bmp.BMPImageWriterSpi x;
      Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix("bmp");

      ByteArrayOutputStream bmpOut = new ByteArrayOutputStream(8192);
      while (it.hasNext()) {
        ImageWriter w = it.next();
        ImageOutputStream iosOut = ImageIO.createImageOutputStream(bmpOut);
        try {
          w.setOutput(iosOut);
          w.write(image);
          iosOut.flush();
        } catch (Exception ex) {
          System.err.println("Exception for writer " + w.getClass().getName());
          ex.printStackTrace();
          bmpOut = new ByteArrayOutputStream(8192);
        }
      }
      if (bmpOut.size() == 0) {
        throw new IllegalStateException("Unable to find an ImageIO Encoder that could encode this image to bmp, the ICOWriter depends on this...");
      }
      /*
      {
        FileInputStream fin = new FileInputStream("c:\\test-output-irfanview.bmp");
        bmpOut.reset();
        int b = 0;
        while((b = fin.read()) != -1) {
          bmpOut.write(b);
        }

      }*/

      bmpdata = bmpOut.toByteArray();
      bmpOut=null;
      /*{
        FileOutputStream tmpOut = new FileOutputStream("c:\\test-output.bmp", false);
        tmpOut.write(bmpdata);
        tmpOut.flush();
        tmpOut.close();
       }
*/
      //position 14 holds the header size -> should be 40 if I want to know how to read it
      //position 10 holds the offset
      DataInputStream tmpin2 = new DataInputStream(new ByteArrayInputStream(bmpdata));
      MyReader tmpin = new MyReader(tmpin2);

      tmpin.skip(10);

      int imageoffset=tmpin.readLONG();
      System.err.println("OFFSET = " + imageoffset + "   should I skip some bytes??");
//      tmpin.skipBytes(imageoffset);
//      tmpin.reset();
  //    tmpin.skip(imageoffset + 2 );

      int headersize=tmpin.readLONG();
      if (headersize < 40) {
        System.err.println("Excpected a header size of 40 but found: " + headersize+" ..... unable to parse the metadata");
      } else {
        if (headersize != 40) {
          System.err.println("Excpected a header size of 40 but found: " + headersize + " ..... but I'll try to parse the metadata anyway");
        }

        {

          int w = tmpin.readLONG();
          int h = tmpin.readLONG();
          int planes = tmpin.readWORD();
          int bcount = tmpin.readWORD();
          tmpin.readLONG(); //compression
          int sizeImg = tmpin.readLONG(); //sizeImage
          tmpin.readDWORD(); //x pels per meter
          tmpin.readDWORD(); //ypels per meter
          int colUsed = tmpin.readDWORD();
          tmpin.readDWORD(); //cols important

          //write ico header
          out.writeByte(w); //width
          out.writeByte(h); //height
          out.writeByte(colUsed); //colorcount
          out.writeByte(0); //reserved
          out.writeShort(planes); //planes
          out.writeShort(bcount); //bitcount
          out.writeInt(sizeImg); //bytesinresource
          //        out.writeInt(tmpin.readInt()); //image offset
          out.writeInt(22);//image offset
        }
        tmpin.reset();
        tmpin.skip(14);


        //write ico image
/*        byte[] buff = new byte[512];
        int len;
        while((len=tmpin.read(buff))!=-1)
          out.write(buff,0,len);*/

try {
    //START FIX HEIGHT
  {
    //header size
    int i = tmpin.readLONG();
//  ico[pos++] + ico[pos++] * 256 + ico[pos++] * 256 * 256 + ico[pos++] * 256 * 256 * 256;
    byte[] test = new byte[4];
    test[3] = (byte) (i / (256 * 256 * 256));
    i -= test[3];
    test[2] = (byte) (i / (256 * 256));
    i -= test[2];
    test[1] = (byte) (i / (256));
    i -= test[1];
    test[0] = (byte) i;
    out.write(test);
    //out.writeInt(i);
  }
  {
    //width
    int i = tmpin.readLONG();
//  ico[pos++] + ico[pos++] * 256 + ico[pos++] * 256 * 256 + ico[pos++] * 256 * 256 * 256;
    byte[] test = new byte[4];
    test[3] = (byte) (i / (256 * 256 * 256));
    i -= test[3];
    test[2] = (byte) (i / (256 * 256));
    i -= test[2];
    test[1] = (byte) (i / (256));
    i -= test[1];
    test[0] = (byte) i;
    out.write(test);
    //out.writeInt(i);
  }
  {
    //height -> this is a fix because it should be multiplied by 2
    int i = tmpin.readLONG() * 2;
    byte[] test = new byte[4];
    test[3] = (byte) (i / (256 * 256 * 256));
    i -= test[3];
    test[2] = (byte) (i / (256 * 256));
    i -= test[2];
    test[1] = (byte) (i / (256));
    i -= test[1];
    test[0] = (byte) i;
    out.write(test);
    //out.writeInt(i);
    /** @todo: I should really get more into bitshifting someday, that could produce much cleaner code ) */
  }
  //END FIX HEIGHT
  while (true) {
    int tmpbyte = tmpin.readBYTE();
    out.writeByte(tmpbyte);
  }
} catch (EOFException ex) {}
    /** END NEED TO WRITE */
out.flush();

      }
      System.out.println("Start writing to the output stream");
      byte[] icondata = icon.toByteArray();
      stream.write(icondata);
      stream.flush();


    //out.setByteOrder(ByteOrder.LITTLE_ENDIAN);

  }
}
