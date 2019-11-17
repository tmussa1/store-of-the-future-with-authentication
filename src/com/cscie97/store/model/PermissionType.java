package com.cscie97.store.model;

public enum PermissionType {
    CREATE("CREATE"), READ("READ"), UPADTE("UPDATE"), DELETE("DELETE");

    private String permission;

    PermissionType(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
