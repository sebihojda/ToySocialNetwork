package com.example.toysocialnetwork.observer;

import com.example.toysocialnetwork.utils.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
