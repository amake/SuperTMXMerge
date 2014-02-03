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
    
    public static boolean validateFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists() || !file.canRead()) {
            LOGGER.log(Level.SEVERE, LocString.getFormat("ERROR_BAD_FILE", file.getAbsolutePath()));
            return false;
        }
        return true;
    }
}
