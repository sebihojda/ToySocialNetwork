package com.example.toysocialnetwork.domain.validators;

import com.example.toysocialnetwork.domain.Account;

import java.util.regex.Pattern;

public class AccountValidator implements Validator<Account> {
    @Override
    public void validate(Account entity) throws ValidationException {
        String errors = "";
        /*Only contains alphanumeric characters, underscore and dot.
        Underscore and dot can't be at the end or start of a username (e.g _username / username_ / .username / username.).
        Underscore and dot can't be next to each other (e.g. user_.name).
        Underscore or dot can't be used multiple times in a row (e.g. user__name / user..name).
        Number of characters must be between 8 to 20.*/
        if(!Pattern.compile("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$").matcher(entity.getUsername()).matches()) errors += "Invalid username!\n";
        //Minimum eight characters, at least one uppercase letter, one lowercase letter and one number:
        if(!Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$").matcher(entity.getPassword()).matches()) errors += "Invalid password!\n";
        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
