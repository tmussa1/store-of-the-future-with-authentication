package com.cscie97.store.authentication;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class User implements Visitable {

    private String userId;
    private List<Credential> credentials;
    private List<Entitlement> entitlements;

    public User(String userId) {
        this.userId = userId;
        this.credentials = new ArrayList<>();
        this.entitlements = new ArrayList<>();
    }

    public User() {
        this.credentials = new ArrayList<>();
        this.entitlements = new ArrayList<>();
    }

    public void addCredentials(Credential credential){
        this.credentials.add(credential);
    }

    public void addEntitlements(Entitlement entitlement){
        this.entitlements.add(entitlement);
    }

    public boolean checkCredentials(String userName, String passWord) throws NoSuchAlgorithmException {
        UserNamePassword userNamePassword = (UserNamePassword) credentials.stream()
                .filter(credential -> credential instanceof UserNamePassword)
                .collect(Collectors.toList()).get(0);
        return userName.equals(userNamePassword.getUserName()) &&
                computePasswordHash(passWord).equals(userNamePassword.getPasswordHash());
    }

    public boolean checkCredentials(String voiceFacePrint){
        VoicePrint voicePrint = (VoicePrint) credentials.stream()
                .filter(credential -> credential instanceof VoicePrint)
                .collect(Collectors.toList()).get(0);
        FacePrint facePrint = (FacePrint) credentials.stream()
                .filter(credential -> credential instanceof FacePrint)
                .collect(Collectors.toList()).get(0);
        return voiceFacePrint.equals(voicePrint.getVoicePrint()) ||
                voiceFacePrint.equals(facePrint.getFacePrint());
    }


    public String computePasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashVal = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashVal);
    }

    @Override
    public void accept(Ivisitor ivisitor) {
        ivisitor.visit(this);
    }

    public List<Entitlement> getEntitlements() {
        return entitlements;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "User with user id " + userId;
    }
}
