/*
 * Copyright (C) 2015 Aaron Madlon-Kay <aaron@madlon-kay.com>.
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.madlonkay.supertmxmerge.data.ITmx;
import org.madlonkay.supertmxmerge.data.JAXB.JAXBTmx;
import org.madlonkay.supertmxmerge.gui.ProgressWindow;
import org.madlonkay.supertmxmerge.util.GuiUtil;
import org.madlonkay.supertmxmerge.util.LocString;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public abstract class FileLoaderWorker extends SwingWorker<List<ITmx>, Object[]> {

    private final File[] files;
    protected final ProgressWindow progress;

    public FileLoaderWorker(File... files) {
        progress = new ProgressWindow();
        progress.setMaximum(files.length);
        this.files = files;
    }

    @Override
    protected List<ITmx> doInBackground() throws Exception {
        List<ITmx> result = new ArrayList<ITmx>(files.length);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            publish(new Object[] { i, LocString.getFormat("STM_FILE_PROGRESS", file.getName(), i + 1, files.length) });
            result.add(new JAXBTmx(file));
        }
        publish(new Object[] { files.length });
        return result;
    }

    @Override
    protected void process(List<Object[]> chunks) {
        Object[] last = chunks.get(chunks.size() - 1);
        progress.setValue((Integer) last[0]);
        if (last.length > 1) {
            progress.setMessage((String) last[1]);
        }
    }
    
    @Override
    protected void done() {
        List<ITmx> result;
        try {
            result = get();
        } catch (Exception ex) {
            throw new RuntimeException(LocString.get("STM_LOAD_ERROR"), ex);
        } finally {
            GuiUtil.closeWindow(progress);
        }
        
        processLoadedTmxs(result);
    }
    
    protected abstract void processLoadedTmxs(List<ITmx> tmxs);
}
