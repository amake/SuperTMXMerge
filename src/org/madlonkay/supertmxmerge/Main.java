/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.madlonkay.supertmxmerge.gui.FileSelectWindow;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author aaron.madlon-kay
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length == 0) {
            final FileSelectWindow window = new FileSelectWindow();
            window.setCallback(new Runnable() {
                @Override
                public void run() {
                    if (window.isDiff()) {
                        SuperTmxMerge.diff(window.getFile1(), window.getFile2());
                    } else if (window.isMerge()) {
                        SuperTmxMerge.merge(window.getBaseFile(), window.getLeftFile(), window.getRightFile());
                    }
                }
            });
            window.setVisible(true);
            window.pack();
            return;
        }
        
        boolean die = false;
        for (String arg : args) {
            File file = new File(arg);
            if (!file.exists() || !file.canRead()) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, LocString.getFormat("error_bad_file", arg));
                die = true;
            }
        }
        
        if (die) {
            printUsage();
            return;
        }
                
        if (args.length == 2) {
            SuperTmxMerge.diff(args[0], args[1]);
            return;
        } else if (args.length == 3) {
            SuperTmxMerge.merge(args[0], args[1], args[2]);
            return;
        }
        
        printUsage();
    }
    
    private static void printUsage() {
        System.out.print(LocString.get("usage_directions"));
    }
}
