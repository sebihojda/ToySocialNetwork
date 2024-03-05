package com.example.toysocialnetwork.dto;

import com.example.toysocialnetwork.domain.Friendship;
import com.example.toysocialnetwork.domain.User;

public class FriendshipDTO extends Friendship {

    private User friend;

    private String friendName;

    public FriendshipDTO(User friend, String friendName){
        this.friend = friend;
        this.friendName = friendName;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

}
