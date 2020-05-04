package nl.ikarus.nxt.priv.imageio.icoreader.lib;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;
/**
 * implementing metadata
 * using the tutorial on
 * http://java.sun.com/j2se/1.4.2/docs/guide/imageio/spec/extending.fm3.html
 *
 * @author J.B. van der Burgh
 * @version 1.0
 */
public class ICOMetaDataFormat extends IIOMetadataFormatImpl {
  protected static final String NAME = "nl.ikarus.nxt.priv.imageio.icoreader.lib.ICOMetaData_1.0";
        // Create a single instance of this class (singleton pattern)
        private static ICOMetaDataFormat defaultInstance =
                new ICOMetaDataFormat();

        // Make constructor private to enforce the singleton pattern
        private ICOMetaDataFormat() {
                // Set the name of the root node
                // The root node has a single child node type that may repeat
                super(ICOMetaDataFormat.NAME,
                      CHILD_POLICY_REPEAT);

                // Set up the "KeywordValuePair" node, which has no children
                addElement("KeywordValuePair",
                           ICOMetaDataFormat.NAME,
                           CHILD_POLICY_EMPTY);

                // Set up attribute "keyword" which is a String that is required
                // and has no default value
                addAttribute("KeywordValuePair", "keyword", DATATYPE_STRING,
                             true, null);
                // Set up attribute "value" which is a String that is required
                // and has no default value
                addAttribute("KeywordValuePair", "value", DATATYPE_STRING,
                             true, null);
        }

        // Check for legal element name
        public boolean canNodeAppear(String elementName,
                                     ImageTypeSpecifier imageType) {
                return elementName.equals("KeywordValuePair");
        }

        // Return the singleton instance
        public static ICOMetaDataFormat getDefaultInstance() {
                return defaultInstance;
        }
}
