package com.cscie97.store.authentication;

public class InventoryVisitor implements Ivisitor {

    private StringBuilder accumulateState;

    public InventoryVisitor() {
        this.accumulateState = new StringBuilder();
    }

    public String getInventoryPrint(){
        return accumulateState.toString();
    }

    @Override
    public void visit(AuthenticationService authenticationService) {
        IAuthenticationService service = AuthenticationService.getInstance();
        accumulateState.append(System.getProperty("line.separator"));
        accumulateState.append(service.toString());
        accumulateState.append(System.getProperty("line.separator"));

        if(service.getUsers().size() > 0){
            service.getUsers()
                    .stream()
                    .forEach(user -> user.accept(this));
        }

        if(service.getTokens().size() > 0){
            service.getTokens()
                    .stream()
                    .forEach(token -> token.accept(this));
        }
    }

    @Override
    public void visit(User user) {
        accumulateState.append(System.getProperty("line.separator"));
        accumulateState.append(user.toString());
        accumulateState.append(System.getProperty("line.separator"));

        if(user.getEntitlements().size() > 0){
            user.getEntitlements()
                    .stream()
                    .forEach(entitlement -> entitlement.accept(this));
        }
    }

    @Override
    public void visit(Role role) {
        accumulateState.append(System.getProperty("line.separator"));
        accumulateState.append(role.toString());
        accumulateState.append(System.getProperty("line.separator"));

        if(role.getEntitlements().size() > 0){
            role.getEntitlements()
                    .stream()
                    .forEach(entitlement -> entitlement.accept(this));
        }
    }

    @Override
    public void visit(Permission permission) {
        accumulateState.append(System.getProperty("line.separator"));
        accumulateState.append(permission.toString());
        accumulateState.append(System.getProperty("line.separator"));
    }

    @Override
    public void visit(ResourceRole resourceRole) {
        accumulateState.append(System.getProperty("line.separator"));
        accumulateState.append(resourceRole.toString());
        accumulateState.append(System.getProperty("line.separator"));

        if(resourceRole.getEntitlements().size() > 0){
            resourceRole.getEntitlements()
                    .stream()
                    .forEach(entitlement -> entitlement.accept(this));
        }

        if(resourceRole.getResources().size() > 0){
            resourceRole.getResources()
                    .stream()
                    .forEach(resource -> resource.accept(this));
        }
    }

    @Override
    public void visit(Resource resource) {
        accumulateState.append(System.getProperty("line.separator"));
        accumulateState.append(resource.toString());
        accumulateState.append(System.getProperty("line.separator"));
    }

    @Override
    public void visit(AuthenticationToken authenticationToken) {

        accumulateState.append(System.getProperty("line.separator"));
        accumulateState.append(authenticationToken.toString());
        accumulateState.append(System.getProperty("line.separator"));
    }
}
