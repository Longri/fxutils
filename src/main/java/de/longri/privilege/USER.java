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

import java.util.ArrayList;

/**
 * The User class represents a user in a system. It implements the iUserPrivileges interface to provide functionality for managing user privileges and groups.
 */
public class User implements iUserPrivileges {

    final protected int ID;
    final protected String NAME;

    /**
     * The PRIVILEGE_LIST variable represents a list of privileges associated with a user in a system.
     *
     * It is an ArrayList of PRIVILEGE objects.
     *
     * This variable is declared in the User class, which represents a user in the system and implements the iUserPrivileges interface.
     * The PRIVILEGE_LIST variable is initialized as an empty ArrayList in the User class constructor.
     *
     * The User class provides methods to manipulate the PRIVILEGE_LIST variable, such as adding or removing privileges from the list.
     *
     * @see User
     * @see iUserPrivileges
     * @see PRIVILEGE
     */
    ArrayList<PRIVILEGE> PRIVILEGE_LIST = new ArrayList<>();

    /**
     * The GROUP_LIST variable represents a list of groups in the system.
     *
     * It is an ArrayList of GROUP objects.
     *
     * This variable is declared in the User class, which represents a user in the system and implements the iUserPrivileges interface.
     * The GROUP_LIST variable is initialized as an empty ArrayList in the User class constructor.
     *
     * The User class provides methods to manipulate the GROUP_LIST variable, such as adding or removing groups from the list.
     *
     * @see User
     * @see GROUP
     */
    ArrayList<GROUP> GROUP_LIST = new ArrayList<>();

    /**
     * Creates a new User with the given ID and name.
     *
     * @param id   the ID of the user
     * @param name the name of the user
     */
    public User(int id, String name) {
        ID = id;
        NAME = name;
    }

    /**
     * Creates a new User with the given ID and name, and optionally adds one or more privileges to the user's list of privileges.
     *
     * The User class represents a user in a system and implements the iUserPrivileges interface to provide functionality for managing user privileges and groups.
     *
     * The User constructor initializes the ID and NAME variables of the User object with the specified values. It also calls the addPrivilege() method to add any privileges passed
     *  as arguments.
     *
     * @param id the ID of the user
     * @param name the name of the user
     * @param privileges one or more PRIVILEGE objects to be added to the user's list of privileges
     *
     * @see User
     * @see iUserPrivileges
     * @see PRIVILEGE
     */
    public User(int id, String name, PRIVILEGE... privileges) {
        ID = id;
        NAME = name;
        addPrivilege(privileges);
    }

    @Override
    public String toString() {
        return "USER: " + NAME;
    }

    /**
     * Returns the list of privileges associated with this user.
     *
     * This method retrieves the list of privileges associated with the user and returns it as an ArrayList of PRIVILEGE objects.
     *
     * @return The list of privileges associated with this user.
     */
    @Override
    public ArrayList<PRIVILEGE> getPrivilegeList() {
        return PRIVILEGE_LIST;
    }

    /**
     * Returns the list of groups associated with this user.
     *
     * This method retrieves the list of groups associated with the user and returns it as an ArrayList of GROUP objects.
     *
     * @return The list of groups associated with this user.
     */
    @Override
    public ArrayList<GROUP> getGroupList() {
        return GROUP_LIST;
    }
}
