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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class SuperTmxMergeTest {
    
    public SuperTmxMergeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of diff method, of class SuperTmxMerge.
     */
    @Test
    public void testDiff() {
        System.out.println("diff");
        File file1 = getFilePath("resources/base.tmx");
        File file2 = getFilePath("resources/left.tmx");
        SuperTmxMerge.diff(file1, file2);
    }

    /**
     * Test of merge method, of class SuperTmxMerge.
     */
    @Test
    public void testMerge() {
        System.out.println("merge");
        File baseFile = getFilePath("resources/base.tmx");
        File file1 = getFilePath("resources/left.tmx");
        File file2 = getFilePath("resources/right.tmx");
        SuperTmxMerge.merge(baseFile, file1, file2);
    }

    /**
     * Test of promptForFiles method, of class SuperTmxMerge.
     */
    @Test
    public void testPromptForFiles() {
        System.out.println("promptForFiles");
        SuperTmxMerge.promptForFiles();
    }
    
    private File getFilePath(String identifier) {
        return new File(getClass().getClassLoader().getResource(identifier).getFile());
    }
}