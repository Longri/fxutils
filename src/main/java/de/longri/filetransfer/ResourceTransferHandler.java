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
package de.longri.filetransfer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ResourceTransferHandler extends Local_FileTransferHandle {


    public ResourceTransferHandler(String path) throws URISyntaxException {
        this(new File(ClassLoader.getSystemResource(path).toURI()));
    }

    public ResourceTransferHandler(File file) {
        super(file);
    }

    /**
     * @throws RuntimeException this will throw a runtime exception! You can't write into resource!
     */
    @Override
    public OutputStream write(boolean append) {
        throw new RuntimeException("Can't write to resource");
    }

    /**
     * @throws RuntimeException this will throw a runtime exception! You can't write into resource!
     */
    @Override
    public void writeString(String string, boolean append) {
        throw new RuntimeException("Can't write to resource");
    }

    /**
     * @throws RuntimeException this will throw a runtime exception! You can't write into resource!
     */
    @Override
    public void writeString(String string, boolean append, String charset) {
        throw new RuntimeException("Can't write to resource");
    }

    /**
     * @throws RuntimeException this will throw a runtime exception! You can't write into resource!
     */
    @Override
    public void mkdirs() {
        throw new RuntimeException("Can't make dirs on resource");
    }



    @Override
    public FileTransferHandle child(String name) {
        return  new ResourceTransferHandler(new File(FILE, name));
    }

    /**
     * Returns the paths to the children of this directory. Returns an empty list if this file handle represents a file and not a
     * directory
     */
    @Override
    public FileTransferHandle[] list() {
        return list((FileTransferHandleFilter) null);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FileTransferHandleFilter} to filter files
     */
    @Override
    public FileTransferHandle[] list(FileTransferHandleFilter filter) {

        File list[] = FILE.listFiles();
        ArrayList<Local_FileTransferHandle> retList = new ArrayList<>();

        for (File f : list) {
            ResourceTransferHandler lf = new ResourceTransferHandler(f);
            //ask filter if exist
            if (filter == null || filter.accept(lf)) retList.add(lf);
        }

        ResourceTransferHandler ret[] = new ResourceTransferHandler[retList.size()];

        return retList.toArray(ret);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FilenameFilter} to filter files
     */
    @Override
    public FileTransferHandle[] list(FilenameFilter filter) {
        return list(new FileTransferHandleFilter() {

            /**
             * Tests whether or not the specified abstract pathname should be
             * included in a pathname list.
             *
             * @param pathname The abstract pathname to be tested
             * @return {@code true} if and only if {@code pathname}
             * should be included
             */
            @Override
            public boolean accept(FileTransferHandle pathname) {

                if (!(pathname instanceof ResourceTransferHandler)) return false;
                return filter.accept(((ResourceTransferHandler) pathname).FILE, ((ResourceTransferHandler) pathname).FILE.getName());
            }
        });
    }

    /**
     * Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory.
     *
     * @param suffix
     */
    @Override
    public FileTransferHandle[] list(String suffix) {
        return new ResourceTransferHandler[0];
    }

}
