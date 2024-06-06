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

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import de.longri.serializable.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;
import java.util.*;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_CREATE;
import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OPEN_IF;

/**
 * Use SMB from https://github.com/hierynomus/smbj
 */
public class SMB_FileTransferHandle implements FileTransferHandle {

    private static final Logger log = LoggerFactory.getLogger(SMB_FileTransferHandle.class);

     final Credentials CRED;
     final String PATH;
     final String NAME;
    private final Session session;

    public SMB_FileTransferHandle(Credentials cred, String path) throws IOException {
        this(cred, path, null);
    }

    private SMB_FileTransferHandle(SMB_FileTransferHandle other, FileIdBothDirectoryInformation f) throws IOException {
        CRED = other.CRED;
        PATH = other.PATH;
        NAME = f.getFileName();
        SMBClient client = new SMBClient();
        com.hierynomus.smbj.connection.Connection connection = client.connect(CRED.getAddress());

        AuthenticationContext ac = new AuthenticationContext(CRED.getUser(), CRED.getPassword().toCharArray(), CRED.getDomain());
        session = connection.authenticate(ac);
    }

    public SMB_FileTransferHandle(Credentials cred, String path, String name) throws IOException {
        CRED = cred;
        PATH = path;
        NAME = name;
        SMBClient client = new SMBClient();
        com.hierynomus.smbj.connection.Connection connection = client.connect(CRED.getAddress());

        AuthenticationContext ac = new AuthenticationContext(CRED.getUser(), CRED.getPassword().toCharArray(), CRED.getDomain());
        session = connection.authenticate(ac);
    }

    private DiskShare getShare() {
        return (DiskShare) session.connectShare(CRED.getShareName());
    }

    private com.hierynomus.smbj.share.File getFile() {
        return getShare().openFile(PATH + "/" + NAME,
                EnumSet.of(AccessMask.FILE_READ_DATA, AccessMask.FILE_WRITE_DATA, AccessMask.FILE_APPEND_DATA),
                null,
                SMB2ShareAccess.ALL,
                FILE_OPEN_IF,
                null);
    }

    public Credentials getCredentials() {
        return CRED;
    }

    /**
     * @return the path of the file as specified on construction, e.g. Files.INSTANCE.internal("dir/file.png") -> dir/file.png.
     * backward slashes will be replaced by forward slashes.
     */
    @Override
    public String path() {
        if (NAME == null) return PATH;
        if (PATH.endsWith("/")) return PATH + NAME;
        return PATH + "/" + NAME;
    }

    /**
     * @return the name of the file, without any parent paths.
     */
    @Override
    public String name() {
        return NAME;
    }

    /**
     * Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot.
     */
    @Override
    public String extension() {
        int pos = NAME.lastIndexOf(".");
        if (pos < 0) return null;
        return NAME.substring(pos);
    }

    /**
     * @return the name of the file, without parent paths or the extension.
     */
    @Override
    public String nameWithoutExtension() {
        return NAME.replace(extension(), "");
    }

    /**
     * @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file. backward slashes will be
     * returned as forward slashes.
     */
    @Override
    public String pathWithoutExtension() {
        if (NAME == null) return PATH;
        return PATH + "/" + nameWithoutExtension();
    }

    /**
     * Returns a stream for reading this file as bytes.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public InputStream read() throws IOException, GeneralSecurityException {
        return getFile().getInputStream();
    }

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString() throws IOException, GeneralSecurityException {
        return readString("UTF-8");
    }

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @param charset If null the default charset is used.
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString(String charset) throws IOException, GeneralSecurityException {
        return new String(read().readAllBytes(), Charset.forName(charset));
    }

    /**
     * Returns a stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory or if it could not be written.
     */
    @Override
    public OutputStream write(boolean append) {
        return getFile().getOutputStream(append);
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
        writeString(string, append, "utf-8");
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
        if (!append) {
            //delete file bevor as workaround
            delete();
        }

        if (!exists()) {
            createFile(path());
        }

        try (Writer w = new OutputStreamWriter(getFile().getOutputStream(append), "UTF-8")) {
            w.write(string);
            w.flush();
            w.close();
        }
    }

