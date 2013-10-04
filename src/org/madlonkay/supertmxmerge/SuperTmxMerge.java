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
package org.madlonkay.supertmxmerge;

import java.io.File;
import java.util.logging.Logger;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.gui.FileSelectWindow;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.omegat.core.data.OmTTmx;
import org.omegat.core.data.ProjectTMX;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class SuperTmxMerge {
    
    private static final Logger LOGGER = Logger.getLogger(SuperTmxMerge.class.getName());
    
    public static void diff(File file1, File file2) {
        DiffIOController controller = new DiffIOController();
        controller.setFile1(file1);
        controller.setFile2(file2);
        controller.go();
    }
    
    public static File merge(File baseFile, File file1, File file2) {
        MergeIOController controller = new MergeIOController();
        controller.setBaseFile(baseFile);
        controller.setFile1(file1);
        controller.setFile2(file2);
        controller.setOutputFile(null);
        controller.go();
        return controller.getOutputFile();
    }
    
    public static ProjectTMX merge(ProjectTMX baseTmx, String baseTmxName,
            ProjectTMX tmx1, String tmx1Name,
            ProjectTMX tmx2, String tmx2Name,
            String sourceLanguage, String targetLanguage) {
        
        MergeController controller = new MergeController();
        ITmx base = new OmTTmx(baseTmx, baseTmxName, sourceLanguage, targetLanguage);
        ITmx one = new OmTTmx(tmx1, tmx1Name, sourceLanguage, targetLanguage);
        ITmx two = new OmTTmx(tmx2, tmx2Name, sourceLanguage, targetLanguage);
        ITmx merged = controller.merge(base, one, two);
        return merged == null ? null : (ProjectTMX) merged.getUnderlyingRepresentation();
    }
    
    public static void mergeTo(File baseFile, File file1, File file2, File outputFile) {
        MergeIOController controller = new MergeIOController();
        controller.setBaseFile(baseFile);
        controller.setFile1(file1);
        controller.setFile2(file2);
        controller.setOutputFile(outputFile);
        controller.go();
    }
    
    public static void promptForFiles() {
        FileSelectWindow window = new FileSelectWindow();
        GuiUtil.displayWindow(window);
    }
    
}
