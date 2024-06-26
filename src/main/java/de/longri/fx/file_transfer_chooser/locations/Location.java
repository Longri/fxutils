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

import java.nio.file.Path;
import java.util.Objects;

public abstract class Location implements Comparable<Location> {

	public abstract String getName();

	public abstract boolean exists();
	
	public abstract FileTransferHandle getPath();

	@Override
	public int compareTo(Location o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPath(), getName());
	}

	@Override
	public boolean equals(Object obj) {

		if (null == obj) 
			return false;

		if (this == obj)
			return true;

		if (!(obj instanceof Location))
			return false;

		Location other = (Location) obj;

		return     Objects.equals(getPath(), other.getPath())
				&& Objects.equals(getName(), other.getName());
	}
}
