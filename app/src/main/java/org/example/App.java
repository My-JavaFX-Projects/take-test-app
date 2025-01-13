package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class App extends Application {

    private Stage mainStage;
    private List<QuestionPOJO> allQuestions;
    private int questionQueue = 0;
    private String givenAnswer;

    private final Map<Integer, String> answers = new HashMap<>();


    @Override
    public void start(Stage mainStage) throws Exception {
        try {
            DataBaseConnection connection = DataBaseConnection.getInstance();
            allQuestions = connection.getQuestion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.mainStage = mainStage;
        mainStage.setTitle("Select One");
        mainStage.setScene(createMainScene());
        mainStage.show();
    }

    protected Pane getTextPane() {
        Pane paneForText = new Pane();
        Text text = new Text(50, 50, allQuestions.get(questionQueue).getQuestionDesc());
        paneForText.getChildren().add(text);
        return paneForText;
    }

    protected VBox getAnswers() {
        VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(20, 20, 20, 20));
        ToggleGroup toggleButton = new ToggleGroup();
        RadioButton answer1 = new RadioButton("A) "+allQuestions.get(questionQueue).getAnswerA());
        RadioButton answer2 = new RadioButton("B) "+allQuestions.get(questionQueue).getAnswerB());
        RadioButton answer3 = new RadioButton("C) "+allQuestions.get(questionQueue).getAnswerC());
        RadioButton answer4 = new RadioButton("D) "+allQuestions.get(questionQueue).getAnswerD());
        answer1.setOnAction(e->this.givenAnswer="A");
        answer2.setOnAction(e->this.givenAnswer="B");
        answer3.setOnAction(e->this.givenAnswer="C");
        answer4.setOnAction(e->this.givenAnswer="D");
        answer1.setToggleGroup(toggleButton);
        answer2.setToggleGroup(toggleButton);
        answer3.setToggleGroup(toggleButton);
        answer4.setToggleGroup(toggleButton);
        vBox.getChildren().addAll(answer1, answer2, answer3, answer4);
        return vBox;
    }

    public void showStartTestScene() {
        mainStage.setTitle("Start Test");
        mainStage.setScene(startTestScene());
    }

    public void showAddQuestionScene() {
        mainStage.setTitle("Add question");
        mainStage.setScene(addQuestionScene());
    }

    public void backToMainScene() {
        mainStage.setTitle("Select One");
        mainStage.setScene(createMainScene());
    }
    public void showResultsScene() {
        mainStage.setTitle("Results");
        mainStage.setScene(resultScene());
    }

    public Scene createMainScene() {
        Pane mainPane = new Pane();
        Button addQuestionButton = new Button("Add question");
        addQuestionButton.setOnAction(e -> showAddQuestionScene());
        Button startTestButton = new Button("Start test");
        startTestButton.setOnAction(e -> showStartTestScene());
        HBox mainPageButtons = new HBox(addQuestionButton, startTestButton);
        mainPageButtons.setAlignment(Pos.CENTER);
        mainPane.getChildren().add(mainPageButtons);
        return new Scene(mainPane, 600, 300);
    }

    public Scene startTestScene() {
        BorderPane pane = new BorderPane();
        pane.setTop(getTextPane());
        pane.setCenter(getAnswers());
        HBox paneForButtons = new HBox(20);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            answers.put(questionQueue+1, givenAnswer);
            questionQueue++;
            if (questionQueue>= allQuestions.size()) showResultsScene();
            else showStartTestScene();
        });
        Button backHomeButton = new Button("Back Home");
        backHomeButton.setOnAction(e -> backToMainScene());
        paneForButtons.getChildren().addAll(submitButton, backHomeButton);
        paneForButtons.setAlignment(Pos.CENTER);
        paneForButtons.setPadding(new Insets(20, 20, 20, 20));
        pane.setBottom(paneForButtons);
        return new Scene(pane, 600, 300);
    }

    public Scene addQuestionScene() {
        BorderPane pane = new BorderPane();
        TextField questionField = new TextField();
        ToggleGroup group = new ToggleGroup();
        RadioButton answer1 = new RadioButton("A");
        RadioButton answer2 = new RadioButton("B");
        RadioButton answer3 = new RadioButton("C");
        RadioButton answer4 = new RadioButton("D");
        TextField answer1Text = new TextField();
        TextField answer2Text = new TextField();
        TextField answer3Text = new TextField();
        TextField answer4Text = new TextField();
        HBox answer1HBox = new HBox(10, answer1, answer1Text);
        HBox answer2HBox = new HBox(10, answer2, answer2Text);
        HBox answer3HBox = new HBox(10, answer3, answer3Text);
        HBox answer4HBox = new HBox(10, answer4, answer4Text);
        answer1.setToggleGroup(group);
        answer2.setToggleGroup(group);
        answer3.setToggleGroup(group);
        answer4.setToggleGroup(group);
        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(questionField, answer1HBox, answer2HBox, answer3HBox, answer4HBox);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        pane.setCenter(vBox);
        Button getHomeButton = new Button("Back Home");

        getHomeButton.setOnAction(e -> backToMainScene());
        Button submitButton = new Button("Submit");
        AtomicReference<String> answer = new AtomicReference<>("");
        answer1.setOnAction(e -> answer.set("A"));
        answer2.setOnAction(e -> answer.set("B"));
        answer3.setOnAction(e -> answer.set("C"));
        answer4.setOnAction(e -> answer.set("D"));
        submitButton.setOnAction(e -> {
            try {
                DataBaseConnection connection = DataBaseConnection.getInstance();
                connection.addQuestion(
                        questionField.getText(),
                        answer1Text.getText(),
                        answer2Text.getText(),
                        answer3Text.getText(),
                        answer4Text.getText(),
                        answer.get()
                );
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        HBox hBox = new HBox(20, getHomeButton, submitButton);
        hBox.setPadding(new Insets(20, 20, 20, 20));
        hBox.setAlignment(Pos.CENTER);
        pane.setBottom(hBox);
        return new Scene(pane, 600, 300);
    }

    public Scene resultScene() {
        Map<Integer, String> answerSheet;
        try {
            DataBaseConnection connection = DataBaseConnection.getInstance();
            answerSheet = connection.getAnswers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20,20,20,20));
        AtomicInteger counter = new AtomicInteger();
        answers.forEach((k,v)->{
            if(answerSheet.get(k).equals(v)) counter.getAndIncrement();
        });
        Text textField = new Text(answers + "\n Correct answers are : " + counter);
        borderPane.setCenter(textField);
        return new Scene(borderPane, 600, 300);
    }
}
