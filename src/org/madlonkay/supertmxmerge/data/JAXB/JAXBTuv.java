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

import gen.core.tmx14.Tuv;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.madlonkay.supertmxmerge.data.ITuv;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class JAXBTuv implements ITuv {
    
    public static final JAXBTuv EMPTY_TUV = new JAXBTuv(null);
    
    private static final char TAG_START_CHAR = '\uE101';
    private static final char TAG_END_CHAR = '\uE102';
    
    private final Tuv tuv;
    
    public JAXBTuv(Tuv tuv) {
        this.tuv = tuv;
    }
    
    @Override
    public String getContent() {
        if (tuv == null) {
            return null;
        }
        List<Object> content = tuv.getSeg().getContent();
        return content.isEmpty() ? "" : extractContent(content);
    }
    
    private String extractContent(List<Object> content) {
        StringBuilder tmp = new StringBuilder();
        for (Object o : content) {
            if (o instanceof String) {
                tmp.append((String) o);
            } else {
                try {
                    Method m = o.getClass().getDeclaredMethod("getContent", (Class<?>[]) null);
                    if (m == null) {
                        throw new RuntimeException("TUV contained item that didn't respond to getContent().");
                    }
                    Object subContent = m.invoke(o, (Object[]) null);
                    if (!(subContent instanceof List)) {
                        throw new RuntimeException("TUV contained item that didn't return a List from getContent().");
                    }
                    tmp.append(TAG_START_CHAR);
                    tmp.append(extractContent((List<Object>) subContent));
                    tmp.append(TAG_END_CHAR);
                } catch (NoSuchMethodException ex) {
                    // Nothing
                } catch (IllegalAccessException ex) {
                    // Nothing
                } catch (InvocationTargetException ex) {
                    // Nothing
                }
            }
        }
        return tmp.toString();
    }
    
    @Override
    public boolean equals(ITuv tuv) {
        if (this.tuv == null) {
            return tuv.getContent() == null;
        }
        return getContent().equals(tuv.getContent());
    }
    
    @Override
    public Object getUnderlyingRepresentation() {
        return tuv;
    }
    
    @Override
    public String getLanguage() {
        if (tuv == null) {
            return null;
        }
        return getLanguage(this.tuv);
    }
    
    public static String getLanguage(Tuv tuv) {
        if (tuv.getLang() != null) {
            return tuv.getLang();
        }
        return tuv.getXmlLang();
    }
}
