/*
 * Copyright (C) 2014 Aaron Madlon-Kay <aaron@madlon-kay.com>.
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.data.MergeAnalysis;
import org.madlonkay.supertmxmerge.data.ResolutionStrategy;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class MergeControllerTest {
    
    public MergeControllerTest() {
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
     * Test of manual resolution, of class MergeController.
     * @throws java.lang.Exception
     */
    @Test
    public void testManualResolution() throws Exception {        
        File baseFile = TestUtils.getFilePath("resources/base.tmx");
        File file1 = TestUtils.getFilePath("resources/left.tmx");
        File file2 = TestUtils.getFilePath("resources/right.tmx");
        File outFile = new File(file1.getParentFile(), "output.tmx");
        
        
        MergeController merger = new MergeController();
        MergeAnalysis analysis = merger.analyze(new JAXBTmx(baseFile),
                new JAXBTmx(file1), new JAXBTmx(file2));
        
        ITmx result = merger.resolve(ResolutionStrategy.LEFT);
        File goldFile = TestUtils.getFilePath("resources/gold/manualResolveLeftGold.tmx");
        TestUtils.ensureEmptyDiff(result, goldFile);
        outFile.delete();
        
        result = merger.resolve(ResolutionStrategy.BASE);
        goldFile = TestUtils.getFilePath("resources/gold/manualResolveBaseGold.tmx");
        TestUtils.ensureEmptyDiff(result, goldFile);
        outFile.delete();
        
        result = merger.resolve(ResolutionStrategy.RIGHT);
        goldFile = TestUtils.getFilePath("resources/gold/manualResolveRightGold.tmx");
        TestUtils.ensureEmptyDiff(result, goldFile);
        outFile.delete();
    }
}
