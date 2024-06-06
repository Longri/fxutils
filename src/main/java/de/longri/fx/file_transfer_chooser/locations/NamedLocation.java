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
package de.longri.fx.file_transfer_chooser.locations;

import de.longri.filetransfer.FileTransferHandle;

import java.util.Objects;

class NamedLocation extends Location {
	
	private final String name;
	
	private final FileTransferHandle directory;
	
	protected NamedLocation(FileTransferHandle parent) {
		this(createName(parent), parent);
	}
	
	private static String createName(FileTransferHandle location) {
		Objects.requireNonNull(location, "location must not be null");
		return location.toString();
	}

	protected NamedLocation(String name, FileTransferHandle path) {
		this.name = Objects.requireNonNull(name, "name must not be null");
		this.directory = Objects.requireNonNull(path, "path must not be null");
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean exists() {
		return directory.exists();
	}

	@Override
	public FileTransferHandle getPath() {
		return this.directory;
	}
	
	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
