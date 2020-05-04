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
import java.io.IOException;
import java.io.DataInputStream;
import java.io.*;
import javax.imageio.stream.*;

public class MyReader {
  private DataInputStream reader;
  private int offset = 0;
  private int offsetAtMark = 0;
  public MyReader(DataInputStream in) {
    this.reader = in;
  }
  public void skip(int byteCount) throws IOException {
    reader.skipBytes(byteCount);
  }
  public void mark() {
    if (!reader.markSupported()) {
      System.err.println("Error, mark not supported for this stream");
    }
    offsetAtMark = offset;
    reader.mark(Integer.MAX_VALUE);
  }

  public void reset() throws IOException {
    reader.reset();
    offset = offsetAtMark;
  }
  public byte[] readBytes(int amount) throws IOException {
    byte[] res=new byte[amount];
    reader.readFully(res);
    offset += amount;
    return res;
  }
  public int getOffset() {
    return offset;
  }

  public int readLONG() throws IOException {
    return readLONG(reader);
  }

  public int readLONG(DataInputStream reader) throws IOException {
    return readDWORD(reader);
  }

  public int readDWORD() throws IOException {
    return readDWORD(reader);
  }

  public int readDWORD(DataInputStream reader) throws IOException {

    //  int res = ico[pos++] + ico[pos++] * 256 + ico[pos++] * 256 * 256 + ico[pos++] * 256 * 256 * 256;
    byte[] tmp = new byte[4];
    reader.readFully(tmp);
    int res = 0;
    for (int i = tmp.length - 1; i >= 0; i--) {
      res <<= 8;
      res += tmp[i] & 0xff;
    }
    offset += 4;
//    int res = tmp[0] + (tmp[1] << 8) + (tmp[2] << 16) + (tmp[3] << 24);
    return res;
  }

  public int readWORD() throws IOException {
    return readWORD(reader);
  }

  public int readWORD(DataInputStream reader) throws IOException {
    byte[] tmp = new byte[2];
    reader.readFully(tmp);
//   int res = tmp[0] + (tmp[1] << 8);
    int res = 0;
    for (int i = tmp.length - 1; i >= 0; i--) {
      res <<= 8;
      res += tmp[i] & 0xff;
    }
    offset += 2;
    return res;
  }

  public int readBYTE() throws IOException {
    return readBYTE(reader);
  }

  public int readBYTE(DataInputStream reader) throws IOException {
    int res = reader.readByte() & 0xFF;
    offset++;
    return res;
  }

}
