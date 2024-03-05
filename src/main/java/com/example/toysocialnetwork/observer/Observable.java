package com.example.toysocialnetwork.observer;

import com.example.toysocialnetwork.utils.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
