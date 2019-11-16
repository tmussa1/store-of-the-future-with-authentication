package com.cscie97.store.authentication;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;

public class AuthenticationToken {

    private String tokenId;
    private long timeToLive;
    private long expirationTime;
    private State expirationState;
    private User user;

    /**
     * One auth token is associated with just one user
     * @param tokenId
     * @param timeToLive
     * @param expirationState
     * @param user
     */
    public AuthenticationToken(String tokenId, long timeToLive, State expirationState, User user) {
        this.tokenId = tokenId;
        this.timeToLive = timeToLive;
        this.expirationTime = LocalDateTime.now().getNano() + timeToLive;
        this.expirationState = State.ACTIVE;
        this.user = user;
    }

    /**
     * If current time exceeds time to live, token expires
     * @return state of token
     */
    public State expireToken(){
        if( LocalDateTime.now().getNano() > expirationTime){
            this.expirationState = State.EXPIRED;
        }
        return expirationState;
    }

    public String getTokenId() {
        return tokenId;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public State getExpirationState() {
        return expirationState;
    }

    public User getUser() {
        return user;
    }
}
