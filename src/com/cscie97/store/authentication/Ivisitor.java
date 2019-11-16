package com.cscie97.store.authentication;

public interface Ivisitor {
    void visit(AuthenticationService authenticationService);
    void visit(User user);
    void visit(Role role);
    void visit(Permission permission);
    void visit(ResourceRole resourceRole);
    void visit(Resource resource);
    void visit(AuthenticationToken authenticationToken);
}
