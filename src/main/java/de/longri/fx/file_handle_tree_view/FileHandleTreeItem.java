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
package de.longri.fx.file_handle_tree_view;

import de.longri.filetransfer.FileTransferHandle;
import de.longri.filetransfer.FileTransferHandleFilter;
import de.longri.fx.file_transfer_chooser.DirectoryIcons;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHandleTreeItem extends TreeItem<String> {
    private static final Logger log = LoggerFactory.getLogger(FileHandleTreeItem.class);

    private final FileTransferHandleFilter DIR_FILTER = FileTransferHandle::isDirectory;

    private static String getName(FileTransferHandle fileTransferHandle) {
        String na = fileTransferHandle.nameWithoutExtension();
        if (na.isEmpty() || na.isBlank()) return "/";
        return na;
    }

    private FileTransferHandle fth;
    private boolean isDir;
    private boolean dirIsRead = false;

    public FileHandleTreeItem(FileTransferHandle fileTransferHandle) {
        this(fileTransferHandle, true);
    }

    private FileHandleTreeItem(FileTransferHandle fileTransferHandle, boolean expand) {
        super(getName(fileTransferHandle));
        fth = fileTransferHandle;
        isDir = fth.isDirectory();

        if (isDir) {
            if (fth.list(DIR_FILTER).length > 0) {
                setGraphic(DirectoryIcons.CLOSED_PLUS.get(16));
            } else {
                setGraphic(DirectoryIcons.CLOSED.get(16));
            }
        }

        expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !dirIsRead) {
                log.debug("Expand fileTreeItem: {}", fth);
                setGraphic(DirectoryIcons.OPEN.get(16));
                //load tree items from file transfer handle (only folder's)
                FileHandleTreeItem.this.getChildren().clear(); // remove first! don't add double
                FileTransferHandle[] ls = fth.list(DIR_FILTER);
                //creat for each fth a treeItem and add as child
                for (FileTransferHandle d : ls) {
                    FileHandleTreeItem treeItem = new FileHandleTreeItem(d, false);
                    String lstr = treeItem.fth.isDirectory() ? "is Directory" : "is File";
                    log.debug("add fileTreeItem: {} [{}] ", treeItem, lstr);
                    FileHandleTreeItem.this.getChildren().add(treeItem);

                    if (treeItem.isDir) {
                        log.debug("   >Tree Item is directory, so read child's");
                        FileTransferHandle[] dirs = treeItem.fth.list(DIR_FILTER);
                        for (FileTransferHandle di : dirs) {
                            log.debug("      >add fileTreeItem: {}", di);
                            treeItem.getChildren().add(new FileHandleTreeItem(di, false));
                        }
                    }
                }
                dirIsRead = true;
            } else {
                setGraphic(DirectoryIcons.CLOSED_PLUS.get(16));
            }
        });

        if (expand) Platform.runLater(() -> expandedProperty().set(true));
    }

    @Override
    public String toString() {
        return "TreeItem: " + fth;
    }

    public FileTransferHandle getFileTransferHandle() {
        return fth;
    }
}
