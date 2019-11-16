package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationService implements IAuthenticationService, Visitable {

    private static IAuthenticationService instance;
    private List<User> users;
    private List<AuthenticationToken> tokens;

    public AuthenticationService() {
        this.users = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }

    public static IAuthenticationService getInstance(){
        if(instance == null){
            instance = new AuthenticationService();
        }
        return instance;
    }

    @Override
    public void accept(Ivisitor ivisitor) {
    }
}
