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

public class Locations {

	public static Location at(FileTransferHandle path) {
		FileTransferHandle file = Objects.requireNonNull(path, "path must not be null");
		if (null != path.parent() && !file.isDirectory()) {
			FileTransferHandle parent = path.parent();
			return new NamedLocation(parent);
		}
		return withName(path.toString(), path);
	}

	public static Location withName(String name, FileTransferHandle path) {
		return new NamedLocation(name, path);
	}

	private Locations() {
		/*
		 * Collection of static factory methods, 
		 * not intended for instantiation.
		 * 
		 */
	}
}
