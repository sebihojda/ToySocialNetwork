package com.example.toysocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {

    private Long from;
    private List<Long> to;
    private String message;
    private LocalDateTime sent_at;
    private List<Long> reply = null;

    public Message(Long from, List<Long> to, String message, LocalDateTime date, List<Long> reply){
        this.from = from;
        this.to = to;
        this.message = message;
        this.sent_at = date;
        this.reply = reply;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSent_at() {
        return sent_at;
    }

    public void setSent_at(LocalDateTime sent_at) {
        this.sent_at = sent_at;
    }

    public List<Long> getReply() {
        return reply;
    }

    public void setReply(List<Long> reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", sent_at=" + sent_at +
                ", reply=" + reply +
                '}';
    }
}
