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
import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.madlonkay.supertmxmerge.util.ReflectionUtil;
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

    private static final JAXBContext CONTEXT;
    private static final Unmarshaller UNMARSHALLER;
    private static final Marshaller MARSHALLER;
    /**
     * Special XMLReader with EntityResolver required to resolve system DTD per
     * http://stackoverflow.com/questions/3587196/jaxb-saxparseexception-when-unmarshalling-document-with-relative-path-to-dtd
     */
    private static final XMLReader XMLREADER;
    
    private PropertyChangeSupport propertySupport;
    
    private Tmx data;
    private String filepath;
    private Map<String, Tuv> anonymousTuvs;
    private Map<String, Tu> tuMap;
    private Map<String, String> tmxMetadata;
    
    private static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    
    private static final Logger LOGGER = Logger.getLogger(TmxFile.class.getName());
    
    static {
        try {
            CONTEXT = JAXBContext.newInstance(Tmx.class);
            UNMARSHALLER = CONTEXT.createUnmarshaller();
            MARSHALLER = CONTEXT.createMarshaller();
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
            throw new RuntimeException(ex);
        } catch (SAXNotRecognizedException ex) {
            throw new RuntimeException(ex);
        } catch (SAXNotSupportedException ex) {
            throw new RuntimeException(ex);
        } catch (SAXException ex) {
            throw new RuntimeException(ex);
        }
    }

    public TmxFile(String filepath) throws UnmarshalException {
        propertySupport = new PropertyChangeSupport(this);
        
        this.filepath = filepath;
        try {
            Source source = new SAXSource(XMLREADER, new InputSource(new FileInputStream(filepath)));
            this.data = (Tmx) UNMARSHALLER.unmarshal(source);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private TmxFile(Tmx data) {
        this.data = data;
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
            generateMaps();
        }
        return anonymousTuvs;
    }
    
    public Map<String, Tu> getTuMap() {
        if (tuMap == null) {
            generateMaps();
        }
        return tuMap;
    }
    
    private void generateMaps() {
        anonymousTuvs = new HashMap<String, Tuv>();
        tuMap = new HashMap<String, Tu>();
        for (Tu tu : getData().getBody().getTu()) {
            String key = TuvUtil.getContent(TuvUtil.getSourceTuv(tu, getSourceLanguage()));
            tuMap.put(key, tu);
            anonymousTuvs.put(key, TuvUtil.getTargetTuv(tu, getSourceLanguage()));
        }
    }
    
    public Map<String, String> getMetadata() {
        if (tmxMetadata == null) {
            generateMetadata();
        }
        return tmxMetadata;
    }
    
    private void generateMetadata() {
        tmxMetadata = ReflectionUtil.simplePropsToMap(getData().getHeader());
    }

    /**
     * @return the doc
     */
    public Tmx getData() {
        return data;
    }
    
    public TmxFile applyChanges(ResolutionSet resolution) throws JAXBException {
        Tmx originalData = clone(getData());
        List<Tu> tus = getData().getBody().getTu();
        for (String key : resolution.toDelete) {
            tus.remove(getTuMap().get(key));
        }
        for (Entry<String, Tuv> e : resolution.toReplace.entrySet()) {
            Tu tu = getTuMap().get(e.getKey());
            tu.getTuv().remove(getTuvMap().get(e.getKey()));
            tu.getTuv().add(e.getValue());
        }
        tus.addAll(resolution.toAdd);
        Tmx modifiedData = getData();
        this.data = originalData;
        this.anonymousTuvs = null;
        this.tuMap = null;
        return new TmxFile(modifiedData);
    }
    
    public static Tmx clone(Tmx jaxbObject) throws JAXBException {
        // Inspired by: http://stackoverflow.com/a/10870833/448068
        StringWriter xml = new StringWriter();
        MARSHALLER.marshal(jaxbObject, xml);
        StringReader reader = new StringReader(xml.toString());
        Source source = new SAXSource(XMLREADER, new InputSource(reader));
        return (Tmx) UNMARSHALLER.unmarshal(source);
    }
    
    public void writeTo(String outputFile) throws JAXBException {
        MARSHALLER.marshal(getData(), new File(outputFile));
    }
}
