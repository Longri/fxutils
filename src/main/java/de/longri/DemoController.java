/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of fxutils.
 *
 * fxutils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * fxutils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with fxutils. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri;

import de.longri.fx.FX_Application;
import de.longri.fx.FxStyles;
import de.longri.fx.I_ShowHeide;
import de.longri.filetransfer.FileTransferHandle;
import de.longri.filetransfer.FileTransferHelper;
import de.longri.fx.DemoDirectoryFileChooser;
import de.longri.fx.file_transfer_chooser.FileTransferChooser;
import de.longri.serializable.BitStore;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.StoreBase;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DemoController implements I_ShowHeide {

    private static Logger log = LoggerFactory.getLogger(DemoController.class);
    FileTransferChooser ftc;

    @Override
    public void show(Object data, Stage stage) {

        //Show modal FileTransferChooser

        if (DemoDirectoryFileChooser.PREF.contains(KEY_FTH)) {
            String base64Str = DemoDirectoryFileChooser.PREF.getString(KEY_FTH);

            if (base64Str.equals("NULL")) {
                ftc = new FileTransferChooser();
            } else {
                StoreBase store = new BitStore(base64Str);
                try {
                    ftc = new FileTransferChooser(FileTransferHelper.getFileTransferHandle(store));
                } catch (NotImplementedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            ftc = new FileTransferChooser();
        }

        log.debug("FileTransferChooser created");
        FX_Application.showModalDialog(ftc);
        log.debug("Modal back from FileTransferChooser");


    }
    
    final private String KEY_FTH = "file";

    @Override
    public void beforeHide() {

        if (ftc == null) return;

        FileTransferHandle returnValue = ftc.getReturnValue();
        log.debug("returnValue: {}", returnValue);
        StoreBase store = new BitStore();

        if (returnValue != null) {
            try {
                FileTransferHelper.serialize(store, returnValue);
            } catch (NotImplementedException e) {
                throw new RuntimeException(e);
            }
            DemoDirectoryFileChooser.PREF.put(KEY_FTH, store.getBase64String());
        } else {
            DemoDirectoryFileChooser.PREF.put(KEY_FTH, "NULL");
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public double getMinWidth() {
        return 0;
    }

    @Override
    public double getMinHeight() {
        return 0;
    }

    @Override
    public void fxStyleChanged(FxStyles style) {

    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
