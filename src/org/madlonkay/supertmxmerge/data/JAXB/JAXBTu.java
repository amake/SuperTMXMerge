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

import gen.core.tmx14.Prop;
import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;
import org.madlonkay.supertmxmerge.data.ITu;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.data.Key;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class JAXBTu implements ITu {
    
    private final Tu tu;
    private final String sourceLanguage;
    
    public JAXBTu(Tu tu, String sourceLanguage) {
        this.tu = tu;
        this.sourceLanguage = sourceLanguage;
    }
    
    public ITuv getSourceTuv() {
        for (Tuv tuv : tu.getTuv()) {
            if (sourceLanguage.equalsIgnoreCase(JAXBTuv.getLanguage(tuv))) {
                return new JAXBTuv(tuv);
            }
        }
        return null;
    }
    
    @Override
    public Object getUnderlyingRepresentation() {
        return tu;
    }
    
    @Override
    public ITuv getTargetTuv() {
        for (Tuv tuv : tu.getTuv()) {
            if (!sourceLanguage.equalsIgnoreCase(JAXBTuv.getLanguage(tuv))) {
                return new JAXBTuv(tuv);
            }
        }
        return null;
    }
    
    public Key getKey() {
        ITuv sourceTuv = getSourceTuv();
        Key key;
        if (sourceTuv == null) {
            key = new Key(null, null);
            key.addProp("tuHashCode", String.valueOf(tu.hashCode()));
        } else {
            key = new Key(sourceTuv.getContent(), null);
        }
        for (Object o : tu.getNoteOrProp()) {
            if (o instanceof Prop) {
                Prop p = (Prop) o;
                key.addProp(p.getType(), p.getContent());
            }
        }
        return key;
    }
}
