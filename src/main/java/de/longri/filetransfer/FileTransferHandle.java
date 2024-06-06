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

import de.longri.serializable.NotImplementedException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;

public interface FileTransferHandle {

    /**
     * @return the path of the file as specified on construction, e.g. Files.INSTANCE.internal("dir/file.png") -> dir/file.png.
     * backward slashes will be replaced by forward slashes.
     */
    String path();

    /**
     * @return the name of the file, without any parent paths.
     */
    String name();

    /**
     * Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot.
     */
    String extension();

    /**
     * @return the name of the file, without parent paths or the extension.
     */
    String nameWithoutExtension();

    /**
     * @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file. backward slashes will be
     * returned as forward slashes.
     */
    String pathWithoutExtension();

    /**
     * Returns a stream for reading this file as bytes.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    InputStream read() throws IOException, GeneralSecurityException;

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    String readString() throws IOException, GeneralSecurityException;

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @param charset If null the default charset is used.
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    String readString(String charset) throws IOException, GeneralSecurityException;

    /**
     * Returns a stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    OutputStream write(boolean append);

    /**
     * Writes the specified string to the file using the default charset. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    void writeString(String string, boolean append) throws IOException;

    /**
     * Writes the specified string to the file using the specified charset. Parent directories will be created if necessary.
     *
     * @param append  If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param charset May be null to use the default charset.
     * @throws RuntimeException if this file handle represents a directory, or if it could not be written.
     */
    void writeString(String string, boolean append, String charset) throws IOException;

    /**
     * Returns the paths to the children of this directory. Returns an empty list if this file handle represents a file and not a
     * directory
     */
    FileTransferHandle[] list();

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FileTransferHandleFilter} to filter files
     */
    FileTransferHandle[] list(FileTransferHandleFilter filter);

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FilenameFilter} to filter files
     */
    FileTransferHandle[] list(FilenameFilter filter);

    /**
     * Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory.
     */
    FileTransferHandle[] list(String suffix);

    /**
     * Returns true if this file is a directory.
     */
    boolean isDirectory();

    /**
     * Returns a handle to the child with the specified name.
     */
    FileTransferHandle child(String name);

    /**
     * Returns a handle to the parent of this FileTransferHandle.
     */
    FileTransferHandle parent();


    void mkdirs();

    /**
     * Returns true if the file exists.
     */
    boolean exists();

    /**
     * Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     */
    boolean delete();

    /**
     * Deletes this file or directory and all children, recursively.
     */
    boolean deleteDirectory();

    /**
     * Deletes all children of this directory, recursively.
     */
    void emptyDirectory();

    /**
     * Copies this file or directory to the specified file or directory. If this handle is a file, then 1) if the destination is a
     * file, it is overwritten, or 2) if the destination is a directory, this file is copied into it, or 3) if the destination
     * doesn't exist, {@link #mkdirs()} is called on the destination's parent and this file is copied into it with a new name. If
     * this handle is a directory, then 1) if the destination is a file, RuntimeException is thrown, or 2) if the destination is
     * a directory, this directory is copied into it recursively, overwriting existing files, or 3) if the destination doesn't
     * exist, {@link #mkdirs()} is called on the destination and this directory is copied into it recursively.
     */
    void copyTo(FileTransferHandle dest);

    /**
     * Moves this file to the specified file, overwriting the file if it already exists.
     */
    void moveTo(FileTransferHandle dest);

    /**
     * Returns the last modified time in milliseconds for this file. Zero is returned if the file doesn't exist.
     */
    long lastModified();

    /**
     * Upload a file to this FileTransferHandle.
     *
     * @param file
     * @throws RuntimeException if this FileTransferHandle represents not a directory.
     */
    void upload(File file);

    /**
     * Download this FileTransferHandle to the given file.
     * Recursively if this FileTransferHandle represents a directory
     *
     * @param file
     */
    void download(File file);

    void closeInputStream(InputStream inputStream) throws IOException;

    FileTransferHandle[] listDirs();

    FileTransferHandle getRoot();

    BasicFileAttributes getBasicFileAttributes() throws IOException, NotImplementedException;

    boolean createNewFile() throws NotImplementedException;

    void zipTo(FileTransferHandle targetFileHandle) throws IOException, NotImplementedException;

    /**
     * Returns true if the binary content of given file transfer handle equals
     *
     * @param other
     * @return
     */
    boolean binaryEquals(FileTransferHandle other) throws NotImplementedException, IOException;


}
