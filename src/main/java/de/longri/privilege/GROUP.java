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
 * The GROUP class represents a group of users with certain privileges.
 * It contains information about the group ID, name, and the list of privileges and users in the group.
 */
public class GROUP {

    final int ID;
    final String Name;
    final ArrayList<PRIVILEGE> PRIVILEGE_LIST = new ArrayList<>();
    final ArrayList<USER> USER_LIST = new ArrayList<>();


    /**
     * Represents a group of users with certain privileges.
     *
     * This class contains information about the group's ID and name.
     * It also manages the list of privileges and users associated with the group.
     *
     * @param id The ID of the group.
     * @param name The name of the group.
     */
    public GROUP(int id, String name) {
        ID = id;
        Name = name;
    }

    /**
     * Represents a group of users with certain privileges.
     *
     * This class contains information about the group's ID and name.
     * It also manages the list of privileges and users associated with the group.
     */
    public GROUP(int id, String name, PRIVILEGE... privileges) {
        ID = id;
        Name = name;
        addPrivilege(privileges);
    }

    /**
     * The GROUP class represents a group of users with certain privileges.
     *
     * This class contains information about the group's ID and name.
     * It also manages the list of privileges and users associated with the group.
     */
    public GROUP(int id, String name, USER... users) {
        ID = id;
        Name = name;
        addUser(users);
    }

    /**
     * Adds one or more users to the group.
     *
     * This method adds the specified users to the group. Each user is added to the USER_LIST of the group and the group is added to the GROUP_LIST of each user.
     *
     * @param USERS The users to be added to the group.
     * @return The group object itself.
     */
    public GROUP addUser(USER... USERS) {
        for (USER user : USERS) {
            USER_LIST.add(user);
            user.addGroup(this);
        }
        return this;
    }

    /**
     * Adds one or more privileges to the group.
     *
     * This method adds the specified privileges to the group's PRIVILEGE_LIST.
     *
     * @param privileges The privileges to be added to the group.
     * @return The group object itself.
     */
    public GROUP addPrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE priv : privileges)
            PRIVILEGE_LIST.add(priv);
        return this;
    }

    /**
     * Removes the specified privileges from the group's PRIVILEGE_LIST.
     * This method removes each privilege from the list of privileges in the group.
     *
     * @param privileges The privileges to be removed from the group.
     */
    public void removePrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE priv : privileges)
            PRIVILEGE_LIST.remove(priv);
    }

    @Override
    public String toString() {
        return "GROP: " + Name;
    }
}
