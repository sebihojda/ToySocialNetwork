package com.example.toysocialnetwork.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    Long idFirstUser;
    Long idSecondUser;
    LocalDateTime friendsFrom;
    String State = "pending";

    public Friendship(){
    }

    public Friendship(Long idFirstUser, Long idSecondUser, LocalDateTime friendsFrom) {
        this.idFirstUser = idFirstUser;
        this.idSecondUser = idSecondUser;
        this.friendsFrom = friendsFrom;
    }
    public Long getIdFirstUser(){ return this.idFirstUser; }
    public Long getIdSecondUser(){ return this.idSecondUser; }

    public void setIdFirstUser(Long idFirstUser) {
        this.idFirstUser = idFirstUser;
    }

    public void setIdSecondUser(Long idSecondUser) {
        this.idSecondUser = idSecondUser;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) { this.friendsFrom = friendsFrom; }

    public String getState(){
        return State;
    }

    public void setState(String State){
        this.State = State;
    }

}
