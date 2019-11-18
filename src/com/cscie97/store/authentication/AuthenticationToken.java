package com.cscie97.store.authentication;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthenticationToken implements Visitable{

    private String tokenId;
    private long timeToLive;
    private long expirationTime;
    private State expirationState;
    private User user;

    /**
     *
     * @param user
     */
    public AuthenticationToken(User user) {
        this.tokenId = UUID.randomUUID().toString();
        this.timeToLive = 15L;
        this.expirationTime = (LocalDateTime.now().getHour() * LocalDateTime.now().getMinute()) + timeToLive;
        this.expirationState = State.ACTIVE;
        this.user = user;
    }

    /**
     * If current time exceeds time to live, token expires
     * @return state of token
     */
    public State expireToken(){
        if( (LocalDateTime.now().getHour() * LocalDateTime.now().getMinute()) > expirationTime){
            this.expirationState = State.EXPIRED;
        }
        return expirationState;
    }

    public State logOut(){
        this.expirationState = State.EXPIRED;
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

    @Override
    public void accept(Ivisitor ivisitor) {
        ivisitor.visit(this);
    }

    @Override
    public String toString() {
        return "AuthenticationToken with token id " + tokenId +
                " with time to live " + timeToLive +
                " with expiration time " + expirationTime +
                " with expiration state " + expirationState +
                " associate with user " + user.getUserId();
    }
}
