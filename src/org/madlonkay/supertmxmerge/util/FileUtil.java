/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.util;

import java.io.File;
import java.util.logging.Level;
import static org.madlonkay.supertmxmerge.MergeController.LOGGER;

/**
 *
 * @author amake
 */
public class FileUtil {
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
