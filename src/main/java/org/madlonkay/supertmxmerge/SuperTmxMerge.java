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
package org.madlonkay.supertmxmerge;

import java.awt.Window;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.madlonkay.supertmxmerge.data.DiffAnalysis;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.MergeAnalysis;
import org.madlonkay.supertmxmerge.data.Report;
import org.madlonkay.supertmxmerge.data.ResolutionSet;
import org.madlonkay.supertmxmerge.gui.FileSelectWindow;
import org.madlonkay.supertmxmerge.util.DiffUtil;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;
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
    
    public static void diffTo(File file1, File file2, File outFile) {
        DiffIOController controller = new DiffIOController();
        controller.setFile1(file1);
        controller.setFile2(file2);
        controller.setOutputFile(outFile);
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
    
    public static ProjectTMX merge(ITmx baseTmx, ITmx tmx1, ITmx tmx2, StmProperties properties) {
        
        if (properties == null) {
            properties = new StmProperties();
        }
        
        LocString.addBundle(properties.getLanguageResource());
        
        MergeController controller = new MergeController();
        controller.setQuiet(true);
        controller.setCanCancel(false);
        controller.setParentWindow(properties.getParentWindow());
        controller.setListViewThreshold(properties.getListViewThreshold());

        MergeAnalysis analysis = controller.analyze(baseTmx, tmx1, tmx2);
        ResolutionSet resolution = controller.resolve(properties.getResolutionStrategy());
        ITmx wrappedResult = controller.apply(resolution);
        if (wrappedResult != null) {
            DiffAnalysis diff = DiffUtil.mapDiff(tmx2, wrappedResult);
            properties.setReport(new Report(analysis, diff));
            return (ProjectTMX) wrappedResult.getUnderlyingRepresentation();
        }
        return null;
    }
    
    public static void mergeTo(File baseFile, File file1, File file2, File outputFile) {
        MergeIOController controller = new MergeIOController();
        controller.setBaseFile(baseFile);
        controller.setFile1(file1);
        controller.setFile2(file2);
        controller.setOutputFile(outputFile);
        controller.go();
    }
    
    public static void combine(File... files) {
        combineTo(null, files);
    }
    
    public static void combineTo(File outputFile, File... files) {
        CombineIOController controller = new CombineIOController();
        controller.setFiles(files);
        controller.setOutputFile(outputFile);
        controller.go();
    }
    
    public static void promptForFiles() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Window window = FileSelectWindow.newAsFrame();
                GuiUtil.displayWindow(window);
            }
        });
    }
    
}
