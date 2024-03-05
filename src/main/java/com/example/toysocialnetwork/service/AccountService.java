package com.example.toysocialnetwork.service;

import com.example.toysocialnetwork.domain.Account;
import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.repository.AccountDBRepository;
import com.example.toysocialnetwork.utils.AdminEvent;
import com.example.toysocialnetwork.utils.ServiceType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class AccountService {

    private final AccountDBRepository accountDBRepository;

    public AccountService(AccountDBRepository accountDBRepository) {
        this.accountDBRepository = accountDBRepository;
    }

    public void add(Long id_user, String username, String password, String acc_state, LocalDateTime acc_date, LocalDateTime locked_date, Long locked_time){
        Account account = new Account(id_user, username, password, acc_date);
        account.setState(acc_state);
        account.setLocked_date(locked_date);
        account.setLocked_time(locked_time);
        if(accountDBRepository.save(account).isPresent())
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        //notifyObservers(new AdminEvent(ServiceType.Account));
    }

    public Iterable<Account> getAll(){
        return accountDBRepository.findAll();
    }

    public Account findOne(String username, String password) { return accountDBRepository.findOne(username, password).get(); }
}
