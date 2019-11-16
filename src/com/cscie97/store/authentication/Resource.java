package com.cscie97.store.authentication;

import org.jetbrains.annotations.Nullable;

public class Resource extends Entitlement implements Visitable {

    public Resource(String entitlementId, String entitlementName, String entitlementDescription) {
        super(entitlementId, entitlementName, entitlementDescription);
    }

    @Override
    public void accept(Ivisitor ivisitor) {

    }

    @Override
    boolean hasPermission(AuthenticationToken authToken, @Nullable Resource resource, Permission permission) {
        return false;
    }
}
