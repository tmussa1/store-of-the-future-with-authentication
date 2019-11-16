package com.cscie97.store.authentication;


public class CheckAccessVisitor implements Ivisitor {

    private AuthenticationToken token;
    private Resource resource;
    private Permission permission;
    private boolean hasPermission = true;

    public CheckAccessVisitor(AuthenticationToken token, Resource resource, Permission permission) {
        this.token = token;
        this.resource = resource;
        this.permission = permission;
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
        hasPermission = role.hasPermission(token, resource, permission);
    }

    @Override
    public void visit(Permission permission) {
        hasPermission = permission.hasPermission(token, resource, permission);
    }

    @Override
    public void visit(ResourceRole resourceRole) {
        hasPermission = resourceRole.hasPermission(token, resource, permission);
    }

    @Override
    public void visit(Resource resource) {

    }

    @Override
    public void visit(AuthenticationToken authenticationToken) {

    }

    public boolean hasPermission() {
        return hasPermission;
    }
}
