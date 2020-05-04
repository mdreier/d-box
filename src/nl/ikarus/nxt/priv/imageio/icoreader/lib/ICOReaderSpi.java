package nl.ikarus.nxt.priv.imageio.icoreader.lib;
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
import java.util.*;
import javax.imageio.*;
import javax.imageio.spi.*;
import nl.ikarus.nxt.priv.imageio.icoreader.*;
import javax.imageio.stream.*;

public class ICOReaderSpi extends ImageReaderSpi {

  public ICOReaderSpi() {
    this("nXt webapps", VersionData.getVersion()
	 , new String[]{"ico","ICO"} ,new String[]{"ico","ICO"}
	 , new String[]{"image/vnd.microsoft.icon","image/x-ico"}
	 ,"nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOReader"
	 , new Class[]{javax.imageio.stream.ImageInputStream.class}
	 , null //new String[]{"nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOWriterSpi"}
	 ,false//supportsStandardStreamMetadataFormat
	 ,null
	 ,null//nativeStreamMetadataFormatClassName
	 ,null
	 ,null //extraStreamMetadataFormatClassNames
	 ,false//supportsStandardImageMetadataFormat
	 ,ICOMetaDataFormat.NAME//nativeImageMetadataFormatName
	 ,"nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOMetaData" //nativeImageMetadataFormatClassName
	 ,null //extraImageMetadataFormatNames
	 ,null
	 );
     /** @todo: figure out what should be done here, is this constructor correct? */

  }
  /**
   * vendorName - the vendor name, as a non-null  String
   * version - a version identifier, as a non-null String
   *  names - a non-null array of Strings indicating the format names. At least one entry must be present.
   * suffixes - an array of Strings indicating the common file suffixes. If no suffixes are defined, null should be supplied. An array of length 0 will be normalized to null.
   * MIMETypes - an array of Strings indicating the format's MIME types. If no MIME types are defined, null should be supplied. An array of length 0 will be normalized to null.
   * readerClassName - the fully-qualified name of the associated ImageReader class, as a non-null String.
   * inputTypes - a non-null array of Class objects of length at least 1 indicating the legal input types.
   * writerSpiNames - an array Strings naming the classes of all associated ImageWriters, or null. An array of length 0 is normalized to null.
   * supportsStandardStreamMetadataFormat - a boolean that indicates whether a stream metadata object can use trees described by the standard metadata format.
   * nativeStreamMetadataFormatName - a String, or null, to be returned from getNativeStreamMetadataFormatName.
   * nativeStreamMetadataFormatClassName - a String, or null, to be used to instantiate a metadata format object to be returned from getNativeStreamMetadataFormat.
   * extraStreamMetadataFormatNames - an array of Strings, or null, to be returned from getExtraStreamMetadataFormatNames. An array of length 0 is normalized to null.
   * extraStreamMetadataFormatClassNames - an array of Strings, or null, to be used to instantiate a metadata format object to be returned from getStreamMetadataFormat. An array of length 0 is normalized to null.
   * supportsStandardImageMetadataFormat - a boolean that indicates whether an image metadata object can use trees described by the standard metadata format.
   * nativeImageMetadataFormatName - a String, or null, to be returned from getNativeImageMetadataFormatName.
   * nativeImageMetadataFormatClassName - a String, or null, to be used to instantiate a metadata format object to be returned from getNativeImageMetadataFormat.
   * extraImageMetadataFormatNames - an array of Strings to be returned from getExtraImageMetadataFormatNames. An array of length 0 is normalized to null.
   * extraImageMetadataFormatClassNames - an array of Strings, or null, to be used to instantiate a metadata format object to be returned from getImageMetadataFormat. An array of length 0 is normalized to null.
   *
   */
  public ICOReaderSpi(String vendorName, String version,
		      String[] names, String[] suffixes,
		      String[] MIMETypes, String readerClassName, Class[] inputTypes,
		      String[] writerSpiNames, boolean supportsStandardStreamMetadataFormat,
		      String nativeStreamMetadataFormatName, String nativeStreamMetadataFormatClassName,
		      String[] extraStreamMetadataFormatNames, String[] extraStreamMetadataFormatClassNames,
		      boolean supportsStandardImageMetadataFormat, String nativeImageMetadataFormatName,
		      String nativeImageMetadataFormatClassName, String[] extraImageMetadataFormatNames,
		      String[] extraImageMetadataFormatClassNames) {
    super(vendorName, version, names, suffixes, MIMETypes, readerClassName, inputTypes, writerSpiNames, supportsStandardStreamMetadataFormat, nativeStreamMetadataFormatName, nativeStreamMetadataFormatClassName, extraStreamMetadataFormatNames, extraStreamMetadataFormatClassNames, supportsStandardImageMetadataFormat, nativeImageMetadataFormatName, nativeImageMetadataFormatClassName, extraImageMetadataFormatNames, extraImageMetadataFormatClassNames);
  }

