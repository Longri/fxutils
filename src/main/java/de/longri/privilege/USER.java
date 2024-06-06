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

public class USER {

    final protected int ID;
    final protected String NAME;
    final protected ArrayList<PRIVILEGE> PRIVILEGE_LIST = new ArrayList<>();
    final protected ArrayList<GROUP> GROUP_LIST = new ArrayList<>();


    public USER(int id, String name) {
        ID = id;
        NAME = name;
    }

    public USER(int id, String name, PRIVILEGE... privileges) {
        ID = id;
        NAME = name;
        addPrivilege(privileges);
    }

    public boolean hasPrivilege(GROUP... groups) {
        for (GROUP gr : groups)
            for (GROUP group : GROUP_LIST)
                if (group == gr) return true;
        return false;
    }

    public boolean hasExplicitPrivilege(PRIVILEGE privilege) {
        for (PRIVILEGE pri : PRIVILEGE_LIST)
            if (pri.ID == privilege.ID) return true;
        return false;
    }

    public boolean hasPrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE privilege : privileges)
            if (!hasPrivilege(privilege)) return false;
        return true;
    }

    private boolean hasPrivilege(PRIVILEGE privilege) {
        for (PRIVILEGE pri : PRIVILEGE_LIST)
            if (pri.ID == privilege.ID) return true;

        for (GROUP group : GROUP_LIST) {
            for (PRIVILEGE pri : group.PRIVILEGE_LIST)
                if (pri.ID == privilege.ID) return true;
        }
        return false;
    }

    void addGroup(GROUP group) {
        GROUP_LIST.add(group);
    }

    public void removePrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE privilege : privileges)
            PRIVILEGE_LIST.remove(privilege);
    }

    public void addPrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE privilege : privileges)
            PRIVILEGE_LIST.add(privilege);
    }
}
