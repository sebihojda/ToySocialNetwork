package com.example.toysocialnetwork.service;

import com.example.toysocialnetwork.domain.Message;
import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.observer.Observable;
import com.example.toysocialnetwork.observer.Observer;
import com.example.toysocialnetwork.repository.MessageDBRepository;
import com.example.toysocialnetwork.utils.AdminEvent;
import com.example.toysocialnetwork.utils.ServiceType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public class MessageService implements Observable<AdminEvent> {

    private List<Observer<AdminEvent>> observers = new ArrayList<>();

    private final MessageDBRepository messageDBRepository;

    public MessageService(MessageDBRepository messageDBRepository) {
        this.messageDBRepository = messageDBRepository;
    }

    public void add(Long from, List<Long> to, String message, LocalDateTime sent_at, List<Long> reply){
        Message new_message = new Message(from, to, message, sent_at, reply);
        if(messageDBRepository.save(new_message).isPresent())
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        notifyObservers(new AdminEvent(ServiceType.Message));
    }

    public void remove(Long id){
        if(messageDBRepository.delete(id).isEmpty())
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        notifyObservers(new AdminEvent(ServiceType.Message));
    }

    public List<Message> sorted_messages(User currentUser, User currentFriend){
        List<Message> messages = StreamSupport.stream(messageDBRepository.findAll().spliterator(), false).toList();
        List<Message> selected_list = messages.stream().filter(message ->
                (message.getFrom() == currentUser.getId() && message.getTo().contains(currentFriend.getId())) ||
                        (message.getFrom() == currentFriend.getId() && message.getTo().contains(currentUser.getId()))
        ).toList();
        return selected_list.stream().sorted(Comparator.comparing(Message::getSent_at)).toList();
    }

    public List<String> chat(User currentUser, User currentFriend) {
        List<Message> selected_sorted_list = sorted_messages(currentUser, currentFriend);
        return selected_sorted_list.stream().map(x -> {
            if(x.getFrom() == currentUser.getId())
                return "( " + currentUser.getFirstName() + " " + currentUser.getLastName() + " ) - " + x.getMessage();
            else
                return "( " + currentFriend.getFirstName() + " " + currentFriend.getLastName() + " ) - " + x.getMessage();
        }).toList();
    }

    public Iterable<Message> getAll(){
        return messageDBRepository.findAll();
    }

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
