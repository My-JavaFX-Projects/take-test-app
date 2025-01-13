package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class MainPage {
    public Scene createMainScene() {
        Pane mainPane = new Pane();
        Button addQuestionButton = new Button("Add question");
        addQuestionButton.setOnAction(e-> System.out.println("main page show"));
        Button startTestButton = new Button("Start test");
        HBox mainPageButtons = new HBox(addQuestionButton, startTestButton);
        mainPageButtons.setAlignment(Pos.CENTER);
        mainPane.getChildren().add(mainPageButtons);
        return new Scene(mainPane, 600, 300);
    }
}
