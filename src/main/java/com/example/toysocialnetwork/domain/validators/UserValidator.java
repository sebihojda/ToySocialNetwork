package com.example.toysocialnetwork.domain.validators;

import com.example.toysocialnetwork.domain.User;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";
        if(entity.getFirstName().isEmpty()) errors += "First Name is empty!\n";
        if(entity.getLastName().isEmpty()) errors += "Last Name is empty!\n";
        //Java regular expression for validating email addresses according to RFC 5322
        if(!Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").matcher(entity.getEmail()).matches())
            errors += "Invalid email!\n";
        if(!Pattern.compile("^[0-9]*$").matcher(entity.getPhoneNumber()).matches())
            errors += "Invalid phone number!\n";
        if(entity.getGender().isEmpty()) errors += "Gender is empty!\n";
        if(entity.getCountry().isEmpty()) errors += "Country is empty!\n";
        if(entity.getCity().isEmpty()) errors += "City is empty!\n";
        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}

