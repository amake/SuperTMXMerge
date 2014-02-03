/*
 * Copyright (C) 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.madlonkay.supertmxmerge.data.JAXB;

import gen.core.tmx14.Body;
import gen.core.tmx14.Header;
import gen.core.tmx14.Prop;
import gen.core.tmx14.Tmx;
import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.madlonkay.supertmxmerge.data.DiffSet;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.ITu;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.data.ResolutionSet;
import org.madlonkay.supertmxmerge.data.WriteFailedException;
import org.madlonkay.supertmxmerge.util.DiffUtil;
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
    
    private final PropertyChangeSupport propertySupport;
    
    private Tmx tmx;
    private final String name;
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
            MARSHALLER.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            // We can't simply use JAXB_FORMATTED_OUTPUT because it will add unwanted
            // whitespace to <seg>s that have complex content; use a custom
            // TmxWriter instead (see `writeTo(File)`).
            //MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
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
    
    public static JAXBTmx createFromDiff(JAXBTmx tmx1, JAXBTmx tmx2) {
        try {
            // We add properties and move stuff around; clone the TMXs
            // so that this function can be idempotent.
            tmx1 = tmx1.klone();
            tmx2 = tmx2.klone();
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        
        DiffSet set = DiffUtil.generateDiffSet(tmx1, tmx2);
        
        Tmx tmx = newEmptyTmx(tmx2.tmx);
        Body body = tmx.getBody();
        
        for (Key key : set.added) {
            Tu tu = (Tu) tmx2.getTuMap().get(key).getUnderlyingRepresentation();
            body.getTu().add(tu);
            
            Prop prop = new Prop();
            tu.getNoteOrProp().add(prop);
            prop.setType(DiffUtil.DIFF_PROP_TYPE);
            prop.setContent(DiffUtil.DIFF_PROP_VALUE_ADDED);
        }
        
        for (Key key : set.deleted) {
            Tu tu = (Tu) tmx1.getTuMap().get(key).getUnderlyingRepresentation();
            body.getTu().add(tu);
            
            Prop prop = new Prop();
            tu.getNoteOrProp().add(prop);
            prop.setType(DiffUtil.DIFF_PROP_TYPE);
            prop.setContent(DiffUtil.DIFF_PROP_VALUE_DELETED);
        }
        
        for (Key key : set.modified) {
            JAXBTu tu1 = (JAXBTu) tmx1.getTuMap().get(key);
            Tu tu = (Tu) tu1.getUnderlyingRepresentation();
            body.getTu().add(tu);
            
            JAXBTu tu2 = (JAXBTu) tmx2.getTuMap().get(key);
            tu.getTuv().add((Tuv) tu2.getTargetTuv().getUnderlyingRepresentation());
            
            Prop prop = new Prop();
            tu.getNoteOrProp().add(prop);
            prop.setType(DiffUtil.DIFF_PROP_TYPE);
            prop.setContent(DiffUtil.DIFF_PROP_VALUE_MODIFIED);
            
            Prop prop1 = new Prop();
            Tuv tuv1 = (Tuv) tu1.getTargetTuv().getUnderlyingRepresentation();
            tuv1.getNoteOrProp().add(prop1);
            prop1.setType(DiffUtil.DIFF_PROP_MODIFIED_TYPE);
            prop1.setContent(DiffUtil.DIFF_PROP_MODIFIED_VALUE_BEFORE);
            
            Prop prop2 = new Prop();
            Tuv tuv2 = (Tuv) tu2.getTargetTuv().getUnderlyingRepresentation();
            tuv2.getNoteOrProp().add(prop2);
            prop2.setType(DiffUtil.DIFF_PROP_MODIFIED_TYPE);
            prop2.setContent(DiffUtil.DIFF_PROP_MODIFIED_VALUE_AFTER);
        }
        
        return new JAXBTmx(tmx, "diff");
    }

    public JAXBTmx(File file) throws Exception {
        propertySupport = new PropertyChangeSupport(this);
        this.name = file.getName();
        this.file = file;
        Source source = new SAXSource(XMLREADER, new InputSource(new FileInputStream(file)));
        this.tmx = (Tmx) UNMARSHALLER.unmarshal(source);
    }
    
    private JAXBTmx(Tmx tmx, String name) {
        this.tmx = tmx;
        this.name = name;
        propertySupport = new PropertyChangeSupport(this);
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
        return name;
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
            ITuv tuv = tu.getTargetTuv();
            tuvMap.put(key, tuv == null ? JAXBTuv.EMPTY_TUV : tuv);
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
        if (file != null) {
            tmxMetadata.put("Path", file.getAbsolutePath());
        }
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
            ITuv tuvToRemove = getTuvMap().get(e.getKey());
            if (tuvToRemove != null) {
                tu.getTuv().remove((Tuv) tuvToRemove.getUnderlyingRepresentation());
                tu.getTuv().add((Tuv) e.getValue().getUnderlyingRepresentation());
            }
        }
        for (ITu tu : resolution.toAdd) {
            tus.add((Tu) tu.getUnderlyingRepresentation());
        }
        Tmx modifiedData = tmx;
        this.tmx = originalData;
        this.tuvMap = null;
        this.tuMap = null;
        return new JAXBTmx(modifiedData, LocString.get("MERGED_TMX_NAME"));
    }
    
    public JAXBTmx klone() throws JAXBException {
        return new JAXBTmx(clone(tmx), name);
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
    public void writeTo(File output) throws WriteFailedException {
        try {
            OutputStream stream = new FileOutputStream(output);
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(stream, "UTF-8");
            MARSHALLER.marshal(tmx, new TmxWriter(writer));
        } catch (FileNotFoundException ex) {
            throw new WriteFailedException(ex);
        } catch (JAXBException ex) {
            throw new WriteFailedException(ex);
        } catch (XMLStreamException ex) {
            throw new WriteFailedException(ex);
        }
    }

    @Override
    public Object getUnderlyingRepresentation() {
        return tmx;
    }
    
    private static Tmx newEmptyTmx(Tmx orig) {
        Tmx tmx = new Tmx();
        tmx.setVersion("1.4");
        
        Package pkg = JAXBTmx.class.getPackage();
        Header header = new Header();
        tmx.setHeader(header);
        if (pkg != null) {
            header.setCreationtool(pkg.getImplementationTitle());
            header.setCreationtoolversion(pkg.getImplementationVersion());
        }
        if (orig != null) {
            tmx.setVersion(orig.getVersion());
            header.setOTmf(orig.getHeader().getOTmf());
            header.setSrclang(orig.getHeader().getSrclang());
        }
        
        Body body = new Body();
        tmx.setBody(body);
        
        return tmx;
    }
    
    public static JAXBTmx newEmptyJAXBTmx(JAXBTmx orig) {
        return new JAXBTmx(newEmptyTmx(orig == null ? null : orig.tmx), LocString.get("NEW_TMX_NAME"));
    }
    
    public void combine(JAXBTmx tmx) {
        Set<Key> toAdd = new HashSet<Key>();
        toAdd.addAll(tmx.getTuMap().keySet());
        toAdd.removeAll(this.getTuMap().keySet());

        List<Tu> tus = this.tmx.getBody().getTu();
        for (Key key : toAdd) {
            tus.add((Tu) tmx.getTuMap().get(key).getUnderlyingRepresentation());
        }
        tuMap = null;
        tuvMap = null;
    }
}
