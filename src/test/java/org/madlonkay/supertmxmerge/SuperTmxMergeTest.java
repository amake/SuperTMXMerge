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
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.madlonkay.supertmxmerge.data.DiffAnalysis;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.util.DiffUtil;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class SuperTmxMergeTest {
    
    private static final boolean DO_GUI_TESTS = false;
    
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

    @Test
    public void testDiffGui() {
        File file1 = TestUtils.getFilePath("resources/base.tmx");
        File file2 = TestUtils.getFilePath("resources/left.tmx");
        if (DO_GUI_TESTS) SuperTmxMerge.diff(file1, file2);
    }

    @Test
    public void testMergeGui() {
        File baseFile = TestUtils.getFilePath("resources/base.tmx");
        File file1 = TestUtils.getFilePath("resources/left.tmx");
        File file2 = TestUtils.getFilePath("resources/right.tmx");
        if (DO_GUI_TESTS) SuperTmxMerge.merge(baseFile, file1, file2);
    }

    @Test
    public void testPromptForFilesGui() {
        if (DO_GUI_TESTS) SuperTmxMerge.promptForFiles();
    }
    
    @Test
    public void testDiff() throws Exception {
        File baseFile = TestUtils.getFilePath("resources/base.tmx");
        File file1 = TestUtils.getFilePath("resources/left.tmx");
        
        DiffAnalysis diff = DiffUtil.mapDiff(new JAXBTmx(baseFile), new JAXBTmx(file1));
        assertEquals(diff.added.size(), 2);
        assertEquals(diff.deleted.size(), 2);
        assertEquals(diff.modified.size(), 5);
    }
    
    @Test
    public void testDiffTo() throws Exception {
        File baseFile = TestUtils.getFilePath("resources/base.tmx");
        File file1 = TestUtils.getFilePath("resources/left.tmx");
        File outFile = new File(file1.getParentFile(), "output.tmx");
        SuperTmxMerge.diffTo(baseFile, file1, outFile);
        
        File goldFile = TestUtils.getFilePath("resources/gold/diffToGold.tmx");
        
        TestUtils.ensureEmptyDiff(outFile, goldFile);
        
        outFile.delete();
    }
    
    @Test
    public void testMergeTo() throws Exception {
        File baseFile = TestUtils.getFilePath("resources/base.tmx");
        File file1 = TestUtils.getFilePath("resources/left-no-conflict.tmx");
        File file2 = TestUtils.getFilePath("resources/right-no-conflict.tmx");
        File outFile = new File(file1.getParentFile(), "output.tmx");
        SuperTmxMerge.mergeTo(baseFile, file1, file2, outFile);
        
        File goldFile = TestUtils.getFilePath("resources/gold/mergeToGold.tmx");
        
        TestUtils.ensureEmptyDiff(outFile, goldFile);
        
        outFile.delete();
    }
    
    @Test
    public void testMergeBidirectional() throws Exception {
        File baseFile = TestUtils.getFilePath("resources/base.tmx");
        File file1 = TestUtils.getFilePath("resources/left-no-conflict.tmx");
        File file2 = TestUtils.getFilePath("resources/right-no-conflict.tmx");
        File outFile1 = new File(file1.getParentFile(), "output1.tmx");
        SuperTmxMerge.mergeTo(baseFile, file1, file2, outFile1);
        
        File outFile2 = new File(file1.getParentFile(), "output2.tmx");
        SuperTmxMerge.mergeTo(baseFile, file2, file1, outFile2);
                
        TestUtils.ensureEmptyDiff(outFile1, outFile2);
        
        outFile1.delete();
        outFile2.delete();
    }
    
    @Test
    public void testMergeBaseless() throws Exception {
        File baseFile = null;
        File file1 = TestUtils.getFilePath("resources/left-no-conflict.tmx");
        File file2 = TestUtils.getFilePath("resources/right-no-conflict-baseless.tmx");
        File outFile = new File(file1.getParentFile(), "output.tmx");
        SuperTmxMerge.mergeTo(baseFile, file1, file2, outFile);
        
        File goldFile = TestUtils.getFilePath("resources/gold/baselessMergeToGold.tmx");
                
        TestUtils.ensureEmptyDiff(outFile, goldFile);
        
        outFile.delete();
    }
    
    @Test
    public void testCombineTo() throws Exception {
        File file1 = TestUtils.getFilePath("resources/base-part1.tmx");
        File file2 = TestUtils.getFilePath("resources/base-part2.tmx");
        File file3 = TestUtils.getFilePath("resources/base-part3.tmx");
        File outFile = new File(file1.getParentFile(), "output.tmx");
        SuperTmxMerge.combineTo(outFile, file1, file2, file3);
        
        File goldFile = TestUtils.getFilePath("resources/base.tmx");
        TestUtils.ensureEmptyDiff(outFile, goldFile);
        
        outFile.delete();
    }
}