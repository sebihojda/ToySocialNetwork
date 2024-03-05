package com.example.toysocialnetwork.domain.validators;

import com.example.toysocialnetwork.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = "";
        if(entity.getId().getLeft() == null) errors += "The Id of the first User is NULL!\n";
        if(entity.getId().getRight() == null) errors += "The Id of the second User is NULL!\n";
        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
