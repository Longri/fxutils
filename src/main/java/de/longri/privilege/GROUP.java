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

public class GROUP {

    final int ID;
    final String Name;
    final ArrayList<PRIVILEGE> PRIVILEGE_LIST = new ArrayList<>();
    final ArrayList<USER> USER_LIST = new ArrayList<>();


    public GROUP(int id, String name) {
        ID = id;
        Name = name;
    }

    public GROUP(int id, String name, PRIVILEGE... privileges) {
        ID = id;
        Name = name;
        addPrivilege(privileges);
    }

    public GROUP(int id, String name, USER... users) {
        ID = id;
        Name = name;
        addUser(users);
    }

    public GROUP addUser(USER... users) {
        for (USER user : users) {
            USER_LIST.add(user);
            user.addGroup(this);
        }
        return this;
    }

    public GROUP addPrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE priv : privileges)
            PRIVILEGE_LIST.add(priv);
        return this;
    }

    public void removePrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE priv : privileges)
            PRIVILEGE_LIST.remove(priv);
    }
}
