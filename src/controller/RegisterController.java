package controller;

import models.Result;

public class RegisterController {
    //Registration
    // TODO : Register the User while also checking the info and saving it to the databse
    public Result processResister(String username, String password, String passwordConfirm, String nickname
    , String email, String gender) {
        return null;
    }
    // TODO : hash the password
    private String hashPassword(String password) {
        return null;
    }
    // TODO : let the user pick a security question
    public Result pickQuestion(int questionNumber, String answer, String answerConfirm) {
        return null;
    }
}
