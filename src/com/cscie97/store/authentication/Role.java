package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.List;

public class Role extends Entitlement implements Visitable {

    private List<Entitlement> entitlements;
    private String roleId;
    private String roleName;
    private String roleDescription;

    public Role(String entitlementId, String entitlementName, String entitlementDescription) {
        super(entitlementId, entitlementName, entitlementDescription);
        this.roleId = entitlementId;
        this.roleName = entitlementName;
        this.roleDescription = entitlementDescription;
        this.entitlements = new ArrayList<>();
    }

    public void addEntitlement(Entitlement entitlement){
        this.entitlements.add(entitlement);
    }

    @Override
    public void accept(Ivisitor ivisitor) {
        ivisitor.visit(this);
    }

    @Override
    boolean hasPermission(AuthenticationToken authToken, Resource resource, Permission permission) {
        if(permission.getPermissionId().equals(roleId)){
            return true;
        }
        boolean hasPermission = false;
        for(Entitlement entitlement : entitlements){
            hasPermission = entitlement.hasPermission(authToken, resource, permission);
            if(hasPermission == true){
                break;
            }
        }
        return hasPermission;
    }

    public List<Entitlement> getEntitlements() {
        return entitlements;
    }

    @Override
    public String toString() {
        return "Role with role id " + roleId +
                " with role name " + roleName +
                " with description " + roleDescription;
    }
}
