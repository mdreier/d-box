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
import nl.ikarus.nxt.priv.imageio.icoreader.obj.*;
import java.util.*;
import java.io.*;
import javax.imageio.stream.*;
import nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOReader;

/**
 * BYTE: 1 byte (unsigned)
 * WORD: 2 bytes
 * DWORD 4 bytes
 *
 * <pre>
 * typedef struct
 * {
 *     WORD           idReserved;   // Reserved (must be 0)
 *     WORD           idType;       // Resource Type (1 for icons)
 *     WORD           idCount;      // How many images?
 *     ICONDIRENTRY   idEntries[1]; // An entry for each image (idCount of 'em)
 * } ICONDIR, *LPICONDIR;
 * </pre>
 *
 * @author J.B. van der Burgh
 * @version 1.0
 */
public class ICOFile {

  private final boolean DEBUG = Boolean.valueOf(System.getProperty(ICOReader.PROPERTY_NAME_PREFIX+"debug",Boolean.toString(false)));
  private int reserved = 0;
  private int type = 1;
  private int imageCount;
  private List<IconEntry> entries = new ArrayList<IconEntry>();
  private MyReader reader;

  public ICOFile(ImageInputStream in) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream(10240);
    byte[] buff = new byte[1024];
    int len ;
    while((len = in.read(buff))!=-1) {
      bout.write(buff,0,len);
    }
    this.reader = new MyReader(new DataInputStream(new ByteArrayInputStream(bout.toByteArray())));
    bout=null;
    _init();
  }
  public ICOFile(byte[] data) throws IOException {
    this(new MyReader(new DataInputStream(new ByteArrayInputStream(data))));
  }
  public ICOFile(MyReader r) throws IOException {
    this.reader = r;
    _init();
  }
  private void _init() throws IOException {
    readHeader();
    readEntries();
  }
  public int getEntryCount() {
    return entries.size();
  }
  public IconEntry getEntry(int nr) {
    return entries.get(nr);
  }
  public Iterator<IconEntry> getEntryIterator() {
    return entries.iterator();
  }

  private void readHeader() throws IOException {
    reserved = reader.readWORD();
    type=reader.readWORD();
    //if (type != 1) System.err.println("Resource is not an ICO file??   expected value: 1, found: " +  type);
    if (type != 1) throw new IOException("Resource is not an ICO file??   expected value: 1, found: " +  type);

    imageCount=reader.readWORD();
    if (DEBUG)  System.out.println(imageCount + " images in resource");
    if (imageCount > 500) {
      /** @todo: fix this nicely */
      throw new IOException("More than 500 icons in resource, aborting to prevent running out of memory....");
    }


  }

  private void readEntries() throws IOException  {
    for (int i=0;i<imageCount;i++) {
      try {
	IconEntry e = new IconEntry(reader);
	entries.add(e);
      } catch (IOException ex) {
	ex.printStackTrace();
      }
    }
    if (DEBUG) System.out.println("Parsed " + entries.size() + " out of " + imageCount + " entries");
  }

}
