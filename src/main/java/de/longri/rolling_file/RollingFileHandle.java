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

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * a file handle for rolling files
 * <p>
 * properties:
 * - base          (base path for store/copy/delete)
 * - filename      (name of actual using file)
 * - date          (the date format for rolling file)
 * - age           (the max age for rolling files)
 * - keep          (count of keeping files)
 */
public class RollingFileHandle extends ImplementedFileTransferHandle {

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    static FileTransferHandle getFth(FileTransferHandle baseDir, String filename) {
        if (baseDir == null) throw new RuntimeException("baseDir can't be NULL");
        if (filename == null) throw new RuntimeException("filename can't be NULL");
        if (filename.isEmpty()) throw new RuntimeException("filename can't be empty");


        if (filename.contains(baseDir.path())) {
            filename = filename.replace(baseDir.path(), "");
        }

        return baseDir.child(filename);
    }

    private final FileTransferHandle BASE_DIR;

    public RollingFileHandle(FileTransferHandle baseDir, String filename) throws RuntimeException {
        super(getFth(baseDir, filename));
        BASE_DIR = baseDir;

        if (baseDir.exists() && !baseDir.isDirectory()) throw new RuntimeException("Base dir can't be a file");

        if (!baseDir.exists()) {
            //create base dir if not exist
            baseDir.mkdirs();
            sleep(200);
            if (!baseDir.exists()) throw new RuntimeException("Can't create base dir");
        }
    }

    /**
     * @param datePattern
     * @param age         days for keep, before rolling
     * @param keep
     * @return
     */
    public boolean rolling(String datePattern, int age, int keep) {
        boolean rolling = false;
        if (!this.exists()) {
            return rolling;
        }

        try {
            BasicFileAttributes attr = this.getBasicFileAttributes();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime creationTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
            LocalDateTime keepDate = creationTime.plusDays(age - 1);

            if (!isDateBefore(keepDate, now)) {
                return rolling;
            }
        } catch (Exception e) {
            return rolling;
        }

        //create new storage name from pattern
        sleep(500);
        String newName = getFileNameFromPattern(datePattern);
        FileTransferHandle movedFile = BASE_DIR.child(newName);

        if (movedFile.extension().equals("zip")) {

            try {
                this.zipTo(movedFile);
            } catch (IOException | NotImplementedException e) {
                throw new RuntimeException(e);
            }

            // delete the current log file, this will then create new later
            sleep(500);
            this.delete();
        } else {
            this.moveTo(movedFile);
        }
        sleep(500);
        if (!movedFile.exists())
            throw new RuntimeException("can't moving rolling file from '" + this + "' to '" + movedFile + "'");
        
        keep(datePattern, age, keep);
        return true;
    }


    boolean isDateBefore(LocalDateTime ldt1, LocalDateTime ldt2) {
        // get only date, without time
        LocalDate ld1 = ldt1.toLocalDate();
        LocalDate ld2 = ldt2.toLocalDate();

        return ld1.isBefore(ld2);
    }


    void keep(String datePattern, int age, int keep) {
        String searchSplit = datePattern.startsWith("/") ? datePattern.replaceFirst("/", "") : datePattern;
        String[] search;
        int posStart = searchSplit.indexOf("%d{");
        int posEnd = searchSplit.indexOf("}", posStart) + 1;
        if (posStart >= 0 && posEnd > posStart) {
            String dateFormatSubStr = searchSplit.substring(posStart, posEnd);
            searchSplit = searchSplit.replace(dateFormatSubStr, "@#@#@#");
            search = searchSplit.split("@#@#@#");
        } else {
            search = new String[]{searchSplit};
        }

        FileTransferHandle[] rollingFiles = BASE_DIR.list(new FileTransferHandleFilter() {
            @Override
            public boolean accept(FileTransferHandle pathname) {
                String name = pathname.name();
                for (String s : search) {
                    if (!name.contains(s)) return false;
                }
                return true;
            }
        });

        if (rollingFiles.length > keep) {
            //sort files at date
            Arrays.sort(rollingFiles, Comparator.comparingLong(FileTransferHandle::lastModified).reversed());

            //delete all file's they're over rolling

            for (int i = keep; i < rollingFiles.length; i++) {
                rollingFiles[i].delete();
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    static String getFileNameFromPattern(String pattern) {
        int posStart = pattern.indexOf("%d{");
        int posEnd = pattern.indexOf("}", posStart) + 1;
        if (posStart >= 0 && posEnd > posStart) {
            String dateFormatSubStr = pattern.substring(posStart + 3, posEnd - 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatSubStr);
            String dateStr = formatter.format(LocalDate.now().minusDays(1));// file is from yesterday
            pattern = pattern.replace("%d{" + dateFormatSubStr + "}", dateStr);
        }
        return pattern;
    }

}
