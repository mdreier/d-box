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
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
/**
 * <pre>

typdef struct
{
   BITMAPINFOHEADER   icHeader;      // DIB header
   RGBQUAD         icColors[1];   // Color table
   BYTE            icXOR[1];      // DIB bits for XOR mask
   BYTE            icAND[1];      // DIB bits for AND mask
} ICONIMAGE, *LPICONIMAGE;

The icHeader member has the form of a DIB BITMAPINFOHEADER.
  Only the following members are used: biSize, biWidth, biHeight,
  biPlanes, biBitCount, biSizeImage. All other members must be 0.
  The biHeight member specifies the combined height of the XOR and AND masks.
  The members of icHeader define the contents and sizes of the other
  elements of the ICONIMAGE structure in the same way that the BITMAPINFOHEADER
  structure defines a CF_DIB format DIB.

The icColors member is an array of RGBQUADs. The number of elements in
  this array is determined by examining the icHeader member.

The icXOR member contains the DIB bits for the XOR mask of the image.
  The number of bytes in this array is determined by examining the icHeader member.
  The XOR mask is the color portion of the image and is applied to the destination
  using the XOR operation after the application of the AND mask.

The icAND member contains the bits for the monochrome AND mask. The number of bytes
  in this array is determined by examining the icHeader member, and assuming 1bpp.
  The dimensions of this bitmap must be the same as the dimensions of the XOR mask.
  The AND mask is applied to the destination using the AND operation, to preserve
  or remove destination pixels before applying the XOR mask.

Note: The biHeight member of the icHeader structure represents the combined height
  of the XOR and AND masks. Remember to divide this number by two before using it to
  perform calculations for either of the XOR or AND masks. Also remember that the
  AND mask is a monochrome DIB, with a color depth of 1 bpp.

 * </pre>
 *
 * @author J.B. van der Burgh
 * @version 1.0
 */
public abstract class Bitmap {
  /** @see BITMAPINFOHEADER http://msdn.microsoft.com/library/default.asp?url=/library/en-us/gdi/bitmaps_1rw2.asp */
  private BufferedImage _cachedImage = null;
 // protected int _bytesInHeader=0;
  protected IconEntry entry;
  protected int biWidth;//LONG
  protected int biHeight; //LONG -> the combined height of the AND & XOR masks
  protected int biSize; //DWORD, bytes in this struct
  protected int biPlanes; //WORD
  protected int biBitCount;//WORD //bits per pixel
  protected int biCompression;//DWORD
  protected int biSizeImage;//DWORD //if there is no compression it is valid to set this to 0
  protected int biXPelsPerMeter;//DWORD
  protected int biYPelsPerMeter;//DWORD
  protected int biColorsUsed;//DWORD
  protected int biColorsImportant;//DWORD
  protected int XORmaskSize;
  protected int ANDMaskSize;
  protected byte[] RGBQUAD;
  protected byte[] XOR;
  protected byte[] AND;
//  protected RGBQuad[] icColors; //RGBQUAD
 // protected byte[] icXOR; //XOR mask
  //protected byte[] icAND; //AND mask

  protected MyReader reader;

  protected static int readBitCountFromData(byte[] data) throws IOException {
    MyReader reader = new MyReader(new DataInputStream(new ByteArrayInputStream(data)));
    reader.readDWORD();
    reader.readLONG();
    reader.readLONG();
    reader.readWORD();
    int bitcount = reader.readWORD();
//    System.err.println("bibitcount: " + bitcount+"     COMPRESSION: " + compression);
    return bitcount;
  }

  protected class Compression {
    public final static int BI_BITFIELDS = 3;
    public final static int BI_JPEG = 4;
    public final static int BI_PNG = 5;
    public final static int BI_RGB = 0; //no compression
    public final static int BI_RLE4 = 2;
    public final static int BI_RLE8 = 1;
    public final static int BI_1632 = 0x32333631;
    public String getCompressionName(int val) {
      switch(val) {
        case BI_BITFIELDS:
          return "BI_BITFIELDS";
        case BI_JPEG:
          return "BI_JPEG";
        case BI_PNG:
          return "BI_PNG";
        case BI_RGB:
          return "BI_RGB (uncompressed)";
        case BI_RLE4:
          return "BI_RLE4";
        case BI_RLE8:
          return "BI_RLE8";
        case BI_1632:
          return "BI_1632";
        default:
          return "UNKNOWN";
      }
    }

  }


