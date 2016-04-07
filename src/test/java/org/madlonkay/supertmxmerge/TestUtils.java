/*
 * Copyright (C) 2014 Aaron Madlon-Kay <aaron@madlon-kay.com>.
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

package org.madlonkay.supertmxmerge;

import java.io.File;
import org.madlonkay.supertmxmerge.data.DiffAnalysis;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.util.DiffUtil;
import static org.junit.Assert.*;
import org.madlonkay.supertmxmerge.data.ITmx;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class TestUtils {
    
    public static File getFilePath(String identifier) {
        return new File(TestUtils.class.getClassLoader().getResource(identifier).getFile());
    }
    
    public static void ensureEmptyDiff(ITmx file1, ITmx file2) throws Exception {
        DiffAnalysis diff = DiffUtil.mapDiff(file1, file2);
        assertTrue(diff.added.isEmpty());
        assertTrue(diff.deleted.isEmpty());
        assertTrue(diff.modified.isEmpty());
    }

    public static void ensureEmptyDiff(File file1, File file2) throws Exception {
        ensureEmptyDiff(new JAXBTmx(file1), new JAXBTmx(file2));
    }
    
    public static void ensureEmptyDiff(ITmx file1, File file2) throws Exception {
        ensureEmptyDiff(file1, new JAXBTmx(file2));
    }
    
}
