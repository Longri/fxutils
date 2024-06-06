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
package de.longri.fx.file_transfer_chooser;

import de.longri.filetransfer.FileTransferHandle;

public class IndexedPath {

    final FileTransferHandle fileTransferHandle;

    public IndexedPath(FileTransferHandle fth) {
        this.fileTransferHandle = fth;
    }

    public static IndexedPath valueOf(FileTransferHandle file) {
        return new IndexedPath(file);
    }

    public FileTransferHandle asPath(FileTransferHandle rootDir) {
        return rootDir;
    }

    public String toString() {
        return fileTransferHandle.name();
    }
}
