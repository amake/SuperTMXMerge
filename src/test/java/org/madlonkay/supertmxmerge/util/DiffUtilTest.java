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

package org.madlonkay.supertmxmerge.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.madlonkay.supertmxmerge.data.DiffAnalysis;
import org.madlonkay.supertmxmerge.data.MergeAnalysis;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class DiffUtilTest {
    
    public DiffUtilTest() {
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
     * Test of mapDiff method, of class DiffUtil.
     */
    @Test
    public void testMapDiff() {
        Map<String,String> before = makeMap(
                new String[] { "a", "b", "c" },
                new String[] { "one", "two", "three" });
        
        Map<String,String> after = new HashMap<String, String>(before);
        
        DiffAnalysis<String> result = DiffUtil.mapDiff(before, after); 
        checkAnalysis(result, 0, 0, 0);
        
        before.put("d", "four");
        result = DiffUtil.mapDiff(before, after);
        checkAnalysis(result, 0, 1, 0);
        
        after.put("e", "five");
        result = DiffUtil.mapDiff(before, after); 
        checkAnalysis(result, 1, 1, 0);
        
        before.put("f", "six");
        after.put("f", "six?");
        result = DiffUtil.mapDiff(before, after);
        checkAnalysis(result, 1, 1, 1);
    }

    /**
     * Test of mapMerge method, of class DiffUtil.
     */
    @Test
    public void testMapMerge() {
        Map<String,String> base = makeMap(
                new String[] { "a", "b", "c" },
                new String[] { "one", "two", "three" });
        Map<String,String> left = new HashMap<String, String>(base);
        Map<String,String> right = new HashMap<String, String>(base);
        
        MergeAnalysis<String,String> result = DiffUtil.mapMerge(base, left, right);
        checkAnalysis(result, 0, 0, 0, 0);
        
        base.clear();
        left.clear();
        right.clear();
        
        // Check item in base, not present in left and right.
        base.put("d", "four");
        result = DiffUtil.mapMerge(base, left, right);
        checkAnalysis(result, 0, 0, 1, 0);
        
        // Check item in base and left, not present in right.
        left.put("d", "four");
        result = DiffUtil.mapMerge(base, left, right);
        checkAnalysis(result, 0, 0, 1, 0);
        
        left.clear();
        
        // Check item in base and right, deleted in left.
        right.put("d", "four");
        result = DiffUtil.mapMerge(base, left, right);
        checkAnalysis(result, 0, 0, 1, 0);
        
        right.clear();
        base.clear();
        
        // Check item added to left, not present in base or right.
        left.put("e", "five");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 0, 1, 0, 0);
        
        left.clear();
        
        // Check item added to right, not present in base or left.
        right.put("e", "five");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 0, 1, 0, 0);
        
        // Check item added to left and right, not present in base.
        left.put("e", "five");
        right.put("e", "five");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 0, 1, 0, 0);
        
        left.clear();
        right.clear();
        
        // Check item in base modified in left, not present in right.
        left.put("f", "six?");
        base.put("f", "six");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 1, 0, 0, 0);
        
        left.clear();
        
        // Check item in base modified in right, not present in left.
        right.put("f", "six?");
        base.put("f", "six");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 1, 0, 0, 0);
        
        // Check item in base modified in left and right.
        left.put("f", "six?");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 0, 0, 0, 1);
        
        // Check item in base and right modified in left.
        right.put("f", "six");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 0, 0, 0, 1);
        
        // Check item in base and left modified in right.
        left.put("f", "six");
        right.put("f", "six?");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 0, 0, 0, 1);
        
        // Check item in base modified differently in left and right.
        left.put("f", "six!");
        right.put("f", "six?");
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 1, 0, 0, 0);
        
        base.clear();
        
        // Check item missing from base modified differently in left and right.
        result = DiffUtil.mapMerge(base, left, right); 
        checkAnalysis(result, 1, 0, 0, 0);
    }
    
    private <T> Map<T,T> makeMap(T[] keys, T[] values) {
        assertEquals(keys.length, values.length);
        Map<T,T> result = new HashMap<T, T>();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }
        return result;
    }
    
    private void checkAnalysis(MergeAnalysis<?, ?> analysis, int conflicts,
            int added, int deleted, int modified) {
        assertEquals(analysis.conflicts.size(), conflicts);
        assertEquals(analysis.added.size(), added);
        assertEquals(analysis.deleted.size(), deleted);
        assertEquals(analysis.modified.size(), modified);
    }
    
    private void checkAnalysis(DiffAnalysis<?> analysis,
            int added, int deleted, int modified) {
        assertEquals(analysis.added.size(), added);
        assertEquals(analysis.deleted.size(), deleted);
        assertEquals(analysis.modified.size(), modified);
    }
}
