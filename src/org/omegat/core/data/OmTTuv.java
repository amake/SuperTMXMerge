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

import java.util.Collections;
import java.util.Map;
import org.madlonkay.supertmxmerge.data.ITuv;
import org.madlonkay.supertmxmerge.util.ReflectionUtil;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class OmTTuv implements ITuv {

    private final TMXEntry tmxEntry;
    private final String language;
    private Map<String, String> props;
    
    public OmTTuv(TMXEntry tmxEntry, String language) {
        this.tmxEntry = tmxEntry;
        this.language = language;
    }
    
    @Override
    public String getContent() {
        return tmxEntry.translation;
    }

    @Override
    public Map<String, String> getMetadata() {
        if (props == null) {
            props = Collections.unmodifiableMap(ReflectionUtil.simpleMembersToMap(tmxEntry));
        }
        return props;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public Object getUnderlyingRepresentation() {
        return tmxEntry;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OmTTuv other = (OmTTuv) obj;
        return tmxEntry.equalsTranslation(other.tmxEntry) &&
                this.language.equals(other.language);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.tmxEntry != null ? this.tmxEntry.hashCode() : 0);
        hash = 89 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equalsImportantMetadata(ITuv o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        final OmTTuv other = (OmTTuv) o;
        if (this.tmxEntry.note == null && other.tmxEntry.note == null) {
            return true;
        }
        if (this.tmxEntry.note != null && other.tmxEntry.note != null) {
            return this.tmxEntry.note.equals(other.tmxEntry.note);
        }
        return false;
    }

    @Override
    public int compareTo(ITuv o) {
        if (this == o)
            return 0;
        if (o == null)
            return 1;
        if (getClass() != o.getClass())
            throw new RuntimeException("Can't compare an OmTTuv with a non-OmTTuv");
        final OmTTuv other = (OmTTuv) o;
        return this.tmxEntry.changeDate < other.tmxEntry.changeDate ? -1 :
                this.tmxEntry.changeDate == other.tmxEntry.changeDate ? 0 : 1;
    }
}
