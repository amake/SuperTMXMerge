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
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class Main {
    
    public static void main(String[] args) {
        
        // Set up look-and-feel stuff.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                "com.apple.mrj.application.apple.menu.about.name", "SuperTMXMerge");
        } catch (UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        
        final String[] theArgs = args;
        
        Runnable run = new Runnable() {
            @Override
            public void run() {
                // TODO: Use a proper args parsing library or something.
                
                if (theArgs.length == 0) {
                    SuperTmxMerge.promptForFiles();
                    return;
                }
                
                // The order of these tests is important!
                
                if ("--combine".equals(theArgs[0])) {
                    List<File> files = new ArrayList<File>();
                    File outputFile = null;
                    int i;
                    for (i = 1; i < theArgs.length; i++) {
                        if ("-o".equals(theArgs[i])) {
                            break;
                        }
                        File file = new File(theArgs[i]);
                        if (!files.contains(file)) {
                            files.add(file);
                        }
                    }
                    if (i < theArgs.length - 1) {
                        outputFile = new File(theArgs[i + 1]);
                    }
                    SuperTmxMerge.combineTo(outputFile, files.toArray(new File[0]));
                    return;
                } else if (theArgs.length == 2) {
                    SuperTmxMerge.diff(new File(theArgs[0]), new File(theArgs[1]));
                    return;
                } else if (theArgs.length == 3) {
                    SuperTmxMerge.merge(new File(theArgs[0]), new File(theArgs[1]), new File(theArgs[2]));
                    return;
                } else if (theArgs.length == 4) {
                    
                    if (theArgs[2].equals("-o")) {
                        SuperTmxMerge.diffTo(new File(theArgs[0]), new File(theArgs[1]), new File(theArgs[3]));
                        return;
                    } else {
                        SuperTmxMerge.mergeTo(new File(theArgs[0]), new File(theArgs[1]), new File(theArgs[2]),
                                new File(theArgs[3]));
                        return;
                    }
                }

                printUsage();
            }
        };
        
        GuiUtil.safelyRunBlockingRoutine(run);
    }
    
    private static void printUsage() {
        System.out.print(LocString.get("USAGE_DIRECTIONS"));
    }
}
