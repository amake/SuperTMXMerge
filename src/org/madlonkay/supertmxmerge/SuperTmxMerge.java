/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import java.util.logging.Logger;
import org.madlonkay.supertmxmerge.gui.FileSelectWindow;

/**
 *
 * @author aaron.madlon-kay
 */
public class SuperTmxMerge {
    
    public static final Logger LOGGER = Logger.getLogger(SuperTmxMerge.class.getName());
    
    public static void diff(String file1, String file2) {
        DiffController controller = new DiffController();
        controller.setFile1(file1);
        controller.setFile2(file2);
        controller.go();
    }
    
    public static void merge(String baseFile, String file1, String file2) {
        MergeController controller = new MergeController();
        controller.setBaseFile(baseFile);
        controller.setLeftFile(file1);
        controller.setRightFile(file2);
        controller.go();
    }
    
    public static void promptForFiles() {
        FileSelectWindow window = new FileSelectWindow();
        window.setVisible(true);
        window.pack();
    }
    
}
