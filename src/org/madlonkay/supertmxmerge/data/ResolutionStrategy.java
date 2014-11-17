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
package org.madlonkay.supertmxmerge.data;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public abstract class ResolutionStrategy<TmxType extends ITmx<?,TuType,TuvType>, TuType extends ITu, TuvType extends ITuv> {
    
    public static final ResolutionStrategy BASE = new ResolutionStrategy() {
        @Override
        public ITuv resolveConflict(Key key, ITuv baseTuv, ITuv leftTuv, ITuv rightTuv) {
            return baseTuv;
        }
    };
    
    public static final ResolutionStrategy LEFT = new ResolutionStrategy() {
        @Override
        public ITuv resolveConflict(Key key, ITuv baseTuv, ITuv leftTuv, ITuv rightTuv) {
            return leftTuv;
        }
    };
    
    public static final ResolutionStrategy RIGHT = new ResolutionStrategy() {
        @Override
        public ITuv resolveConflict(Key key, ITuv baseTuv, ITuv leftTuv, ITuv rightTuv) {
            return rightTuv;
        }
    };
    
    private ResolutionSet<TuType,TuvType> resolution;
    
    public ResolutionStrategy() {}
    
    public ResolutionSet resolve(MergeAnalysis<Key,TuvType> analysis, TmxType baseTmx, TmxType leftTmx, TmxType rightTmx) {
        resolution = ResolutionSet.fromAnalysis(analysis, leftTmx, rightTmx);
        
        for (Key key : analysis.conflicts) {
            ITuv baseTuv = baseTmx.get(key);
            ITuv leftTuv = leftTmx.get(key);
            ITuv rightTuv = rightTmx.get(key);
            
            ITuv selection = resolveConflict(key, baseTuv, leftTuv, rightTuv);
            
            if (selection == baseTuv) {
                // No change
            } else if (selection == leftTuv) {
                dispatchKey(key, baseTmx, leftTmx);
            } else if (selection == rightTuv) {
                dispatchKey(key, baseTmx, rightTmx);
            } else {
                throw new RuntimeException("ResolutionStrategy resolved conflict with unknown ITuv.");
            }
        }
        
        return resolution;
    }

    public abstract ITuv resolveConflict(Key key, ITuv baseTuv, ITuv leftTuv, ITuv rightTuv);
    
    private void dispatchKey(Key key, TmxType baseTmx, TmxType thisTmx) {
        if (!thisTmx.containsKey(key)) {
            resolution.toDelete.add(key);
        } else if (baseTmx.containsKey(key)) {
            resolution.toReplace.put(key, thisTmx.get(key));
        } else {
            resolution.toAdd.put(key, thisTmx.getTu(key));
        }
    }
}
