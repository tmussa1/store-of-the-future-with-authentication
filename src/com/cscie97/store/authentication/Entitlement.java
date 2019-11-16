package com.cscie97.store.authentication;


import org.jetbrains.annotations.Nullable;

public abstract class Entitlement implements Visitable{

    private String entitlementId;
    private String entitlementName;
    private String entitlementDescription;

    public Entitlement(String entitlementId, String entitlementName, String entitlementDescription) {
        this.entitlementId = entitlementId;
        this.entitlementName = entitlementName;
        this.entitlementDescription = entitlementDescription;
    }

    /**
     * Resource is null. Subclasses need to implement this method
     * @param authToken
     * @param resource
     * @param permission
     * @return permission exists or not
     */
    abstract boolean hasPermission(AuthenticationToken authToken, @Nullable Resource resource, Permission permission);

    public String getEntitlementId() {
        return entitlementId;
    }
}
