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

import java.awt.Window;
import java.util.ResourceBundle;
import org.madlonkay.supertmxmerge.data.ResolutionStrategy;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class StmProperties {
    
    private String baseTmxName = LocString.get("STM_BASE_TMX_DEFAULT_NAME");
    private String leftTmxName = LocString.get("STM_LEFT_TMX_DEFAULT_NAME");
    private String rightTmxName = LocString.get("STM_RIGHT_TMX_DEFAULT_NAME");
    private ResourceBundle resource = null;
    private Window parentWindow = null;
    private int listViewThreshold = 5;
    private ResolutionStrategy resolutionStrategy = null;

    public StmProperties() {
    }
    
    public StmProperties setBaseTmxName(String name) {
        this.baseTmxName = name;
        return this;
    }
    
    public String getBaseTmxName() {
        return baseTmxName;
    }
    
    public StmProperties setTmx1Name(String name) {
        this.leftTmxName = name;
        return this;
    }
    
    public String geTmx1Name() {
        return leftTmxName;
    }
    
    public StmProperties setTmx2Name(String name) {
        this.rightTmxName = name;
        return this;
    }
    
    public String getTmx2Name() {
        return rightTmxName;
    }
    
    public StmProperties setLanguageResource(ResourceBundle resource) {
        this.resource = resource;
        return this;
    }
    
    public ResourceBundle getLanguageResource() {
        return resource;
    }
    
    public StmProperties setParentWindow(Window window) {
        this.parentWindow = window;
        return this;
    }
    
    public Window getParentWindow() {
        return parentWindow;
    }
    
    public StmProperties setListViewThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("The list view threshold must be at least 0.");
        }
        this.listViewThreshold = threshold;
        return this;
    }
    
    public int getListViewThreshold() {
        return listViewThreshold;
    }

    public ResolutionStrategy getResolutionStrategy() {
        return this.resolutionStrategy;
    }
    
    public void setResolutionStrategy(ResolutionStrategy resolutionStrategy) {
        this.resolutionStrategy = resolutionStrategy;
    }
}
