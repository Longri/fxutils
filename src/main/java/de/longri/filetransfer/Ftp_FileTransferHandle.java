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
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static de.longri.filetransfer.Ftp_Connection.showServerReply;
import static org.apache.commons.net.ftp.FTPFile.DIRECTORY_TYPE;
import static org.apache.commons.net.ftp.FTPFile.FILE_TYPE;


public class Ftp_FileTransferHandle implements FileTransferHandle {
    private final Ftp_Connection connection;
    private final FTPFile ftpFile;
    private final String path;

    public Ftp_FileTransferHandle(Credentials credentials) {
        this.connection = new Ftp_Connection(credentials);
        this.ftpFile = new FTPFile();
        this.path = "/"; //ftp root path
        ftpFile.setType(DIRECTORY_TYPE);
    }

    private Ftp_FileTransferHandle(Ftp_Connection ftp_connection, FTPFile ftpFile, String path) {
        this.connection = ftp_connection;
        this.ftpFile = ftpFile;
        this.path = path;
    }

    public Ftp_FileTransferHandle(Ftp_Connection ftp_connection, String rootPath) {
        this.connection = ftp_connection;
        this.ftpFile = new FTPFile();
        this.path = rootPath; //ftp root path
        ftpFile.setType(DIRECTORY_TYPE);
    }

    public Credentials getCredentials() {
        return connection.getCredentials();
    }

    /**
     * @return the path of the file as specified on construction, e.g. Files.INSTANCE.internal("dir/file.png") -> dir/file.png.
     * backward slashes will be replaced by forward slashes.
     */
    @Override
    public String path() {
        return null;
    }

    /**
     * @return the name of the file, without any parent paths.
     */
    @Override
    public String name() {
        synchronized (connection) {
            return this.ftpFile.getName();
        }
    }