    public OutputStream createFile(String f) throws FileSystemException, IOException {
        Set<AccessMask> accessMask = new HashSet<AccessMask>(EnumSet.of(AccessMask.FILE_ADD_FILE));
        Set<SMB2CreateOptions> createOptions = new HashSet<SMB2CreateOptions>(
                EnumSet.of(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE, SMB2CreateOptions.FILE_WRITE_THROUGH));

        final com.hierynomus.smbj.share.File file = getShare().openFile(f, accessMask, null, SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OVERWRITE_IF, createOptions);
        OutputStream out = file.getOutputStream();
        FilterOutputStream fos = new FilterOutputStream(out) {

            boolean isOpen = true;

            @Override
            public void close() throws IOException {
                if (isOpen) {
                    super.close();
                    isOpen = false;
                }
                file.close();
            }
        };
        return fos;
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
        DiskShare share = getShare();
        ArrayList<FileTransferHandle> retList = new ArrayList<>();
        List<FileIdBothDirectoryInformation> list = share.list(path());
        for (FileIdBothDirectoryInformation f : list) {
            if (f.getFileName().equals(".") || f.getFileName().equals("..")) continue;
            SMB_FileTransferHandle lf = null;
            try {
                lf = new SMB_FileTransferHandle(this, f);
            } catch (IOException e) {
                log.error("can't create SMB_FileHandle instanz", e);
            }
            //ask filter if exist
            if (filter == null || filter.accept(lf)) retList.add(lf);
        }
        SMB_FileTransferHandle ret[] = new SMB_FileTransferHandle[retList.size()];
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
        throw new RuntimeException("FilenameFilter not supported");
    }

    /**
     * Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory.
     *
     * @param suffix
     */
    @Override
    public FileTransferHandle[] list(String suffix) {
        return list(new FileTransferHandleFilter() {
            @Override
            public boolean accept(FileTransferHandle pathname) {
                if (suffix == null) return false;
                if (!(pathname instanceof SMB_FileTransferHandle)) return false;
                String ext = pathname.extension();
                if (ext == null && suffix != null) return false;
                return ext.equals(suffix);
            }
        });
    }

    /**
     * Returns true if this file is a directory.
     */
    @Override
    public boolean isDirectory() {
        return getShare().folderExists(path());
    }

    /**
     * Returns a handle to the child with the specified name.
     *
     * @param name
     */
    @Override
    public FileTransferHandle child(String name) {
        if (NAME != null) throw new RuntimeException("Can't create child from File");
        try {
            return new SMB_FileTransferHandle(CRED, PATH, name);
        } catch (IOException e) {
            log.error("can't create SMB_FileHandle instanz", e);
        }
        return null;
    }

    /**
     * Returns a handle to the parent of this FileTransferHandle.
     */
    @Override
    public FileTransferHandle parent() {
        String path = path();
        int index = path.lastIndexOf("/");
        try {
            return new SMB_FileTransferHandle(this.CRED, path.substring(0, index));
        } catch (IOException e) {
            log.error("can't create SMB_FileHandle instanz", e);
        }
        return null;
    }

    @Override
    public void mkdirs() {
        DiskShare share = getShare();
        String DIRS[] = PATH.split("/");

        String dirs = "";
        String pr = "";
        for (String p : DIRS) {
            dirs = dirs + pr + p;
            if (!share.folderExists(dirs)) {
                share.mkdir(dirs);
            }
            pr = "/";
        }

    }

    /**
     * Returns true if the file exists.
     */
    @Override
    public boolean exists() {
        return isDirectory() ? getShare().folderExists(path()) : getShare().fileExists(path());
    }

    /**
     * Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     */
    @Override
    public boolean delete() {
        String path = path();
        DiskShare share = getShare();

        if (isDirectory()) {
            if (!share.folderExists(path)) return false;
            share.rmdir(path, true);
            try {
                share.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            share = getShare();
            return !share.folderExists(path);
        } else {
            if (!share.fileExists(path)) return false;
            share.rm(path);
            try {
                share.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            share = getShare();
            return !share.fileExists(path);
        }
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
        return 0;
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
                return fth.exists() && fth.isDirectory();
            }
        });
    }

    @Override
    public FileTransferHandle getRoot() {
        try {
            return new SMB_FileTransferHandle(this.CRED, "/");
        } catch (IOException e) {
            log.error("can't create SMB_FileHandle instanz", e);
        }
        return null;
    }

    @Override
    public BasicFileAttributes getBasicFileAttributes() throws IOException, NotImplementedException {
        throw new NotImplementedException("getBasicFileAttributes() not implemented for SMB-FileTransferHandle");
    }

    @Override
    public boolean createNewFile() throws NotImplementedException {
        throw new NotImplementedException("createNewFile() not implemented for SMB-FileTransferHandle");
    }

    @Override
    public void zipTo(FileTransferHandle targetFileHandle) throws IOException, NotImplementedException {
        throw new NotImplementedException("zipTo() not implemented for SMB-FileTransferHandle");
    }

    /**
     * Returns true if the binary content of given file transfer handle equals
     *
     * @param other
     * @return
     */
    @Override
    public boolean binaryEquals(FileTransferHandle other) throws NotImplementedException {
        throw new NotImplementedException("binaryEquals() not implemented for SMB-FileTransferHandle");
    }

    @Override
    public String toString() {
        return this.path();
    }
}