  /**
   * Returns <code>true</code> if the supplied source object appears to be of
   * the format supported by this reader.
   *
   * @param source the object (typically an <code>ImageInputStream</code>) to
   *   be decoded.
   * @return <code>true</code> if it is likely that this stream can be decoded.
   * @throws IOException if an I/O error occurs while reading the stream.
   * @todo Implement this javax.imageio.spi.ImageReaderSpi method
   */
  public boolean canDecodeInput(Object source) throws IOException {
    /** @todo: check if  I can decode the stream */
    if (source instanceof ImageInputStream) {
      byte[]      buff = new byte[4];
      int len;
      ImageInputStream in =((ImageInputStream)source);
      in.mark();
      in.readFully(buff);
      in.reset();
      //check header
      return (buff[0] == 0x00 && buff[1] == 0x00 && buff[2] == 0x01 && buff[3] == 0x00);
     // boolean res= (buff[0] == 0x00 && buff[1] == 0x00 && buff[2] == 0x01 && buff[3] == 0x00);
    //  System.err.println("Can Read image: " + res);
     // return res;
    }
    return true;
  }
  private volatile static Boolean isRegistered = Boolean.FALSE;
  static {
    registerIcoReader();
  }

  public synchronized static void registerIcoReader() {
    if (ICOReaderSpi.isRegistered.booleanValue()) return;
    ICOReaderSpi.isRegistered=Boolean.TRUE;
    try {
      Object registeredReader = IIORegistry.getDefaultInstance().getServiceProviderByClass(ICOReaderSpi.class);
      if (registeredReader == null) {
	Object reader = new ICOReaderSpi();
	IIORegistry.getDefaultInstance().registerServiceProvider(reader);
      }
    } finally {
     boolean DEBUG = Boolean.valueOf(System.getProperty(ICOReader.PROPERTY_NAME_PREFIX+"debug",Boolean.toString(false)));
     if (DEBUG) System.out.println(ICOReader.class.getName() + " loaded, version: " + VersionData.getVersion() + " build: " + VersionData.getBuild());
    }
  }

  /**
   * Returns an instance of the <code>ImageReader</code> implementation
   * associated with this service provider.
   *
   * @param extension a plug-in specific extension object, which may be
   *   <code>null</code>.
   * @return an <code>ImageReader</code> instance.
   * @throws IOException if the attempt to instantiate the reader fails.
   * @todo Implement this javax.imageio.spi.ImageReaderSpi method
   */
  public ImageReader createReaderInstance(Object extension) throws IOException {
    return new ICOReader(this);
  }

  /**
   * Returns a brief, human-readable description of this service provider and
   * its associated implementation.
   *
   * @param locale a <code>Locale</code> for which the return value should be
   *   localized.
   * @return a <code>String</code> containing a description of this service
   *   provider.
   * @todo Implement this javax.imageio.spi.IIOServiceProvider method
   */
  public String getDescription(Locale locale) {
    return "Microsoft Icon Format (ICO) Reader version: "+VersionData.getVersion() + " #"+VersionData.getBuild();
  }
}
