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

import de.longri.rolling_file.ImplementedFileTransferHandle;
import de.longri.rolling_file.RollingFileHandle;
import de.longri.serializable.NotImplementedException;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Local_FileTransferHandle implements FileTransferHandle {


    protected final File FILE;

    public Local_FileTransferHandle(String path) {
        FILE = new File(path);
    }

    public Local_FileTransferHandle(File file) {
        FILE = file;
    }


    /**
     * @return the path of the file as specified on construction, e.g. Files.INSTANCE.internal("dir/file.png") -> dir/file.png.
     * backward slashes will be replaced by forward slashes.
     */
    @Override
    public String path() {
        return FILE.getAbsolutePath();
    }

    /**
     * @return the name of the file, without any parent paths.
     */
    @Override
    public String name() {
        return FILE.getName();
    }

    /**
     * Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot.
     */
    @Override
    public String extension() {
        String name = FILE.getName();
        int pos = name.lastIndexOf(".");
        if (pos < 1) return null;
        return name.substring(pos + 1);
    }

    /**
     * @return the name of the file, without parent paths or the extension.
     */
    @Override
    public String nameWithoutExtension() {
        String name = FILE.getName();
        return name.replace("." + extension(), "");
    }

    /**
     * @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file. backward slashes will be
     * returned as forward slashes.
     */
    @Override
    public String pathWithoutExtension() {
        String path = FILE.getPath();
        return path.replace("." + extension(), "");
    }

    /**
     * Returns a stream for reading this file as bytes.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public InputStream read() throws IOException {
        return new FileInputStream(FILE);
    }

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString() throws IOException {
        return Files.readString(FILE.toPath());
    }

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @param charset If null the default charset is used.
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString(String charset) throws IOException {
        return Files.readString(FILE.toPath(), Charset.forName(charset));
    }

    /**
     * Returns a stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    @Override
    public OutputStream write(boolean append) {
        try {
            return new FileOutputStream(FILE, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            Files.writeString(FILE.toPath(), string, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            if (!FILE.exists()) FILE.createNewFile();
            Files.writeString(FILE.toPath(), string, Charset.forName(charset), append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (list == null) return new FileTransferHandle[0];
        ArrayList<Local_FileTransferHandle> retList = new ArrayList<>();

        for (File f : list) {
            Local_FileTransferHandle lf = new Local_FileTransferHandle(f);
            //ask filter if exist
            if (filter == null || filter.accept(lf)) retList.add(lf);
        }

        Local_FileTransferHandle ret[] = new Local_FileTransferHandle[retList.size()];

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

                if (!(pathname instanceof Local_FileTransferHandle)) return false;
                return filter.accept(((Local_FileTransferHandle) pathname).FILE, ((Local_FileTransferHandle) pathname).FILE.getName());
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
        return new FileTransferHandle[0];
    }

    /**
     * Returns true if this file is a directory.
     */
    @Override
    public boolean isDirectory() {
        return this.FILE.isDirectory();
    }

    /**
     * Returns a handle to the child with the specified name.
     *
     * @param name
     */
    @Override
    public FileTransferHandle child(String name) {
        return new Local_FileTransferHandle(new File(FILE, name));
    }

    /**
     * Returns a handle to the parent of this FileTransferHandle.
     */
    @Override
    public FileTransferHandle parent() {
        return new Local_FileTransferHandle(FILE.getParentFile());
    }

    @Override
    public void mkdirs() {
        if (this.extension() != null) {
            FILE.getParentFile().mkdirs();
        } else {
            FILE.mkdirs();
        }
    }

    /**
     * Returns true if the file exists.
     */
    @Override
    public boolean exists() {
        return FILE.exists();
    }

    /**
     * Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     */
    @Override
    public boolean delete() {
        return FILE.delete();
    }

    /**
     * Deletes this file or directory and all children, recursively.
     */
    @Override
    public boolean deleteDirectory() {
        return deleteDirectory(FILE);
    }


    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
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

        File copied = null;

        if (dest instanceof Local_FileTransferHandle) {
            copied = ((Local_FileTransferHandle) dest).FILE;
        } else if (dest instanceof ImplementedFileTransferHandle) {
            FileTransferHandle transferHandle = ((ImplementedFileTransferHandle) dest).HANDLE;
            if (transferHandle instanceof Local_FileTransferHandle)
                copied = ((Local_FileTransferHandle) dest).FILE;
        }
        try {
            FileUtils.copyFile(FILE, copied);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Moves this file to the specified file, overwriting the file if it already exists.
     *
     * @param dest
     */
    @Override
    public void moveTo(FileTransferHandle dest) {
        File rnf = new File(FILE.getAbsolutePath());
        File nf = new File(dest.path());
        rnf.renameTo(nf);
    }

    /**
     * Returns the last modified time in milliseconds for this file. Zero is returned if the file doesn't exist.
     */
    @Override
    public long lastModified() {
        try {
            BasicFileAttributes attr = getBasicFileAttributes();
            return attr.lastModifiedTime().toMillis();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    }

    @Override
    public FileTransferHandle[] listDirs() {
        return list(new FileTransferHandleFilter() {
            @Override
            public boolean accept(FileTransferHandle fth) {
                return fth.exists() && fth.isDirectory() && !fth.path().startsWith("/.");
            }
        });
    }

    @Override
    public FileTransferHandle getRoot() {
        return new Local_FileTransferHandle("/");
    }

    @Override
    public BasicFileAttributes getBasicFileAttributes() throws IOException {
        return Files.readAttributes(FILE.toPath(), BasicFileAttributes.class);
    }

    @Override
    public boolean createNewFile() {
        mkdirs();
        try {
            return FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void zipTo(FileTransferHandle targetFileHandle) throws IOException {
        FileOutputStream fos = new FileOutputStream(((Local_FileTransferHandle) targetFileHandle).FILE);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        zipFile(this.FILE, this.FILE.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (false && fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    @Override
    public String toString() {
        return path();
    }

    public File getFile() {
        return FILE;
    }

    int GIGA_BYTE = 1024 * 1024;

    /**
     * Returns true if the binary content of given file transfer handle equals
     *
     * @param other
     * @return
     */
    @Override
    public boolean binaryEquals(FileTransferHandle other) throws NotImplementedException, IOException {

        if (other instanceof Local_FileTransferHandle) {
            Local_FileTransferHandle o = (Local_FileTransferHandle) other;
            if (o.FILE.length() != this.FILE.length()) return false;

            if (o.FILE.length() > GIGA_BYTE) {
                //use random access file

                RandomAccessFile ra1 = new RandomAccessFile(o.FILE, "r");
                RandomAccessFile ra2 = new RandomAccessFile(this.FILE, "r");

                byte[] b1 = new byte[GIGA_BYTE];
                byte[] b2 = new byte[GIGA_BYTE];

                long length = ra1.length();
                long seek = 0;
                ra1.seek(seek);
                ra2.seek(seek);
                while (length > 0) {
                    ra1.read(b1, 0, GIGA_BYTE);
                    ra2.read(b2, 0, GIGA_BYTE);
                    if (!Arrays.equals(b1, b2)) return false;
                    seek += GIGA_BYTE;
                    length = ra1.length() - seek;
                    ra1.seek(seek);
                    ra2.seek(seek);
                }
                return true;
            } else {
                //read in to array and return array equals result
                byte[] b1 = FileUtils.readFileToByteArray(o.FILE);
                byte[] b2 = FileUtils.readFileToByteArray(this.FILE);

                return Arrays.equals(b1, b2);
            }

        } else if (other instanceof ImplementedFileTransferHandle) {
            ImplementedFileTransferHandle implementedFileTransferHandle = (ImplementedFileTransferHandle) other;

            if (implementedFileTransferHandle.HANDLE instanceof Local_FileTransferHandle) {
                Local_FileTransferHandle o = (Local_FileTransferHandle) implementedFileTransferHandle.HANDLE;
                if (o.FILE.length() != this.FILE.length()) return false;

                if (o.FILE.length() > GIGA_BYTE) {
                    //use random access file

                    RandomAccessFile ra1 = new RandomAccessFile(o.FILE, "r");
                    RandomAccessFile ra2 = new RandomAccessFile(this.FILE, "r");

                    byte[] b1 = new byte[GIGA_BYTE];
                    byte[] b2 = new byte[GIGA_BYTE];

                    long length = ra1.length();
                    long seek = 0;
                    ra1.seek(seek);
                    ra2.seek(seek);
                    while (length > 0) {
                        ra1.read(b1, 0, GIGA_BYTE);
                        ra2.read(b2, 0, GIGA_BYTE);
                        if (!Arrays.equals(b1, b2)) return false;
                        seek += GIGA_BYTE;
                        length = ra1.length() - seek;
                        ra1.seek(seek);
                        ra2.seek(seek);
                    }
                    return true;
                } else {
                    //read in to array and return array equals result
                    byte[] b1 = FileUtils.readFileToByteArray(o.FILE);
                    byte[] b2 = FileUtils.readFileToByteArray(this.FILE);

                    return Arrays.equals(b1, b2);
                }
            }
            throw new NotImplementedException("Bin equals not implemented");

        } else {
            throw new NotImplementedException("Bin equals not implemented");
        }
    }
}
