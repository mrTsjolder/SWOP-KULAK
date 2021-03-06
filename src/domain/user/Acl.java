package domain.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles the authorization of user and keeps track of the logged
 * in user.
 * 
 * @author Mathias, Frederic, Pieter-Jan
 */
public class Acl {
    
    private final Map<Role, List<String>> permissions;
    
    /**
     * Initializes a new Acl
     */
    public Acl() {
       permissions = new HashMap<>();
    }
    
    /**
     * Checks whether the given user has the given permission
     * 
     * @param user The user to check the permission of
     * @param permission The permission to check
     * @return True if and only if the role of the user exists in this acl and
     * if the role of the given user has the given permission.
     */
    public boolean hasPermission(User user, String permission) {
        if(!permissions.containsKey(user.getRole())){
            return false;
        }
        
        return permissions.get(user.getRole()).contains(permission);
    }
    
    /**
     * Returns the permissions associated with the given user.
     * 
     * @param user The user to get the permissions of
     * @return The list of permissions associated to the given user
     */
    public List<String> getPermissions(User user) {
        return permissions.get(user.getRole());
    }
    
     /**
     * Returns the permissions associated with the given user.
     * 
     * @param role The role to get the permissions of
     * @return The list of permissions associated to the given user
     */
    public List<String> getPermissions(Role role) {
        return permissions.get(role);
    }
    
    /**
     * Adds the given permission to the permission of the given user
     * 
     * @param role The role to add the permission to
     * @param permission The permission to add
     */
    public void addPermission(Role role, String permission) {
        if(!permissions.containsKey(role))
            permissions.put(role, new ArrayList<>());
        
        permissions.get(role).add(permission);
    }
    
    /**
     * Adds a new entry of the given role associated with the given list
     * of permissions to this acl. If there already exists an entry for the 
     * given role, the permissions are replaced by the given permissions.
     * 
     * @param role The role to add
     * @param permissionList The permissions to associate with the given role
     */
    public void addEntry(Role role, List<String> permissionList) {
        if(permissionList == null)
            permissionList = new ArrayList<>();
        
        permissions.put(role, new ArrayList<>(permissionList));
    }
}
