package com.cscie97.store.authentication;


import java.util.List;


public interface IAuthenticationService {
    Permission createPermission(String permissionId, String permissionName, String permissionDescription);
    Role createRole(String roleId, String roleName, String roleDescription);
    Role addPermissionToRole(String roleId, String permissionId) throws AuthenticationServiceException;
    Role addChildRoleToParentRole(String parentRoleId, String childRoleId);
    Role getRoleById(String roleId) throws AuthenticationServiceException;
    Permission getPermissionById(String permissionId) throws AuthenticationServiceException;
    User createUser(String userId);
    User getUserByUserId(String userId) throws AuthenticationServiceException;
    User addCredentialsToUser(String userId, Credential credential);
    User addEntitlementToUser(String userId, Entitlement entitlement);
    Resource createResource(String resourceId, String resourceName);
    Resource getResourceByResourceId(String resouceId) throws AuthenticationServiceException;
    ResourceRole createResourceRole(String resourceRoleId, String resourceRoleName, String resourceRoleDesc, String resourceId);
    ResourceRole addEntitlementsToResourceRole(String resourceRoleId, String entitlementId);
    ResourceRole addResourcesToResourceRole(String resourceId, String resourceRoleId);
    ResourceRole getResourceRoleByResourceRoleId(String resourceRoleId) throws AuthenticationServiceException;
    Entitlement getEntitlementByEntitlementId(String entitlementId) throws AuthenticationServiceException;
    User addResourceRoleToUser(String userId, String resourceRoleId);
    Role addChildResourceRoleToParentRole(String parentRoleId, String childResourceRoleId);
    AuthenticationToken generateToken(String userId, String userName, String password);
    AuthenticationToken generateToken(String userId, String voiceFacePrint);
    AuthenticationToken validateIfTokenExists(String tokenId) throws AccessDeniedException;
    State checkTokenExpiry(String tokenId);
    boolean sessionTimedOut(String tokenId);
    State logOut(String tokenId);
    String getInventoryPrint();
    List<User> getUsers();
    AuthenticationToken findValidAuthenticationTokenForAUser(String userId) throws AccessDeniedException;
    void checkAccess(String tokenId, Resource resource, Permission permission) throws AccessDeniedException;
}
