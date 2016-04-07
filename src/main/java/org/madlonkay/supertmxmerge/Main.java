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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class Main {
    
    public static void main(final String[] args) {
        
        // Set up look-and-feel stuff.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "SuperTMXMerge");
        } catch (UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        
        // TODO: Use a proper args parsing library or something.
                
        try {
            if (args.length == 0) {
                
                SuperTmxMerge.promptForFiles();
                return;
            }

            // The order of these tests is important!

            if ("--combine".equals(args[0])) {
                List<File> files = new ArrayList<File>();
                File outputFile = null;
                int i;
                for (i = 1; i < args.length; i++) {
                    if ("-o".equals(args[i])) {
                        break;
                    }
                    File file = new File(args[i]);
                    if (!files.contains(file)) {
                        files.add(file);
                    }
                }
                if (i < args.length - 1) {
                    outputFile = new File(args[i + 1]);
                }
                SuperTmxMerge.combineTo(outputFile, files.toArray(new File[0]));
                return;
            } else if (args.length == 2) {
                SuperTmxMerge.diff(new File(args[0]), new File(args[1]));
                return;
            } else if (args.length == 3) {
                SuperTmxMerge.merge(new File(args[0]), new File(args[1]), new File(args[2]));
                return;
            } else if (args.length == 4) {

                if (args[2].equals("-o")) {
                    SuperTmxMerge.diffTo(new File(args[0]), new File(args[1]), new File(args[3]));
                    return;
                } else {
                    SuperTmxMerge.mergeTo(new File(args[0]), new File(args[1]), new File(args[2]),
                            new File(args[3]));
                    return;
                }
            }

            printUsage();
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
    
    private static void printUsage() {
        System.out.print(LocString.get("STM_USAGE_DIRECTIONS"));
    }
}
