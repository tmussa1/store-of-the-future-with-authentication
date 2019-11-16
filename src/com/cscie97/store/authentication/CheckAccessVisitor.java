package com.cscie97.store.authentication;


import java.util.ArrayList;
import java.util.List;

public class CheckAccessVisitor implements Ivisitor {

    private AuthenticationToken token;
    private Resource resource;
    private Permission permission;
    private List<Boolean> hasPermission;

    public CheckAccessVisitor(AuthenticationToken token, Resource resource, Permission permission) {
        this.token = token;
        this.resource = resource;
        this.permission = permission;
        this.hasPermission = new ArrayList<>();
    }

    @Override
    public void visit(AuthenticationService authenticationService) {
        IAuthenticationService service = AuthenticationService.getInstance();
        service.getUsers().stream()
                .forEach(user -> user.accept(this));
    }

    @Override
    public void visit(User user) {
        user.getEntitlements().stream()
                .forEach(entitlement -> entitlement.accept(this));
    }

    @Override
    public void visit(Role role) {
        hasPermission.add(role.hasPermission(token, resource, permission));
    }

    @Override
    public void visit(Permission permission) {
        hasPermission.add(permission.hasPermission(token, resource, permission));
    }

    @Override
    public void visit(ResourceRole resourceRole) {
        hasPermission.add(resourceRole.hasPermission(token, resource, permission));
    }

    @Override
    public void visit(Resource resource) {

    }

    @Override
    public void visit(AuthenticationToken authenticationToken) {

    }

    public boolean hasPermission() {
        return hasPermission.contains(true);
    }
}
