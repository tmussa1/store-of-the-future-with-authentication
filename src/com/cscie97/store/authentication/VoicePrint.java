package com.cscie97.store.authentication;

public class VoicePrint implements Credential {

    private String voicePrint;

    public VoicePrint(String voicePrint) {
        this.voicePrint = voicePrint;
    }

    @Override
    public Credential getCredential() {
        return this;
    }
}
