module com.example.toysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.toysocialnetwork to javafx.fxml;
    exports com.example.toysocialnetwork;
    exports com.example.toysocialnetwork.controller;
    opens com.example.toysocialnetwork.controller to javafx.fxml;
    opens com.example.toysocialnetwork.domain;
    exports com.example.toysocialnetwork.domain;
    opens com.example.toysocialnetwork.dto;
    exports com.example.toysocialnetwork.dto;
    opens com.example.toysocialnetwork.repository;
    exports com.example.toysocialnetwork.repository;
    opens com.example.toysocialnetwork.repository.paging;
    exports com.example.toysocialnetwork.repository.paging;
    opens com.example.toysocialnetwork.observer;
    exports com.example.toysocialnetwork.observer;
    opens com.example.toysocialnetwork.utils;
    exports com.example.toysocialnetwork.utils;
    opens com.example.toysocialnetwork.service;
    exports com.example.toysocialnetwork.service;
}