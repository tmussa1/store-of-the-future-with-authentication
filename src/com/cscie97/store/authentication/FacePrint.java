package com.cscie97.store.authentication;

public class FacePrint implements Credential{

    private String facePrint;

    public FacePrint(String facePrint) {
        this.facePrint = facePrint;
    }

    @Override
    public Credential getCredential() {
        return this;
    }
}
