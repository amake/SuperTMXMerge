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
package org.omegat.core.data;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.ITu;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.data.ResolutionSet;
import org.madlonkay.supertmxmerge.data.WriteFailedException;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class OmTTmx implements ITmx {

    private ProjectTMX tmx;
    private final String name;
    private final String sourceLanguage;
    private final String targetLanguage;
    
    private Map<Key, ITu> tuMap;
    private Map<Key, ITuv> tuvMap;
    
    public OmTTmx(ProjectTMX tmx, String name, String sourceLanguage, String targetLanguage) {
        this.tmx = tmx;
        this.name = name;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        generateMaps();
    }
    
    private void generateMaps() {
        tuvMap = new HashMap<Key, ITuv>();
        tuMap = new HashMap<Key, ITu>();
        for (Entry<String, TMXEntry> e : tmx.defaults.entrySet()) {
            ITu tu = new OmTTu(e.getValue(), targetLanguage);
            Key key = makeKey(e.getKey(), e.getValue());
            assert(!tuMap.containsKey(key));
            assert(!tuvMap.containsKey(key));
            tuMap.put(key, tu);
            tuvMap.put(key, tu.getTargetTuv());
        }
        for (Entry<EntryKey, TMXEntry> e : tmx.alternatives.entrySet()) {
            ITu tu = new OmTTu(e.getValue(), targetLanguage);
            Key key = makeKey(e.getKey(), e.getValue());
            assert(!tuMap.containsKey(key));
            assert(!tuvMap.containsKey(key));
            tuMap.put(key, tu);
            tuvMap.put(key, tu.getTargetTuv());
        }
    }
    
    private Key makeKey(Object entryKey, TMXEntry tmxEntry) {
        Key key = new Key(tmxEntry.source, entryKey);
        if (entryKey instanceof EntryKey) {
            EntryKey ek = (EntryKey) entryKey;
            key.addProp("file", ek.file);
            key.addProp("id", ek.id);
            key.addProp("next", ek.next);
            key.addProp("path", ek.path);
            key.addProp("prev", ek.prev);
        }
        return key;
    }

    @Override
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return tmx.alternatives.size() + tmx.defaults.size();
    }

    @Override
    public Map<String, String> getMetadata() {
        return null;
    }

    @Override
    public ITmx applyChanges(ResolutionSet resolution) {
        ProjectTMX originalData = clone(tmx);
        for (Key key : resolution.toDelete) {
            remove(key);
        }
        for (Entry<Key, ITuv> e : resolution.toReplace.entrySet()) {
            remove(e.getKey());
            add(e.getKey(), (TMXEntry) e.getValue().getUnderlyingRepresentation());
        }
        for (ITu tu : resolution.toAdd) {
            TMXEntry entry = (TMXEntry) tu.getUnderlyingRepresentation();
            tmx.defaults.put(entry.source, entry);
        }
        ProjectTMX modifiedData = tmx;
        this.tmx = originalData;
        this.tuvMap = null;
        this.tuMap = null;
        return new OmTTmx(modifiedData, this.name, this.sourceLanguage, this.targetLanguage);
    }
    
    private void add(Key key, TMXEntry tuv) {
        if (key.foreignKey instanceof String) {
            tmx.defaults.put((String) key.foreignKey, tuv);
        } else if (key.foreignKey instanceof EntryKey) {
            tmx.alternatives.put((EntryKey) key.foreignKey, tuv);
        } else {
            throw new IllegalArgumentException("Cannot add key of type "
                    + key.getClass().getName() + " to a " + getClass().getName());
        }
    }
    
    private void remove(Key key) {
        if (key.foreignKey instanceof String) {
            tmx.defaults.remove((String) key.foreignKey);
        } else if (key.foreignKey instanceof EntryKey) {
            tmx.alternatives.remove((EntryKey) key.foreignKey);
        } else {
            throw new IllegalArgumentException("Cannot remove key of type "
                    + key.getClass().getName() + " from a " + getClass().getName());
        }
    }
    
    private static ProjectTMX clone(ProjectTMX tmx) {
        ProjectTMX newTmx = new ProjectTMX();
        for (Entry<EntryKey, TMXEntry> e : tmx.alternatives.entrySet()) {
            newTmx.alternatives.put(e.getKey(), e.getValue());
        }
        for (Entry<String, TMXEntry> e : tmx.defaults.entrySet()) {
            newTmx.defaults.put(e.getKey(), e.getValue());
        }
        return newTmx;
    }

    @Override
    public void writeTo(File outputFile) throws WriteFailedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getUnderlyingRepresentation() {
        return tmx;
    }

    @Override
    public ITu getTu(Key key) {
        return tuMap.get(key);
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ITuv get(Object key) {
        return tuvMap.get(key);
    }

    @Override
    public ITuv put(Key key, ITuv value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ITuv remove(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putAll(Map<? extends Key, ? extends ITuv> m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Key> keySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<ITuv> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Entry<Key, ITuv>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
