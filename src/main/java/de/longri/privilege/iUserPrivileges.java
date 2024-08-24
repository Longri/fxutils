package de.longri.privilege;

import java.util.ArrayList;

/**
 * The iUserPrivileges interface represents the privileges of a user in a system. It provides methods to get the list of privileges and groups associated with the user, check if the
 *  user has a specific privilege or set of privileges, add or remove privileges from the user, and add or remove groups from the user.
 */
public interface iUserPrivileges {

    /**
     * Retrieves the list of privileges associated with this user.
     *
     * This method returns an ArrayList of PRIVILEGE objects representing the privileges of the user.
     *
     * @return The list of privileges associated with this user.
     */
    ArrayList<PRIVILEGE> getPrivilegeList();

    /**
     * Returns the list of groups associated with this user.
     *
     * This method returns an ArrayList of GROUP objects representing the groups of the user.
     *
     * @return The list of groups associated with this user.
     */
    ArrayList<GROUP> getGroupList();


    /**
     * Checks if the user has the specified privileges associated with the given groups.
     *
     * @param groups The groups to check for privilege.
     * @return {@code true} if the user has one or more privileges associated with any of the given groups, {@code false} otherwise.
     */
    default boolean hasPrivilege(GROUP... groups) {
        for (GROUP gr : groups)
            for (GROUP group : getGroupList())
                if (group == gr) return true;
        return false;
    }

    /**
     * Checks if the user has the specified privilege.
     *
     * This method iterates over the user's list of privileges and groups to determine if the user has the specified privilege. It returns {@code true} if the user has the privilege
     *  and {@code false} otherwise.
     *
     * @param privilege The privilege to check for.
     * @return {@code true} if the user has the specified privilege, {@code false} otherwise.
     */
    default boolean hasPrivilege(PRIVILEGE privilege) {
        for (PRIVILEGE pri : getPrivilegeList())
            if (pri.ID == privilege.ID) return true;

        for (GROUP group : getGroupList()) {
            for (PRIVILEGE pri : group.PRIVILEGE_LIST)
                if (pri.ID == privilege.ID) return true;
        }
        return false;
    }

    /**
     * Checks if the user has the specified privilege.
     *
     * This method iterates over the user's list of privileges and groups to determine if
     * the user has the specified privilege. It returns true if the user has the privilege
     * and false otherwise.
     *
     * @param privilege The privilege to check for.
     * @return true if the user has the specified privilege, false otherwise.
     */
    default boolean hasExplicitPrivilege(PRIVILEGE privilege) {
        for (PRIVILEGE pri : getPrivilegeList())
            if (pri.ID == privilege.ID) return true;
        return false;
    }

    /**
     * Checks if the user has the specified privileges.
     *
     * This method iterates over the user's list of privileges and groups to determine if the user has all of the specified privileges.
     * It returns {@code true} if the user has all the specified privileges, and {@code false} otherwise.
     *
     * @param privileges The privileges to check for.
     * @return {@code true} if the user has all the specified privileges, {@code false} otherwise.
     */
    default boolean hasPrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE privilege : privileges)
            if (!hasPrivilege(privilege)) return false;
        return true;
    }

    /**
     * Adds a group to the user's list of groups.
     *
     * This method adds the specified group to the list of groups associated with the user.
     * The group is added to the user's list of groups.
     *
     * @param group The group to be added.
     */
    default void addGroup(GROUP group) {
        getGroupList().add(group);
    }

    /**
     * Removes the specified privileges from the list of privileges associated with this user.
     *
     * @param privileges The privileges to be removed.
     */
    default void removePrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE privilege : privileges)
            getPrivilegeList().remove(privilege);
    }

    /**
     * Adds one or more privileges to the user's list of privileges.
     *
     * This method adds the specified privileges to the list of privileges associated with the user.
     *
     * @param privileges One or more PRIVILEGE objects to be added to the user's list of privileges.
     */
    default void addPrivilege(PRIVILEGE... privileges) {
        for (PRIVILEGE privilege : privileges)
            getPrivilegeList().add(privilege);
    }
}
