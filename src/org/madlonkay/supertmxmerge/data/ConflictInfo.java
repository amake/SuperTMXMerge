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
package org.madlonkay.supertmxmerge.data;

import bmsi.util.Diff;
import org.madlonkay.supertmxmerge.util.DiffUtil;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class ConflictInfo {
    public final String sourceText;
    public final String sourceLanguage;
    
    public final String targetLanguage;
    public final String baseTuvText;
    public final String leftTuvText;
    public final String rightTuvText;
    
    public final Diff.change leftTuvDiff;
    public final Diff.change rightTuvDiff;
    
    public ConflictInfo(String sourceText, String sourceLanguage,
            String targetLanguage, String baseTuvText, String leftTuvText, String rightTuvText) {
        this.sourceText = sourceText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.baseTuvText = baseTuvText;
        this.leftTuvText = leftTuvText;
        this.rightTuvText = rightTuvText;
        this.leftTuvDiff = DiffUtil.getCharacterDiff(baseTuvText, leftTuvText);
        this.rightTuvDiff = DiffUtil.getCharacterDiff(baseTuvText, rightTuvText);
    }
}
