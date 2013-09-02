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
package org.madlonkay.supertmxmerge.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class FileUtil {
    
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    
    public static boolean validateFile(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() || ! file.canRead()) {
            LOGGER.log(Level.SEVERE, LocString.getFormat("error_bad_file", path));
            return false;
        }
        return true;
    }
}
