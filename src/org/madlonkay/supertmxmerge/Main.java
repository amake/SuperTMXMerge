/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

import org.madlonkay.supertmxmerge.data.DiffInfo;
import org.madlonkay.supertmxmerge.data.TmxInfo;
import java.util.ArrayList;
import java.util.List;
import org.madlonkay.supertmxmerge.gui.DiffWindow;

/**
 *
 * @author aaron.madlon-kay
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<DiffInfo> diffs = new ArrayList<>();
        diffs.add(new DiffInfo("Blah", "en-us", "ja-jp", "など", "などなど"));
        diffs.add(new DiffInfo("What", "en-us", "ja-jp", "何", "なぁに～"));
        diffs.add(new DiffInfo("What in the world is going on here, pray tell? \nmonkey1 monkey2 monkey3 monkey4 monkey5", "en-us", "ja-jp", "これは一体何なんだ！", "一体これはなんということだ！"));
        TmxInfo tmx1 = new TmxInfo("tmx1.tmx", 35);
        TmxInfo tmx2 = new TmxInfo("tmx2.tmx", 32);
        //DiffWindow window = new DiffWindow(tmx1, tmx2, diffs);
        //window.setVisible(true);
        //window.pack();
        
        //SuperTmxMerge.diff("C:\\Users\\aaron.madlon-kay\\Desktop\\Behavior+Cloth-old.tmx", "C:\\Users\\aaron.madlon-kay\\Desktop\\Behavior+Cloth-new.tmx");
        SuperTmxMerge.merge("C:\\Users\\aaron.madlon-kay\\Desktop\\Behavior+Cloth-base.tmx",
                "C:\\Users\\aaron.madlon-kay\\Desktop\\Behavior+Cloth-old.tmx",
                "C:\\Users\\aaron.madlon-kay\\Desktop\\Behavior+Cloth-new.tmx");
    }
}
