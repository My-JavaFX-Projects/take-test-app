package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseConnection {

    private static DataBaseConnection instance;

    private Connection connection;

    private DataBaseConnection() throws SQLException {
        try {

            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/test-app",
                    "postgres",
                    "123456"
            );
            Statement statement = connection.createStatement();

            statement.executeQuery("""
                    CREATE TABLE  IF NOT EXISTS questions (
                      question_id SERIAL NOT NULL PRIMARY KEY,
                      description VARCHAR(255),
                      A VARCHAR(500),
                      B VARCHAR(500),
                      C VARCHAR(500),
                      D VARCHAR(500)
                    );
                    CREATE TABLE IF NOT EXISTS answers (
                      answer_id SERIAL NOT NULL PRIMARY KEY,
                      question_id INT NOT NULL,
                      answer VARCHAR(255),
                      FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
                    );
                    """);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DataBaseConnection getInstance() throws Exception {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DataBaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void addQuestion(String description,String answer1,String answer2,String answer3, String answer4, String answer) throws SQLException {
        System.out.println("Preparing to add question: " + description);
        PreparedStatement insertAQuestion = instance.getConnection().prepareStatement(
                "INSERT INTO questions(description,A,B,D,C) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        );
        insertAQuestion.setString(1, description);
        insertAQuestion.setString(2, answer1);
        insertAQuestion.setString(3, answer2);
        insertAQuestion.setString(4, answer3);
        insertAQuestion.setString(5, answer4);
        insertAQuestion.executeUpdate();
        ResultSet generatedKeys = insertAQuestion.getGeneratedKeys();
        int generatedId = 0;
        if (generatedKeys.next()) {
            generatedId = generatedKeys.getInt(1);
            System.out.println("Inserted question ID: " + generatedId);
        }
        PreparedStatement insertAnAnswer = instance.getConnection().prepareStatement(
                "INSERT INTO answers(question_id,answer) VALUES(?,?)",
                Statement.RETURN_GENERATED_KEYS
        );
        insertAnAnswer.setInt(1,generatedId);
        insertAnAnswer.setString(2, answer);
        insertAnAnswer.executeUpdate();
    }
    public List<QuestionPOJO> getQuestion() throws SQLException {
        List<QuestionPOJO> allQuestions = new ArrayList<>();
        ResultSet questions = instance.getConnection().createStatement().executeQuery(
                """
                        SELECT * FROM questions;
                    """
        );
        while (questions.next()) {
            QuestionPOJO questionPOJO = new QuestionPOJO();
            questionPOJO.setQuestionDesc(questions.getString("description"));
            questionPOJO.setAnswerA(questions.getString("a"));
            questionPOJO.setAnswerB(questions.getString("b"));
            questionPOJO.setAnswerC(questions.getString("c"));
            questionPOJO.setAnswerD(questions.getString("d"));
            allQuestions.add(questionPOJO);
        }
        return allQuestions;
    }

    public Map<Integer, String> getAnswers() throws SQLException {
        Map<Integer, String> answerSheet = new HashMap<>();
        ResultSet data = instance.getConnection().createStatement().executeQuery(
                "SELECT * FROM answers;"
        );
        while (data.next()) {
            answerSheet.put(data.getInt("question_id"), data.getString("answer"));
        }
        System.out.println(answerSheet);

        return answerSheet;
    }
}
