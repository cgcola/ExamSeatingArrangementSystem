package com.example.examseatingarangementfinal;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddStudentController implements Initializable {

    @FXML private TextField courseCodeField;
    @FXML private TextField courseNameField;
    @FXML private TextField examDateTimeField;
    @FXML private TextField studentIdField;
    @FXML private TextField studentNameField;
    @FXML private TextField sectionField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseCodeField.requestFocus();
    }





}
