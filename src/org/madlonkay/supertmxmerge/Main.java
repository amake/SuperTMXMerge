/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge;

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
        List<TuDiffInfo> diffs = new ArrayList<>();
        diffs.add(new TuDiffInfo("Blah", "en-us", "ja-jp", "など", "などなど"));
        diffs.add(new TuDiffInfo("What", "en-us", "ja-jp", "何", "なぁに～"));
        diffs.add(new TuDiffInfo("What in the world is going on here, pray tell? \nmonkey1 monkey2 monkey3 monkey4 monkey5", "en-us", "ja-jp", "これは一体何なんだ！", "一体これはなんということだ！"));
        TmxInfo tmx1 = new TmxInfo("tmx1.tmx", 35);
        TmxInfo tmx2 = new TmxInfo("tmx2.tmx", 32);
        DiffWindow window = new DiffWindow(tmx1, tmx2, diffs);
        window.setVisible(true);
    }
}
