package com.example.toysocialnetwork.service;

import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.observer.Observable;
import com.example.toysocialnetwork.observer.Observer;
import com.example.toysocialnetwork.repository.FriendshipDBRepository;
import com.example.toysocialnetwork.repository.UserDBRepository;
import com.example.toysocialnetwork.repository.paging.Page;
import com.example.toysocialnetwork.repository.paging.Pageable;
import com.example.toysocialnetwork.utils.AdminEvent;
import com.example.toysocialnetwork.utils.ServiceType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class UserService implements Observable<AdminEvent>{

    private List<Observer<AdminEvent>> observers = new ArrayList<>();
    private final UserDBRepository userDBRepository;

    private final FriendshipDBRepository friendshipDBRepository;

    public UserService(UserDBRepository userDBRepository, FriendshipDBRepository friendshipDBRepository){
        this.userDBRepository = userDBRepository;
        this.friendshipDBRepository = friendshipDBRepository;
    }

    public void add(String firstName, String lastName, String email, String phoneNumber, LocalDateTime dateOfBirth, String gender, String country, String city){
        User newUser = new User(firstName, lastName, email, phoneNumber, dateOfBirth, gender, country, city);
        if(userDBRepository.save(newUser).isPresent())
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        notifyObservers(new AdminEvent(ServiceType.User));
    }

    public void remove(Long id){
        if(userDBRepository.delete(id).isEmpty())
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        notifyObservers(new AdminEvent(ServiceType.User));
    }

    public void update(Long user_id, String firstName, String lastName, String email, String phoneNumber,LocalDateTime dateOfBirth, String gender, String country, String city){
        User newUser = new User(firstName, lastName, email, phoneNumber, dateOfBirth, gender, country, city);
        newUser.setId(user_id);
        if(userDBRepository.update(newUser).isPresent())
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        notifyObservers(new AdminEvent(ServiceType.User));
    }

    public Iterable<User> getAll(){
        return userDBRepository.findAll();
    }

    public Page<User> usersOnPage(Pageable pageable){
        return userDBRepository.findAll(pageable);
    }

    public User findOne(Long Id) { return userDBRepository.findOne(Id).get(); }

    public Long findOne(String email, String phoneNumber) { return userDBRepository.findOne(email, phoneNumber).get(); }

    @Override
    public void addObserver(Observer<AdminEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<AdminEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(AdminEvent t) {
        observers.forEach(observer -> observer.update(t));
    }
}