    /**
     * Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot.
     */
    public String extension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    /**
     * @return the name of the file, without parent paths or the extension.
     */
    public String nameWithoutExtension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }

    /**
     * @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file. backward slashes will be
     * returned as forward slashes.
     */
    @Override
    public String pathWithoutExtension() {
        return null;
    }

    /**
     * Returns a stream for reading this file as bytes.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public InputStream read() throws IOException {
        synchronized (connection) {
            this.connection.connect();
            //remove forwarded '/'
            String p = this.path;
            InputStream stream = this.connection.ftpClient.retrieveFileStream(p);
            showServerReply(this.connection.ftpClient);
            this.connection.disconnect();
            return stream;
        }
    }

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString() throws IOException {
        return readString("UTF-8");
    }

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @param charset If null the default charset is used.
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString(String charset) throws IOException {
        synchronized (connection) {
            this.connection.connect();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read = 0;
            InputStream inputStream = read();
            while ((read = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, read);
                if (read <= 0) break;
            }
            closeInputStream(inputStream);
            this.connection.disconnect();
            return result.toString(charset);
        }
    }

    /**
     * Returns a stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    @Override
    public OutputStream write(boolean append) {
        return null;
    }

    /**
     * Writes the specified string to the file using the default charset. Parent directories will be created if necessary.
     *
     * @param string
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    @Override
    public void writeString(String string, boolean append) {

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
    public void writeString(String string, boolean append, String charset) {

    }

    /**
     * Returns the paths to the children of this directory. Returns an empty list if this file handle represents a file and not a
     * directory
     */
    @Override
    public FileTransferHandle[] list() {

        if (!this.ftpFile.isDirectory()) {
            throw new RuntimeException("FtpFileHandle is not a directory");
        }
        this.connection.connect();
        List<FileTransferHandle> list = new ArrayList<>();
        try {
            FTPFile[] files = this.connection.ftpClient.listFiles(this.path);
            for (FTPFile file : files) {
                String filePath = this.path + "/" + file.getName();
                FileTransferHandle handle = new Ftp_FileTransferHandle(this.connection, file, filePath);
                list.add(handle);
            }
        } catch (IOException e) {
            e.printStackTrace();//TODO replace with Logger
        }
        this.connection.disconnect();
        return list.toArray(new FileTransferHandle[0]);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FileFilter} to filter files
     */
    @Override
    public FileTransferHandle[] list(FileTransferHandleFilter filter) {

        if (!this.ftpFile.isDirectory()) {
            throw new RuntimeException("FtpFileHandle is not a directory");
        }
        this.connection.connect();
        List<FileTransferHandle> list = new ArrayList<>();
        try {
            FTPFile[] files = this.connection.ftpClient.listFiles(this.path);
            for (FTPFile file : files) {
                String filePath = this.path + "/" + file.getName();

                FileTransferHandle handle = new Ftp_FileTransferHandle(this.connection, file, filePath);

                //ask filter
                if (filter.accept(handle))
                    list.add(handle);
            }
        } catch (IOException e) {
            e.printStackTrace();//TODO replace with Logger
        }
        this.connection.disconnect();
        return list.toArray(new FileTransferHandle[0]);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory.
     *
     * @param filter the {@link FilenameFilter} to filter files
     */
    @Override
    public FileTransferHandle[] list(FilenameFilter filter) {
        return new FileTransferHandle[0];
    }

    /**
     * Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory.
     *
     * @param suffix
     */
    @Override
    public FileTransferHandle[] list(String suffix) {
        return new FileTransferHandle[0];
    }

    /**
     * Returns true if this file is a directory.
     */
    @Override
    public boolean isDirectory() {
        return this.ftpFile.isDirectory();
    }

    /**
     * Returns a handle to the child with the specified name.
     *
     * @param name
     */
    @Override
    public FileTransferHandle child(String name) {
        this.connection.connect();
        try {

            String parentPath = getParentPath(this.path + "/" + name);

            int pos = name.lastIndexOf("/");
            if (pos > 0) {
                name = name.substring(pos + 1);
            }

            FTPFile[] files = this.connection.ftpClient.listFiles(parentPath);
            for (FTPFile file : files) {
                if (name.equals(file.getName())) {
                    return new Ftp_FileTransferHandle(this.connection, file, parentPath + "/" + name);
                }
            }
            //file not exist, create a new one
            FTPFile file = new FTPFile();
            file.setName(name);
            file.setType(FILE_TYPE);
            return new Ftp_FileTransferHandle(this.connection, file, parentPath + "/" + name);
        } catch (IOException e) {
            e.printStackTrace();//TODO replace with Logger
        } finally {
            this.connection.disconnect();
        }
        return null;
    }

    /**
     * Returns a handle to the parent of this FileTransferHandle.
     */
    @Override
    public FileTransferHandle parent() {
        return null;
    }

    @Override
    public void mkdirs() {

    }

    /**
     * Returns true if the file exists.
     */
    @Override
    public boolean exists() {
        this.connection.connect();
        try {
            FTPFile[] files = this.connection.ftpClient.listFiles(getParentPath(this.path));
            for (FTPFile file : files) {
                if (this.ftpFile.getName().equals(file.getName())) {
                    this.connection.disconnect();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connection.disconnect();
        return false;
    }

    private String getParentPath(String parent) {
        int index = parent.lastIndexOf("/");
        return parent.substring(0, index);
    }

    /**
     * Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     */
    @Override
    public boolean delete() {
        return false;
    }

    /**
     * Deletes this file or directory and all children, recursively.
     */
    @Override
    public boolean deleteDirectory() {
        return false;
    }

    /**
     * Deletes all children of this directory, recursively.
     */
    @Override
    public void emptyDirectory() {

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

    }

    /**
     * Moves this file to the specified file, overwriting the file if it already exists.
     *
     * @param dest
     */
    @Override
    public void moveTo(FileTransferHandle dest) {

    }

    /**
     * Returns the last modified time in milliseconds for this file. Zero is returned if the file doesn't exist.
     */
    @Override
    public long lastModified() {
        return this.ftpFile.getTimestamp().getTimeInMillis();
    }

    /**
     * Upload a file to this FileTransferHandle.
     *
     * @param file
     * @throws RuntimeException if this FileTransferHandle represents not a directory.
     */
    @Override
    public void upload(File file) {

    }

    /**
     * Download this FileTransferHandle to the given file.
     * Recursively if this FileTransferHandle represents a directory
     *
     * @param file
     */
    @Override
    public void download(File file) {

    }

    @Override
    public void closeInputStream(InputStream inputStream) throws IOException {
        synchronized (connection) {
            inputStream.close();
            this.connection.ftpClient.completePendingCommand();
            showServerReply(this.connection.ftpClient);
        }
    }

    @Override
    public FileTransferHandle[] listDirs() {
        return list(new FileTransferHandleFilter() {
            @Override
            public boolean accept(FileTransferHandle fth) {
                return fth.exists() && fth.isDirectory();
            }
        });
    }

    @Override
    public FileTransferHandle getRoot() {
        return new Ftp_FileTransferHandle(this.connection, "/");
    }

    @Override
    public BasicFileAttributes getBasicFileAttributes() throws IOException, NotImplementedException {
        throw new NotImplementedException("getBasicFileAttributes() not implemented for Ftp-FileTransferHandle");
    }

    @Override
    public boolean createNewFile() throws NotImplementedException {
        throw new NotImplementedException("createNewFile() not implemented for Ftp-FileTransferHandle");
    }

    @Override
    public void zipTo(FileTransferHandle targetFileHandle) throws IOException, NotImplementedException {
        throw new NotImplementedException("zipTo() not implemented for Ftp-FileTransferHandle");
    }

    /**
     * Returns true if the binary content of given file transfer handle equals
     *
     * @param other
     * @return
     */
    @Override
    public boolean binaryEquals(FileTransferHandle other) throws NotImplementedException {
        throw new NotImplementedException("binaryEquals() not implemented for Ftp-FileTransferHandle");
    }
}
