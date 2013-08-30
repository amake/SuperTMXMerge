/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import java.util.logging.Logger;
import org.madlonkay.supertmxmerge.gui.FileSelectWindow;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author aaron.madlon-kay
 */
public class SuperTmxMerge {
    
    private static final DiffController DIFF_CONTROLLER = new DiffController();
    private static final MergeController MERGE_CONTROLLER = new MergeController();
    public static final Logger LOGGER = Logger.getLogger(SuperTmxMerge.class.getName());
    
    public static MergeController getMergeController() {
        return MERGE_CONTROLLER;
    }
    
    public static DiffController getDiffController() {
        return DIFF_CONTROLLER;
    }
    
    public static void diff(String file1, String file2) {
        DIFF_CONTROLLER.setFile1(file1);
        DIFF_CONTROLLER.setFile2(file2);
        DIFF_CONTROLLER.go();
    }
    
    
    
    public static void merge(String baseFile, String file1, String file2) {
        MERGE_CONTROLLER.setBaseFile(baseFile);
        MERGE_CONTROLLER.setLeftFile(file1);
        MERGE_CONTROLLER.setRightFile(file2);
        MERGE_CONTROLLER.go();
    }
    
    public static void promptForFiles() {
        FileSelectWindow window = new FileSelectWindow();
        window.setVisible(true);
        window.pack();
    }
    
}
