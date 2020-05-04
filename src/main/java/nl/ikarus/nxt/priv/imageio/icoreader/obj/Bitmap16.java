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
/**
 *  Warning, this class is untested.....
 *  are 16bit icons using the rgb way or the indexed way...?
 * @author J.B. van der Burgh
 * @version 1.0
 */
public class Bitmap16 extends RgbBitmap {
  protected Bitmap16(IconEntry pEntry) throws IOException {
    super(pEntry, 2);
  }
}
