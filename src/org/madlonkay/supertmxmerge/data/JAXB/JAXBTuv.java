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

import gen.core.tmx14.Tuv;
import java.util.List;
import org.madlonkay.supertmxmerge.data.ITuv;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class JAXBTuv implements ITuv {
    
    private final Tuv tuv;
    
    public JAXBTuv(Tuv tuv) {
        this.tuv = tuv;
    }
    
    @Override
    public String getContent() {
        List<Object> content = tuv.getSeg().getContent();
        return content.isEmpty() ? "" : (String) content.get(0);
    }
    
    @Override
    public boolean equals(ITuv tuv) {
        return getContent().equals(tuv.getContent());
    }
    
    @Override
    public Object getUnderlyingRepresentation() {
        return tuv;
    }
    
    @Override
    public String getLanguage() {
        return getLanguage(this.tuv);
    }
    
    public static String getLanguage(Tuv tuv) {
        if (tuv.getLang() != null) {
            return tuv.getLang();
        }
        return tuv.getXmlLang();
    }
}
