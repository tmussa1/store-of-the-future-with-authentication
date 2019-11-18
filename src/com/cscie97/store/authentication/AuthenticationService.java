package com.cscie97.store.authentication;


import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AuthenticationService implements IAuthenticationService, Visitable {

    private static IAuthenticationService instance;
    private List<User> users;
    private List<AuthenticationToken> tokens;
    private List<Entitlement> entitlements;
    private List<Resource> resources;
    Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    public AuthenticationService() {
        this.users = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.entitlements = new ArrayList<>();
        this.resources = new ArrayList<>();
    }

    public static IAuthenticationService getInstance(){
        if(instance == null){
            instance = new AuthenticationService();
        }
        return instance;
    }

    @Override
    public void accept(Ivisitor ivisitor) {
        ivisitor.visit(this);
    }

    @Override
    public Permission createPermission(String permissionId, String permissionName, String permissionDescription) {
        Permission permission = new Permission(permissionId, permissionName, permissionDescription);
        entitlements.add(permission);
        return permission;
    }

    @Override
    public Role createRole(String roleId, String roleName, String roleDescription) {
        Role role = new Role(roleId, roleName, roleDescription);
        entitlements.add(role);
        return role;
    }

    @Override
    public Role addPermissionToRole(String roleId, String permissionId)  {
        Permission permission  = null;
        Role role = null;
        try {
            permission = getPermissionById(permissionId);
            role = getRoleById(roleId);
            role.addEntitlement(permission);
        } catch (AuthenticationServiceException e) {
            logger.warning("Adding permission to role failed for "+ e.getReason() + " : " + e.getFix());
        }
        return role;
    }

    @Override
    public Role addChildRoleToParentRole(String parentRoleId, String childRoleId) {
        Role parent = null;
        try {
            parent = getRoleById(parentRoleId);
            Role child = getRoleById(childRoleId);
            parent.addEntitlement(child);
        } catch (AuthenticationServiceException e) {
            logger.warning("Adding role to parent role failed for "+ e.getReason() + " : " + e.getFix());
        }
        return parent;
    }

    @Override
    public User createUser(String userId) {
        User user = new User(userId);
        users.add(user);
        return user;
    }

    @Override
    public User addCredentialsToUser(String userId, Credential credential) {
        User user = null;
        try {
             user = getUserByUserId(userId);
             user.addCredentials(credential);
        } catch (AuthenticationServiceException e) {
            logger.warning("Error finding user " + e.getReason() + " : " + e.getFix());
        }
        return user;
    }

    @Override
    public User addEntitlementToUser(String userId, Entitlement entitlement) {
        User user = null;
        try {
            user = getUserByUserId(userId);
            user.addEntitlements(entitlement);
        } catch (AuthenticationServiceException e) {
            logger.warning("Error finding user " + e.getReason() + " : " + e.getFix());
        }
        return user;
    }

    @Override
    public Resource createResource(String resourceId, String resourceName) {
        Resource resource = new Resource(resourceId, resourceName);
        resources.add(resource);
        return resource;
    }

    @Override
    public ResourceRole createResourceRole(String resourceRoleId, String resourceRoleName, String resourceRoleDesc,
                                           String resourceId) {
        ResourceRole resourceRole = null;
        try {
            resourceRole = new ResourceRole(resourceRoleId, resourceRoleName, resourceRoleDesc);
            Resource resource = getResourceByResourceId(resourceId);
            resourceRole.addResources(resource);
        } catch (AuthenticationServiceException e) {
            logger.warning("Error creating resource role " + e.getReason() + " : " + e.getFix());
        }
        return resourceRole;
    }

    @Override
    public ResourceRole addEntitlementsToResourceRole(String resourceRoleId, String entitlementId) {
        Entitlement entitlement = null;
        ResourceRole resourceRole = null;
        try {
            resourceRole = getResourceRoleByResourceRoleId(resourceRoleId);
            entitlement = getEntitlementByEntitlementId(entitlementId);
            resourceRole.addEntitlement(entitlement);
        } catch (AuthenticationServiceException e) {
           logger.warning("Error adding entitlement " + e.getReason() + " : " + e.getFix());
        }
        return resourceRole;
    }

    @Override
    public ResourceRole addResourcesToResourceRole(String resourceId, String resourceRoleId) {
        ResourceRole resourceRole = null;
        try {
            Resource resource = getResourceByResourceId(resourceId);
            resourceRole = getResourceRoleByResourceRoleId(resourceRoleId);
            resourceRole.addResources(resource);
        } catch (AuthenticationServiceException e) {
            logger.warning("Error adding resource to resource role " + e.getReason() + " : " + e.getFix());
        }
        return resourceRole;
    }

    @Override
    public User addResourceRoleToUser(String userId, String resourceRoleId) {
        User user = null;
        try {
            user = getUserByUserId(userId);
            ResourceRole resourceRole = getResourceRoleByResourceRoleId(resourceRoleId);
            user.addEntitlements(resourceRole);
        } catch (AuthenticationServiceException e) {
            logger.warning("Error adding resource role to user " + e.getReason() + " : " + e.getFix());
        }
        return user;
    }

    @Override
    public Role addChildResourceRoleToParentRole(String parentRoleId, String childResourceRoleId) {
        Role parent = null;
        try {
            parent = getRoleById(parentRoleId);
            ResourceRole child = getResourceRoleByResourceRoleId(childResourceRoleId);
            parent.addEntitlement(child);
        } catch (AuthenticationServiceException e) {
            logger.warning("Error adding resource role to parent role " + e.getReason() + " : " + e.getFix());
        }
        return parent;
    }

    @Override
    public AuthenticationToken generateToken(String userId, String userName, String password) {
        User user = null;
        AuthenticationToken token = null;
        try {
            user = getUserByUserId(userId);
            boolean authenticated = user.checkCredentials(userName, password);
            if(authenticated){
                token = new AuthenticationToken(user);
                tokens.add(token);
            }
        } catch (AuthenticationServiceException | NoSuchAlgorithmException e) {
            logger.warning("Unable ot generate authentication token " + e.getMessage());
        }
        return token;
    }

    @Override
    public AuthenticationToken generateToken(String userId, String voiceFacePrint) {
        User user = null;
        AuthenticationToken token = null;
        try {
            user = getUserByUserId(userId);
            boolean authenticated = user.checkCredentials(voiceFacePrint);
            if(authenticated){
                token = new AuthenticationToken(user);
                tokens.add(token);
            }
        } catch (AuthenticationServiceException e) {
            logger.warning("Unable ot generate authentication token " + e.getReason() + " : " +e.getFix());
        }
        return token;
    }

    @Override
    public AuthenticationToken validateIfTokenExists(String tokenId) throws AccessDeniedException {
        AuthenticationToken token = tokens.stream()
                .filter(aToken -> aToken.getTokenId().equals(tokenId))
                .findFirst().get();
        if(token == null){
            throw new AccessDeniedException("Unable to find token", "Please authenticate again");
        }
        return token;
    }

    @Override
    public State checkTokenExpiry(String tokenId) {
        AuthenticationToken token = null;
        try {
            token = validateIfTokenExists(tokenId);
        } catch (AccessDeniedException e) {
            logger.warning("Unable to check token expiry " + e.getReason() + " : " + e.getFix());
        }
        return token.getExpirationState();
    }

    @Override
    public boolean sessionTimedOut(String tokenId) {
        boolean loggedOut = false;
        AuthenticationToken token = null;
        try {
            token = validateIfTokenExists(tokenId);
            token.expireToken();
            loggedOut = true;
        } catch (AccessDeniedException e) {
            logger.warning("Unable to log out for expiring session "+ e.getReason() + " : " + e.getFix());
        }
        return loggedOut;
    }

    @Override
    public State logOut(String userId) {
        AuthenticationToken token = null;
        try {
            token = findValidAuthenticationTokenForAUser(userId);
            token.logOut();
        } catch (AccessDeniedException e) {
            logger.warning("Unable to log out "+ e.getReason() + " : " + e.getFix());
        }
        return token.getExpirationState();
    }

    @Override
    public String getInventoryPrint() {
        InventoryVisitor inventoryVisitor = new InventoryVisitor();
        this.accept(inventoryVisitor);
        return inventoryVisitor.getInventoryPrint();
    }

    @Override
    public void checkAccess(String tokenId, Resource resource, Permission permission) throws AccessDeniedException {
        AuthenticationToken token = null;
        CheckAccessVisitor checkAccessVisitor = null;
        try {
             token = validateIfTokenExists(tokenId);
             checkAccessVisitor = new CheckAccessVisitor(token, resource, permission);
             this.accept(checkAccessVisitor);
        } catch (AccessDeniedException e) {
            logger.info("Unable to access the resource " + e.getReason() + " : " + e.getFix());
        }
        if(!checkAccessVisitor.hasPermission()){
            throw new AccessDeniedException("Access denied",
                    "Please obtain the correct permission to access resource");
        }
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public Role getRoleById(String roleId) throws AuthenticationServiceException {
        Role role = (Role) entitlements.stream()
                .filter(entitlement -> entitlement instanceof Role && entitlement.getEntitlementId().equals(roleId))
                .findFirst().get();

        if(role == null){
            throw new AuthenticationServiceException("Role not found",
                    "Please make sure you request the role first");
        }
        return role;
    }

    @Override
    public Permission getPermissionById(String permissionId) throws AuthenticationServiceException {
        Permission permission = (Permission) entitlements.stream()
                .filter(entitlement -> entitlement instanceof Permission &&
                        entitlement.getEntitlementId().equals(permissionId))
                .findFirst().get();
        if(permission == null){
            throw new AuthenticationServiceException("Permission not found",
                    "Please make sure you request the permission first");
        }
        return permission;
    }

    @Override
    public User getUserByUserId(String userId) throws AuthenticationServiceException {
        User user= users.stream()
                .filter(aUser -> aUser.getUserId().equals(userId))
                .findFirst().get();
        if(user == null){
            throw new AuthenticationServiceException("User not found ", "User must register first");
        }
        return user;
    }

    @Override
    public Resource getResourceByResourceId(String resouceId) throws AuthenticationServiceException {
        Resource resource = resources.stream()
                .filter(Aresource -> Aresource.getResourceId().equals(resouceId))
                .findFirst().get();
        if(resource == null){
            throw new AuthenticationServiceException("Resource not found",
                    "Please enter a resource that is provisioned");
        }
        return resource;
    }

    @Override
    public ResourceRole getResourceRoleByResourceRoleId(String resourceRoleId) throws AuthenticationServiceException {
        ResourceRole resourceRole = (ResourceRole) entitlements.stream()
                .filter(AresourceRole -> AresourceRole instanceof ResourceRole &&
                        AresourceRole.getEntitlementId().equals(resourceRoleId))
                .findFirst()
                .get();
        if(resourceRole == null){
            throw new AuthenticationServiceException("Resource role not found",
                    "Please make sure that you have a role restricted to a resource");
        }
        return resourceRole;
    }

    @Override
    public AuthenticationToken findValidAuthenticationTokenForAUser(String userId) throws AccessDeniedException {
        AuthenticationToken authenticationToken = tokens.stream()
                .filter(token -> token.getUser().getUserId().equals(userId))
                .findFirst().get();
        if(authenticationToken == null || checkTokenExpiry(authenticationToken.getTokenId()) == State.EXPIRED){
            throw new AccessDeniedException("No authentication found for the user or may have timed out",
                    "Please log in again");
        }
        return authenticationToken;
    }

    @Override
    public Entitlement getEntitlementByEntitlementId(String entitlementId) throws AuthenticationServiceException {
        Entitlement entitlement = entitlements.stream()
                .filter(Anentitlement -> Anentitlement.getEntitlementId().equals(entitlementId))
                .findFirst()
                .get();
        if(entitlement == null){
            throw new AuthenticationServiceException("Entitlement not found", "Please check the entitlement id");
        }
        return entitlement;
    }


}
