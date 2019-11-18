package com.cscie97.store.authentication;


public class Permission extends Entitlement implements Visitable {

    private String permissionId;
    private String permissionName;
    private String permissionDescription;

    public Permission(String entitlementId, String entitlementName, String entitlementDescription) {
        super(entitlementId, entitlementName, entitlementDescription);
        this.permissionId = entitlementId;
        this.permissionName = entitlementName;
        this.permissionDescription = entitlementDescription;
    }

    public Permission(String permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public void accept(Ivisitor ivisitor) {
        ivisitor.visit(this);
    }

    @Override
    boolean hasPermission(AuthenticationToken authToken, Resource resource, Permission permission) {
        return permission.getPermissionId().equals(permissionId);
    }

    public String getPermissionId() {
        return permissionId;
    }

    @Override
    public String toString() {
        return "Permission with permission id " + permissionId +
                " with permission name " + permissionName +
                " with description " + permissionDescription;
    }
}
