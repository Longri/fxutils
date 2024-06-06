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
package de.longri.rolling_file;

import de.longri.filetransfer.FileTransferHandle;
import de.longri.filetransfer.FileTransferHandleFilter;
import de.longri.serializable.NotImplementedException;

import java.io.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;

public class ImplementedFileTransferHandle implements FileTransferHandle {

    public final FileTransferHandle HANDLE;

    ImplementedFileTransferHandle(FileTransferHandle handle) throws RuntimeException {
        if (handle == null) throw new RuntimeException("Handle can't be NULL");
        this.HANDLE = handle;
    }


    /**
     * @return the path of the file as specified on construction, e.g. Files.INSTANCE.internal("dir/file.png") -> dir/file.png.
     * backward slashes will be replaced by forward slashes.
     */
    @Override
    public String path() {
        return HANDLE.path();
    }

    /**
     * @return the name of the file, without any parent paths.
     */
    @Override
    public String name() {
        return HANDLE.name();
    }

    /**
     * Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot.
     */
    @Override
    public String extension() {
        return HANDLE.extension();
    }

    /**
     * @return the name of the file, without parent paths or the extension.
     */
    @Override
    public String nameWithoutExtension() {
        return HANDLE.nameWithoutExtension();
    }

    /**
     * @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file. backward slashes will be
     * returned as forward slashes.
     */
    @Override
    public String pathWithoutExtension() {
        return HANDLE.pathWithoutExtension();
    }

    /**
     * Returns a stream for reading this file as bytes.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public InputStream read() throws IOException, GeneralSecurityException {
        return HANDLE.read();
    }

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString() throws IOException, GeneralSecurityException {
        return HANDLE.readString();
    }

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @param charset If null the default charset is used.
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString(String charset) throws IOException, GeneralSecurityException {
        return HANDLE.readString(charset);
    }

    /**
     * Returns a stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    @Override
    public OutputStream write(boolean append) {
        return HANDLE.write(append);
    }

    /**
     * Writes the specified string to the file using the default charset. Parent directories will be created if necessary.
     *
     * @param string
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    @Override
    public void writeString(String string, boolean append) throws IOException {
        HANDLE.writeString(string, append);
    }

    /**
     * Writes the specified string to the file using the specified charset. Parent directories will be created if necessary.
     *
     * @param string
     * @param append  If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param charset May be null to use the default charset.
     * @throws RuntimeException if this file handle represents a directory, or if it could not be written.
     */
    @Override
    public void writeString(String string, boolean append, String charset) throws IOException {
        HANDLE.writeString(string, append, charset);
    }

    /**
     * Returns the paths to the children of this directory. Returns an empty list if this file handle represents a file and not a
     * directory
     */
    @Override
    public FileTransferHandle[] list() {
        return HANDLE.list();
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FileTransferHandleFilter} to filter files
     */
    @Override
    public FileTransferHandle[] list(FileTransferHandleFilter filter) {
        return HANDLE.list(filter);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FilenameFilter} to filter files
     */
    @Override
    public FileTransferHandle[] list(FilenameFilter filter) {
        return HANDLE.list(filter);
    }

    /**
     * Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory.
     *
     * @param suffix
     */
    @Override
    public FileTransferHandle[] list(String suffix) {
        return HANDLE.list(suffix);
    }

    /**
     * Returns true if this file is a directory.
     */
    @Override
    public boolean isDirectory() {
        return HANDLE.isDirectory();
    }

    /**
     * Returns a handle to the child with the specified name.
     *
     * @param name
     */
    @Override
    public FileTransferHandle child(String name) {
        return HANDLE.child(name);
    }

    /**
     * Returns a handle to the parent of this FileTransferHandle.
     */
    @Override
    public FileTransferHandle parent() {
        return HANDLE.parent();
    }

    @Override
    public void mkdirs() {
        HANDLE.mkdirs();
    }

    /**
     * Returns true if the file exists.
     */
    @Override
    public boolean exists() {
        return HANDLE.exists();
    }

    /**
     * Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     */
    @Override
    public boolean delete() {
        return HANDLE.delete();
    }

    /**
     * Deletes this file or directory and all children, recursively.
     */
    @Override
    public boolean deleteDirectory() {
        return HANDLE.deleteDirectory();
    }

    /**
     * Deletes all children of this directory, recursively.
     */
    @Override
    public void emptyDirectory() {
        HANDLE.emptyDirectory();
    }

    /**
     * Copies this file or directory to the specified file or directory. If this handle is a file, then 1) if the destination is a
     * file, it is overwritten, or 2) if the destination is a directory, this file is copied into it, or 3) if the destination
     * doesn't exist, {@link #mkdirs()} is called on the destination's parent and this file is copied into it with a new name. If
     * this handle is a directory, then 1) if the destination is a file, RuntimeException is thrown, or 2) if the destination is
     * a directory, this directory is copied into it recursively, overwriting existing files, or 3) if the destination doesn't
     * exist, {@link #mkdirs()} is called on the destination and this directory is copied into it recursively.
     *
     * @param dest
     */
    @Override
    public void copyTo(FileTransferHandle dest) {
        HANDLE.copyTo(dest);
    }

    /**
     * Moves this file to the specified file, overwriting the file if it already exists.
     *
     * @param dest
     */
    @Override
    public void moveTo(FileTransferHandle dest) {
        HANDLE.moveTo(dest);
    }

    /**
     * Returns the last modified time in milliseconds for this file. Zero is returned if the file doesn't exist.
     */
    @Override
    public long lastModified() {
        return HANDLE.lastModified();
    }

    /**
     * Upload a file to this FileTransferHandle.
     *
     * @param file
     * @throws RuntimeException if this FileTransferHandle represents not a directory.
     */
    @Override
    public void upload(File file) {
        HANDLE.upload(file);
    }

    /**
     * Download this FileTransferHandle to the given file.
     * Recursively if this FileTransferHandle represents a directory
     *
     * @param file
     */
    @Override
    public void download(File file) {
        HANDLE.download(file);
    }

    @Override
    public void closeInputStream(InputStream inputStream) throws IOException {
        HANDLE.closeInputStream(inputStream);
    }

    @Override
    public FileTransferHandle[] listDirs() {
        return HANDLE.listDirs();
    }

    @Override
    public FileTransferHandle getRoot() {
        return HANDLE.getRoot();
    }

    @Override
    public BasicFileAttributes getBasicFileAttributes() throws IOException, NotImplementedException {
        return HANDLE.getBasicFileAttributes();
    }

    @Override
    public boolean createNewFile() throws NotImplementedException {
        return HANDLE.createNewFile();
    }

    @Override
    public void zipTo(FileTransferHandle targetFileHandle) throws IOException, NotImplementedException {
        HANDLE.zipTo(targetFileHandle);
    }

    /**
     * Returns true if the binary content of given file transfer handle equals
     *
     * @param other
     * @return
     */
    @Override
    public boolean binaryEquals(FileTransferHandle other) throws NotImplementedException, IOException {
        return HANDLE.binaryEquals(other);
    }

    @Override
    public String toString() {
        return HANDLE.toString();
    }
}
