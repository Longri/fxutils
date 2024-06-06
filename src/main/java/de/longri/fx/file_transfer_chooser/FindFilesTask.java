/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2022 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.longri.fx.file_transfer_chooser;

import java.util.Objects;

import de.longri.filetransfer.FileTransferHandle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FindFilesTask extends Task<Integer> {

    private final static Logger log = LoggerFactory.getLogger(FindFilesTask.class);

    private final ObservableList<IndexedPath> pathsToUpdate;

    private final FileTransferHandle directory;

    private final DoubleProperty duration;

    public FindFilesTask(FileTransferHandle searchFolder, ObservableList<IndexedPath> listOfPaths) {
        this.pathsToUpdate = Objects.requireNonNull(listOfPaths, "listOfPaths must not be null");
        this.directory = searchFolder;
        this.duration = new SimpleDoubleProperty(0d);
    }

    /**
     * Even in case the directory to be processed is empty or does not exist,
     * the consumer collection is always cleared as first step.
     *
     * @return number of files found and processed
     */
    @Override
    protected Integer call() throws Exception {
        Invoke.andWait(pathsToUpdate::clear);
        long start = System.currentTimeMillis();
        if (null == directory) {
            return 0;
        }

        FileTransferHandle[] files = directory.list();
        if (null == files) {
            return 0;
        }

        if (files.length == 0)
            return 0;

        updateProgress(0, files.length);
        int progressIntervall = getProgressInterval(files.length);
        RefreshBuffer buffer = RefreshBuffer.get(this, files.length, pathsToUpdate);
        for (int f = 0; f < files.length; f++) {
            if (isCancelled()) {
                updateProgress(f, files.length);
                duration.set((System.currentTimeMillis() - start) / 1E3);
                buffer.flush();
                break;
            }
            if (f % progressIntervall == 0) {
                updateProgress(f + 1, files.length);
            }
            if (!files[f].isDirectory()) {
                buffer.update(files[f]);
            }
        }
        buffer.flush();
        updateProgress(files.length, files.length);
        duration.set((System.currentTimeMillis() - start) / 1E3);
        return files.length;
    }

    @Override
    protected void running() {
        super.running();
        log.info("in {}", directory.path());
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        log.info("with {} files out of {} entries after {} sec",pathsToUpdate.size(), getValue(), duration.get());
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        log.info("with {} files after {} seconds!", pathsToUpdate.size(), duration.get());
    }

    @Override
    protected void failed() {
        super.failed();
        String message = String.format("after indexing %s files with an error.", pathsToUpdate.size());
        log.warn(message,getException());
    }

    protected int getProgressInterval(int length) {
        int divider = 1;
        if (length >= 200) {
            divider = length / 200;
        }
        return divider;
    }
}
