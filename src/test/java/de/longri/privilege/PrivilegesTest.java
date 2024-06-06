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

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PrivilegesTest {

    private static class TestButton {
        boolean disable = false;

        void setDisable(boolean di) {
            disable = di;
        }
    }

    static TestButton testBtn = new TestButton();

    @Test
    void create() {
        Privileges privileges = new Privileges();
        assertNotNull(privileges);

        PRIVILEGE priv = privileges.create("test1");
        assertNotNull(priv);
        assertThrows(PrivilegeDoubleExistException.class, () -> {
            PRIVILEGE priv2 = privileges.create("test1");
        });

    }

    @Test
    void privilege() {
        Privileges privileges = new Privileges();
        assertNotNull(privileges);

        PRIVILEGE priv1 = privileges.create("test1");
        PRIVILEGE priv2 = privileges.create("test2");
        PRIVILEGE priv3 = privileges.create("test3");

        USER user = new USER(0, "user1");

        GROUP adminGroup = new GROUP(0, "admins");

        adminGroup.addPrivilege(priv1, priv2);
        user.addPrivilege(priv1);
        adminGroup.addUser(user);

        assertTrue(user.hasPrivilege(priv1, priv2));
        assertFalse(user.hasPrivilege(priv3));


        AtomicInteger cont = new AtomicInteger(3);
        privileges.perform(user, priv1, () -> {
            cont.addAndGet(3);
        });

        assertEquals(6, cont.get());

        privileges.perform(user, priv2, () -> {
            cont.addAndGet(2);
        });

        assertEquals(8, cont.get());

        assertThrows(PrivilegedActionException.class, () -> {
            privileges.perform(user, priv3, () -> {
                cont.addAndGet(2);
            });
        });

        assertEquals(8, cont.get());

        adminGroup.removePrivilege(priv2);
        assertTrue(user.hasPrivilege(priv1));
        assertFalse(user.hasPrivilege(priv2));
        assertFalse(user.hasPrivilege(priv3));


        assertThrows(PrivilegedActionException.class, () -> {
            privileges.perform(user, priv2, () -> {
                cont.addAndGet(7);
            });
        });
        assertEquals(8, cont.get());

        adminGroup.addPrivilege(priv2);
        user.addPrivilege(priv2);
        privileges.perform(user, priv2, () -> {
            cont.addAndGet(7);
        });
        assertEquals(15, cont.get());
        assertTrue(user.hasPrivilege(priv1, priv2));
        assertFalse(user.hasPrivilege(priv3));

        adminGroup.removePrivilege(priv2);
        assertTrue(user.hasPrivilege(priv1, priv2));
        assertFalse(user.hasPrivilege(priv3));
        privileges.perform(user, priv2, () -> {
            cont.addAndGet(5);
        });
        assertEquals(20, cont.get());

        user.removePrivilege(priv2);
        assertTrue(user.hasPrivilege(priv1));
        assertFalse(user.hasPrivilege(priv2));
        assertFalse(user.hasPrivilege(priv3));
        assertThrows(PrivilegedActionException.class, () -> {
            privileges.perform(user, priv2, () -> {
                cont.addAndGet(7);
            });
        });
        assertEquals(20, cont.get());
    }

    @Test
    void privilegeApplicationTest() {

        USER user1 = new USER(1, "TestUser1");
        USER user2 = new USER(2, "Testuser2", PrivilegeTestClass.WRITE);

        GROUP admin = new GROUP(0, "ADMIN");
        admin.addUser(user1);

        assertFalse(user2.hasPrivilege(admin));
        assertTrue(user1.hasPrivilege(admin));

        /**
         * disable button if user has no privileges
         */
        testBtn.setDisable(!user2.hasPrivilege(PrivilegeTestClass.WRITE));
        assertFalse(testBtn.disable);

        testBtn.setDisable(!user1.hasPrivilege(PrivilegeTestClass.WRITE));
        assertTrue(testBtn.disable);

        PrivilegeTestClass.writeToDB(user2);

        assertThrows(PrivilegedActionException.class, () -> {
            PrivilegeTestClass.writeToDB(user1); // user1 has no write privileges
        });
    }
}