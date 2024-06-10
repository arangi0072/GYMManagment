package com.arpit.gymmanagment.Model;

public class Login {
        private String userName;
        private String Password;
        private String GYMName;
        private String Question;
        private String Answer;

    public Login() {
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public Login(String userName, String password, String GYMName, String Question, String Answer) {
        this.userName = userName;
        this.Password = password;
        this.GYMName = GYMName;
        this.Question = Question;
        this.Answer = Answer;
    }

    public String getGYMName() {
        return GYMName;
    }

    public void setGYMName(String GYMName) {
        this.GYMName = GYMName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }
}
