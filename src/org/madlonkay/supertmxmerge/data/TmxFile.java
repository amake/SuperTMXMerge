/*
 * Copyright (C) 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.madlonkay.supertmxmerge.data;

import gen.core.tmx14.Tmx;
import gen.core.tmx14.Tuv;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class TmxFile {

    private static Unmarshaller UNMARSHALLER = null;
    /**
     * Special XMLReader with EntityResolver required to resolve system DTD per
     * http://stackoverflow.com/questions/3587196/jaxb-saxparseexception-when-unmarshalling-document-with-relative-path-to-dtd
     */
    private static XMLReader XMLREADER = null;
    public static final String PROP_DATA = "data";
    
    private PropertyChangeSupport propertySupport;
    
    private Tmx data = null;
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
        propertySupport = new PropertyChangeSupport(this);
        
        this.filepath = filepath;
        try {
            Source source = new SAXSource(XMLREADER, new InputSource(new FileInputStream(filepath)));
            this.data = (Tmx) UNMARSHALLER.unmarshal(source);
        } catch (JAXBException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public String getSourceLanguage() {
        return getData().getHeader().getSrclang();
    }

    public int getSize() {
        return getData().getBody().getTu().size();
    }
    
    public String getFilePath() {
        return filepath;
    }
    
    public String getFileName() {
        return (new File(getFilePath())).getName();
    }

    public Map<String, Tuv> getTuvMap() {
        if (anonymousTuvs == null) {
            anonymousTuvs = TuvUtil.generateTuvMap(getData().getBody().getTu(), getSourceLanguage());
        }
        return anonymousTuvs;
    }

    /**
     * @return the doc
     */
    public Tmx getData() {
        return data;
    }

    /**
     * @param data the doc to set
     */
    public void setData(Tmx data) {
        gen.core.tmx14.Tmx oldData = this.data;
        this.data = data;
        propertySupport.firePropertyChange(PROP_DATA, oldData, data);
    }
}
