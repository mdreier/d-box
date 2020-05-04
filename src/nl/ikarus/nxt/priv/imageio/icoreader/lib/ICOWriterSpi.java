package nl.ikarus.nxt.priv.imageio.icoreader.lib;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.spi.*;
import nl.ikarus.nxt.priv.imageio.icoreader.VersionData;

public class ICOWriterSpi extends ImageWriterSpi {
  protected ICOWriterSpi() {
    this("nXt webapps", VersionData.getVersion()
	 , new String[]{"ico","ICO"} ,new String[]{"ico","ICO"}
	 , new String[]{"image/vnd.microsoft.icon","image/x-ico"}
	 ,"nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOWriter"
	 , new Class[]{javax.imageio.stream.ImageInputStream.class}
	 ,new String[]{"nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOReaderSpi"}
	 , false //supportsStandardStreamMetadataFormat
	 , null
	 , null //nativeStreamMetadataFormatClassName
	 , null
	 , null //extraStreamMetadataFormatClassNames
	 , false //supportsStandardImageMetadataFormat
	 , null //nativeImageMetadataFormatName
	 , null
	 , null //extraImageMetadataFormatNames
	 , null
	);
  }
  private volatile static Boolean isRegistered = Boolean.FALSE;
  static {
    registerIcoWriter();
  }

  public synchronized static void registerIcoWriter() {
    if (isRegistered.booleanValue()) return;
    isRegistered = Boolean.TRUE;
    try {
      Object registeredReader = IIORegistry.getDefaultInstance().getServiceProviderByClass(ICOWriterSpi.class);
      if (registeredReader == null) {
	Object writer = new ICOWriterSpi();
	IIORegistry.getDefaultInstance().registerServiceProvider(writer);
      }
      isRegistered=true;
    } finally {
      boolean DEBUG = Boolean.valueOf(System.getProperty(ICOReader.PROPERTY_NAME_PREFIX+"debug",Boolean.toString(false)));
      if (DEBUG) System.out.println(ICOWriter.class.getName() + " loaded, version: " + VersionData.getVersion() + " build: " + VersionData.getBuild());
    }
  }


  /**
   * @param vendorName String
   * @param version String
   * @param names String[]
   * @param suffixes String[]
   * @param MIMETypes String[]
   * @param writerClassName String
   * @param outputTypes Class[]
   * @param readerSpiNames String[]
   * @param supportsStandardStreamMetadataFormat boolean
   * @param nativeStreamMetadataFormatName String
   * @param nativeStreamMetadataFormatClassName String
   * @param extraStreamMetadataFormatNames String[]
   * @param extraStreamMetadataFormatClassNames String[]
   * @param supportsStandardImageMetadataFormat boolean
   * @param nativeImageMetadataFormatName String
   * @param nativeImageMetadataFormatClassName String
   * @param extraImageMetadataFormatNames String[]
   * @param extraImageMetadataFormatClassNames String[]
   */
  public ICOWriterSpi(String vendorName, String version, String[] names, String[] suffixes, String[] MIMETypes, String writerClassName, Class[] outputTypes, String[] readerSpiNames, boolean supportsStandardStreamMetadataFormat, String nativeStreamMetadataFormatName, String nativeStreamMetadataFormatClassName, String[] extraStreamMetadataFormatNames, String[] extraStreamMetadataFormatClassNames, boolean supportsStandardImageMetadataFormat, String nativeImageMetadataFormatName, String nativeImageMetadataFormatClassName, String[] extraImageMetadataFormatNames, String[] extraImageMetadataFormatClassNames) {
    super(vendorName, version, names, suffixes,
	  MIMETypes, writerClassName, outputTypes,
	  readerSpiNames, supportsStandardStreamMetadataFormat,
	  nativeStreamMetadataFormatName, nativeStreamMetadataFormatClassName,
	  extraStreamMetadataFormatNames, extraStreamMetadataFormatClassNames,
	  supportsStandardImageMetadataFormat, nativeImageMetadataFormatName,
	  nativeImageMetadataFormatClassName, extraImageMetadataFormatNames,
	  extraImageMetadataFormatClassNames);
  }

  /**
   * Returns <code>true</code> if the <code>ImageWriter</code> implementation
   * associated with this service provider is able to encode an image with the
   * given layout.
   *
   * @param type an <code>ImageTypeSpecifier</code> specifying the layout of
   *   the image to be written.
   * @return <code>true</code> if this writer is likely to be able to encode
   *   images with the given layout.
   * @todo check if the image can be encoded
   */
  public boolean canEncodeImage(ImageTypeSpecifier type) {
    return true;
  }

  /**
   * Returns an instance of the <code>ImageWriter</code> implementation
   * associated with this service provider.
   *
   * @param extension a plug-in specific extension object, which may be
   *   <code>null</code>.
   * @return an <code>ImageWriter</code> instance.
   * @throws IOException if the attempt to instantiate the writer fails.
   * @todo Implement this javax.imageio.spi.ImageWriterSpi method
   */
  public ImageWriter createWriterInstance(Object extension) throws IOException {
    return new ICOWriter(this);
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
    return "ICO Writer";
  }
}
