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
package org.omegat.core.data;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.ITu;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.data.Key;
import org.madlonkay.supertmxmerge.data.ResolutionSet;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class OmTTmx implements ITmx {

    private ProjectTMX tmx;
    private String name;
    private String sourceLanguage;
    private String targetLanguage;
    
    private Map<Key, ITu> tuMap;
    private Map<Key, ITuv> tuvMap;
    
    public OmTTmx(ProjectTMX tmx, String name, String sourceLanguage, String targetLanguage) {
        this.tmx = tmx;
        this.name = name;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
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
        for (Entry<String, TMXEntry> e : tmx.defaults.entrySet()) {
            ITu tu = new OmTTu(e.getValue(), null, targetLanguage);
            Key key = tu.getKey();
            assert(!tuMap.containsKey(key));
            assert(!tuvMap.containsKey(key));
            tuMap.put(key, tu);
            tuvMap.put(key, tu.getTargetTuv());
        }
        for (Entry<EntryKey, TMXEntry> e : tmx.alternatives.entrySet()) {
            ITu tu = new OmTTu(e.getValue(), e.getKey(), targetLanguage);
            Key key = tu.getKey();
            assert(!tuMap.containsKey(key));
            assert(!tuvMap.containsKey(key));
            tuMap.put(key, tu);
            tuvMap.put(key, tu.getTargetTuv());
        }
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
        return Collections.EMPTY_MAP;
    }

    @Override
    public ITmx applyChanges(ResolutionSet resolution) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeTo(File outputFile) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getUnderlyingRepresentation() {
        return tmx;
    }
    
}
