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
    private String sourceLanguage;
    
    public JAXBTu(Tu tu, String sourceLanguage) {
        this.tu = tu;
        this.sourceLanguage = sourceLanguage;
    }
    
    public ITuv getSourceTuv() {
        for (Tuv tuv : tu.getTuv()) {
            if (sourceLanguage.equals(JAXBTuv.getLanguage(tuv))) {
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
            if (!sourceLanguage.equals(JAXBTuv.getLanguage(tuv))) {
                return new JAXBTuv(tuv);
            }
        }
        return null;
    }
    
    @Override
    public Key getKey() {
        Key key = new Key(getSourceTuv().getContent());
        for (Object o : tu.getNoteOrProp()) {
            if (o instanceof Prop) {
                Prop p = (Prop) o;
                key.addProp(p.getType(), p.getContent());
            }
        }
        return key;
    }
}
