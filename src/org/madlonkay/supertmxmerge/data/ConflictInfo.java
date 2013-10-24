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
package org.madlonkay.supertmxmerge.data;

import bmsi.util.Diff;
import org.madlonkay.supertmxmerge.util.DiffUtil;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ConflictInfo {
    public final Key key;
    public final String sourceLanguage;
    
    public final String targetLanguage;
    public final String baseTuvText;
    public final String leftTuvText;
    public final String rightTuvText;
    
    public final Diff.change leftTuvDiff;
    public final Diff.change rightTuvDiff;
    
    public ConflictInfo(Key key, String sourceLanguage,
            String targetLanguage, String baseTuvText, String leftTuvText, String rightTuvText) {
        this.key = key;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.baseTuvText = baseTuvText;
        this.leftTuvText = leftTuvText;
        this.rightTuvText = rightTuvText;
        this.leftTuvDiff = DiffUtil.getCharacterDiff(baseTuvText, leftTuvText);
        this.rightTuvDiff = DiffUtil.getCharacterDiff(baseTuvText, rightTuvText);
    }
}
