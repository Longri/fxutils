/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
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
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FileUpdateService extends javafx.concurrent.Service<Integer> implements UpdateService {

	private ObjectProperty<FileTransferHandle> rootFolder = new SimpleObjectProperty<>();

	private ObservableList<IndexedPath> pathsToUpdate;
	
	private Thread shutdownThread = null;

	public FileUpdateService(FileTransferHandle folderToStart, ObservableList<IndexedPath> paths) {
		setSearchLocation(folderToStart);
		assignTargetCollection(paths);
		registerShutdownHook();
	}

	private void assignTargetCollection(ObservableList<IndexedPath> paths) {
		pathsToUpdate = Objects.requireNonNull(paths, "Target collection paths must not be null");
	}

	private void setSearchLocation(FileTransferHandle folderToStart) {
		rootFolder.setValue(obtainDirectory(folderToStart));
	}

	private FileTransferHandle obtainDirectory(FileTransferHandle folderToStart) {
		if (null == folderToStart)
			return null;
		
		if (folderToStart.isDirectory())
			return folderToStart;
		else
			return folderToStart.parent();
	}

	@Override
	protected Task<Integer> createTask() {
		return new FindFilesTask(rootFolder.getValue(), pathsToUpdate);
	}

	@Override
	public void restartIn(FileTransferHandle directory) {
		if (null != directory)
			restartInDirectory(directory);
	}

	@Override
	public ObjectProperty<FileTransferHandle> searchPathProperty() {
		return this.rootFolder;
	}

	@Override
	public void refresh() {
		this.restart();
	}

	@Override
	public void cancelUpdate() {
		this.cancel();
	}

	@Override
	public void startUpdate() {
		this.start();
	}

	private void restartInDirectory(FileTransferHandle directory) {
		if (directory.isDirectory())
			refreshWhenExists(directory);
		else
			attemptRefreshUsingParent(directory);

	}

	private void attemptRefreshUsingParent(FileTransferHandle directory) {
		FileTransferHandle parent = directory.parent();
		if (null != parent)
			refreshWhenExists(parent);
	}

	protected void refreshWhenExists(FileTransferHandle location) {
		if (location.exists())
			setLocationAndRefresh(location);
	}

	private void setLocationAndRefresh(FileTransferHandle location) {
		setSearchLocation(location);
		this.refresh();
	}

	private void registerShutdownHook() {
		Runnable shutDownAction = () -> Platform.runLater(this::cancelUpdate);
		shutdownThread = new Thread(shutDownAction);
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}
	
	protected Thread getShutdownThread() {
		return this.shutdownThread;
	}

}
