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
package de.longri.privilege;

/**
 * The PrivilegeDoubleExistException class represents an exception that is thrown when a duplicate privilege is attempted to be created.
 *
 * This exception is thrown when the create() method in the Privileges class is called with a privilege name that already exists in the list of privileges.
 * It extends the RuntimeException class, making it an unchecked exception.
 *
 * The exception message includes the name of the duplicate privilege.
 */
public class PrivilegeDoubleExistException extends RuntimeException {
    /**
     * Constructs a new PrivilegeDoubleExistException with the specified privilege name.
     *
     * This exception is thrown when a duplicate privilege is attempted to be created.
     *
     * @param name the name of the duplicate privilege
     */
    public PrivilegeDoubleExistException(String name) {
        super("the PRIVILEGE " + name + " is exist");
    }
}
