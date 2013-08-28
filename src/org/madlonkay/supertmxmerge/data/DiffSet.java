/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.data;

import java.util.Set;

/**
 *
 * @author aaron.madlon-kay
 */
public class DiffSet {
    public Set<String> deleted;
    public Set<String> added;
    public Set<String> modified;
    
    public DiffSet(Set<String> deleted, Set<String> added, Set<String> modified) {
        this.deleted = deleted;
        this.added = added;
        this.modified = modified;
    }
}
