package com.cscie97.store.authentication;

import org.jetbrains.annotations.Nullable;

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

    @Override
    public void accept(Ivisitor ivisitor) {
        ivisitor.visit(this);
    }

    @Override
    boolean hasPermission(AuthenticationToken authToken, @Nullable Resource resource, Permission permission) {
        return permission.getPermissionId().equals(permissionId);
    }

    public String getPermissionId() {
        return permissionId;
    }
}
