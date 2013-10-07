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
package org.madlonkay.supertmxmerge.data.JAXB;

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
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.ITu;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.data.ResolutionSet;
import org.madlonkay.supertmxmerge.util.LocString;
import org.madlonkay.supertmxmerge.util.ReflectionUtil;
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
public class JAXBTmx implements ITmx {

    private static final JAXBContext CONTEXT;
    private static final Unmarshaller UNMARSHALLER;
    private static final Marshaller MARSHALLER;
    /**
     * Special XMLReader with EntityResolver required to resolve system DTD per
     * http://stackoverflow.com/questions/3587196/jaxb-saxparseexception-when-unmarshalling-document-with-relative-path-to-dtd
     */
    private static final XMLReader XMLREADER;
    
    private PropertyChangeSupport propertySupport;
    
    private Tmx tmx;
    private File file;
    private Map<Key, ITuv> tuvMap;
    private Map<Key, ITu> tuMap;
    private Map<String, String> tmxMetadata;
    
    private static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    
    private static final Logger LOGGER = Logger.getLogger(JAXBTmx.class.getName());
    
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
                    InputSource s = new InputSource(JAXBTmx.class.getResourceAsStream("/schemas/" + systemFile.getName()));
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

    public JAXBTmx(File file) throws UnmarshalException {
        propertySupport = new PropertyChangeSupport(this);
        this.file = file;
        try {
            Source source = new SAXSource(XMLREADER, new InputSource(new FileInputStream(this.file)));
            this.tmx = (Tmx) UNMARSHALLER.unmarshal(source);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private JAXBTmx(Tmx tmx) {
        this.tmx = tmx;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    @Override
    public String getSourceLanguage() {
        return tmx.getHeader().getSrclang();
    }

    @Override
    public int getSize() {
        return tmx.getBody().getTu().size();
    }
    
    @Override
    public String getName() {
        return file == null ? LocString.get("file_no_name") : file.getName();
    }

    @Override
    public Map<Key, ITuv> getTuvMap() {
        if (tuvMap == null) {
            generateMaps();
        }
        return tuvMap;
    }
    
    @Override
    public Map<Key, ITu> getTuMap() {
        if (tuMap == null) {
            generateMaps();
        }
        return tuMap;
    }
    
    private void generateMaps() {
        tuvMap = new HashMap<Key, ITuv>();
        tuMap = new HashMap<Key, ITu>();
        for (Tu rawTu : tmx.getBody().getTu()) {
            JAXBTu tu = new JAXBTu(rawTu, getSourceLanguage());
            Key key = tu.getKey();
            assert(!tuMap.containsKey(key));
            assert(!tuvMap.containsKey(key));
            tuMap.put(key, tu);
            tuvMap.put(key, tu.getTargetTuv());
        }
    }
    
    @Override
    public Map<String, String> getMetadata() {
        if (tmxMetadata == null) {
            generateMetadata();
        }
        return tmxMetadata;
    }
    
    private void generateMetadata() {
        tmxMetadata = ReflectionUtil.simplePropsToMap(tmx.getHeader());
    }
    
    @Override
    public ITmx applyChanges(ResolutionSet resolution) {
        Tmx originalData;
        try {
            originalData = clone(tmx);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        List<Tu> tus = tmx.getBody().getTu();
        for (Key key : resolution.toDelete) {
            tus.remove((Tu) getTuMap().get(key).getUnderlyingRepresentation());
        }
        for (Entry<Key, ITuv> e : resolution.toReplace.entrySet()) {
            Tu tu = (Tu) getTuMap().get(e.getKey()).getUnderlyingRepresentation();
            tu.getTuv().remove((Tuv) getTuvMap().get(e.getKey()).getUnderlyingRepresentation());
            tu.getTuv().add((Tuv) e.getValue().getUnderlyingRepresentation());
        }
        for (ITu tu : resolution.toAdd) {
            tus.add((Tu) tu.getUnderlyingRepresentation());
        }
        Tmx modifiedData = tmx;
        this.tmx = originalData;
        this.tuvMap = null;
        this.tuMap = null;
        return new JAXBTmx(modifiedData);
    }
    
    public static Tmx clone(Tmx jaxbObject) throws JAXBException {
        // Inspired by: http://stackoverflow.com/a/10870833/448068
        StringWriter xml = new StringWriter();
        MARSHALLER.marshal(jaxbObject, xml);
        StringReader reader = new StringReader(xml.toString());
        Source source = new SAXSource(XMLREADER, new InputSource(reader));
        return (Tmx) UNMARSHALLER.unmarshal(source);
    }
    
    @Override
    public void writeTo(File output) throws JAXBException {
        MARSHALLER.marshal(tmx, output);
    }

    @Override
    public Object getUnderlyingRepresentation() {
        return tmx;
    }
}
