/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.madlonkay.supertmxmerge.data;

import gen.core.tmx14.Tmx;
import gen.core.tmx14.Tuv;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import org.madlonkay.supertmxmerge.SuperTmxMerge;
import org.madlonkay.supertmxmerge.util.TuvUtil;

/**
 *
 * @author aaron.madlon-kay
 */
public class TmxFile {

    private static Unmarshaller u = null;
    private Tmx doc = null;
    private String filepath = null;
    private Map<String, Tuv> anonymousTuvs = null;

    static {
        try {
            JAXBContext jc = JAXBContext.newInstance(Tmx.class.getPackage().getName());
            u = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger(SuperTmxMerge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TmxFile(String filepath) throws UnmarshalException {
        this.filepath = filepath;
        try {
            this.doc = (Tmx) u.unmarshal(new FileInputStream(filepath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(TmxFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getSourceLanguage() {
        return doc.getHeader().getSrclang();
    }

    public int size() {
        return doc.getBody().getTu().size();
    }
    
    public String getFilePath() {
        return filepath;
    }

    public Map<String, Tuv> getTuvMap() {
        if (anonymousTuvs == null) {
            anonymousTuvs = TuvUtil.generateTuvMap(doc.getBody().getTu(), getSourceLanguage());
        }
        return anonymousTuvs;
    }
}
