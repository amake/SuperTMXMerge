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
public class JAXBTu implements ITu<Tu,JAXBTuv> {
    
    private final Tu tu;
    private final String sourceLanguage;
    
    public JAXBTu(Tu tu, String sourceLanguage) {
        this.tu = tu;
        this.sourceLanguage = sourceLanguage;
    }
    
    public ITuv<Tuv> getSourceTuv() {
        for (Tuv tuv : tu.getTuv()) {
            if (sourceLanguage.equalsIgnoreCase(JAXBTuv.getLanguage(tuv))) {
                return new JAXBTuv(tuv);
            }
        }
        return null;
    }
    
    @Override
    public Tu getUnderlyingRepresentation() {
        return tu;
    }
    
    @Override
    public JAXBTuv getTargetTuv() {
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
        // If there is no relevant source TUV, make a null key with
        // all the TUV contents as properties.
        if (sourceTuv == null) {
            key = new Key(null, null);
            for (Tuv tuv : tu.getTuv()) {
                key.addProp(JAXBTuv.getLanguage(tuv), new JAXBTuv(tuv).getContent());
            }
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
