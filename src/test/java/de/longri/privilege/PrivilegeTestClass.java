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

public class PrivilegeTestClass extends Privileges {

    static final PrivilegeTestClass INSTANCE = new PrivilegeTestClass();

    public static PRIVILEGE WRITE = INSTANCE.create("write");

    public static PRIVILEGE READ = INSTANCE.create("read");

    /**
     * Only user with WRITE PRIVILEGE can use this method.
     * A PrivilegedActionException will throw without user Privileges WRITE!
     * @param user
     */
    public static void writeToDB(User user) {
        INSTANCE.perform(user, WRITE, new perform() {
            @Override
            public void run() {
                //TODO implement your work here
            }
        });
    }

}
