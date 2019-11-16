package com.cscie97.store.authentication;


public class Resource implements Visitable {

    private String resourceId;
    private String resourceName;

    public Resource(String resourceId, String resourceName) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
    }

    @Override
    public void accept(Ivisitor ivisitor) {
        ivisitor.visit(this);
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }
}
