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

public class Privileges {
    public ArrayList<PRIVILEGE> ALL_PRIVILEGE_LIST = new ArrayList<>();

    public PRIVILEGE create(String name) {
        // check if exist
        for (PRIVILEGE privilege : ALL_PRIVILEGE_LIST) {
            if (privilege.NAME.equals(name)) {
                throw new PrivilegeDoubleExistException(name);
            }
        }
        PRIVILEGE privilege = new PRIVILEGE(ALL_PRIVILEGE_LIST.size(), name);
        ALL_PRIVILEGE_LIST.add(privilege);
        return privilege;
    }

    public interface perform {
        void run();
    }

    public void perform(USER user, PRIVILEGE privilege, perform o) {
        if (!user.hasPrivilege(privilege))
            throw new PrivilegedActionException(user);
        o.run();
    }
}