  protected Bitmap(IconEntry pEntry) throws IOException {
      this.entry = pEntry;
//      width = pEntry.getWidth();
      //    height = pEntry.getHeight();
      this.reader = new MyReader(new DataInputStream(new ByteArrayInputStream(entry.getImageData())));
      this.biSize = reader.readDWORD();
      this.biWidth=reader.readLONG();
      this.biHeight=reader.readLONG();
  //    System.out.println("W="+biWidth +" h="+biHeight);
      this.biPlanes=reader.readWORD();
      this.biBitCount=reader.readWORD();
      this.biCompression=reader.readDWORD();
      this.biSizeImage=reader.readDWORD();
      this.biXPelsPerMeter=reader.readLONG();
      this.biYPelsPerMeter=reader.readLONG();
      this.biColorsUsed=reader.readDWORD();
      this.biColorsImportant=reader.readDWORD();
    //  _bytesInHeader = reader.getOffset();
      checkCompression();
  }

  private void checkCompression() throws IOException {
      if (this.biCompression != 0 && !(this instanceof BitmapImageIO)) {
        throw new IOException("Compressed icons are currently unsupported... If you want support, please send an example to the author of this class, used compression: "+new Compression().getCompressionName(this.biCompression));
      }
  }

  public static Bitmap getImageIoBitmap(IconEntry entry) throws IOException {
      return new BitmapImageIO(entry);
  }
  public static Bitmap getBitmap(IconEntry entry) throws IOException {
    Bitmap res=null;
//    if (true) {
//      return new BitmapImageIO(entry);
//    }
    /** icons
     * 1 - 8 have a pallette
     * 16+ are RGB
     */
    switch(entry.getBitCount()) {
      case 1:
	//2 colors (Black&White)
      case 2:
	//4 colors
      case 4:
	//16 colors
      case 8:
	//256 colors
	res = new IndexedBitmap(entry);
	break;
      case 16:
	System.err.println("16 Bpp not yet tested: " + entry.getBitCount() + " using an untested class");
	res = new Bitmap16(entry);
	break;
      case 24:
	//true color
	res = new Bitmap24(entry);
	break;
      case 32:
	//true color with alpha
	res = new Bitmap32(entry);
//	System.err.println("32 Bpp not yet implemented: " + entry.getBitCount());
	break;
      default:
	System.err.println("Unsupported bpp: " + entry.getBitCount());
	break;
    }
    return res;
  }
  public BufferedImage getImage() throws IOException {
    if (_cachedImage != null) return _cachedImage;
    checkCompression();

    _cachedImage = createImage();
    return _cachedImage;
  }



  protected abstract BufferedImage createImage()  throws IOException;

  public String toString() {
    StringBuffer sb = new StringBuffer(100);
    String name = this.getClass().getName();
    int idx = name.lastIndexOf('.');
    sb.append(name.substring(idx + 1));
    sb.append(" [");
    try {
      java.util.LinkedList<Class> classes = new java.util.LinkedList<Class>();
      Class tmpC = this.getClass();
      while(tmpC != Bitmap.class && tmpC != Object.class && tmpC != null) {
	classes.addFirst(tmpC);
	tmpC = tmpC.getSuperclass();
      }
//      classes.addFirst(tmpC);
      append(sb, true, tmpC.getDeclaredFields());
      for(Class c : classes) {
	append(sb, false, c.getDeclaredFields());
      }
    } catch (IllegalAccessException ex) {
    } catch (IllegalArgumentException ex) {
    }
    sb.append("]");
    return sb.toString();
  }
private void append(StringBuffer sb,boolean first, Field[] data) throws IllegalAccessException , IllegalArgumentException{

  for (Field f : data) {
    if (f.getName().equals("entry") || f.getName().equals("reader"))
      continue;
    if (!first)
      sb.append(", ");
    else
      first = false;
    sb.append(f.getName()).append("=");
    sb.append(f.get(this));
  }

}
}
