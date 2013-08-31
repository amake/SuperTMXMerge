/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.data;

import gen.core.tmx14.Tmx;
import gen.core.tmx14.Tuv;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.madlonkay.supertmxmerge.SuperTmxMerge;
import org.madlonkay.supertmxmerge.util.TuvUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author aaron.madlon-kay
 */
public class TmxFile {

    private static Unmarshaller UNMARSHALLER = null;
    /**
     * Special XMLReader with EntityResolver required to resolve system DTD per
     * http://stackoverflow.com/questions/3587196/jaxb-saxparseexception-when-unmarshalling-document-with-relative-path-to-dtd
     */
    private static XMLReader XMLREADER = null;
    private Tmx doc = null;
    private String filepath = null;
    private Map<String, Tuv> anonymousTuvs = null;
    
    private static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

    static {
        try {
            JAXBContext jc = JAXBContext.newInstance(Tmx.class);
            UNMARSHALLER = jc.createUnmarshaller();
            XMLReader xmlreader = XMLReaderFactory.createXMLReader();
            xmlreader.setFeature(FEATURE_NAMESPACES, true);
            xmlreader.setFeature(FEATURE_NAMESPACE_PREFIXES, true);
            xmlreader.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    File systemFile = new File(systemId);
                    InputSource s = new InputSource(TmxFile.class.getResourceAsStream("/schemas/" + systemFile.getName()));
                    return s;
                }
            });
            XMLREADER = xmlreader;
        } catch (JAXBException ex) {
            Logger.getLogger(SuperTmxMerge.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXNotRecognizedException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXNotSupportedException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TmxFile(String filepath) throws UnmarshalException {
        this.filepath = filepath;
        try {
            Source source = new SAXSource(XMLREADER, new InputSource(new FileInputStream(filepath)));
            this.doc = (Tmx) UNMARSHALLER.unmarshal(source);
        } catch (JAXBException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getSourceLanguage() {
        return doc.getHeader().getSrclang();
    }

    public int size() {
        return doc.getBody().getTu().size();
    }
    
    public String getFilePath() {
        return filepath;
    }

    public Map<String, Tuv> getTuvMap() {
        if (anonymousTuvs == null) {
            anonymousTuvs = TuvUtil.generateTuvMap(doc.getBody().getTu(), getSourceLanguage());
        }
        return anonymousTuvs;
    }
}
