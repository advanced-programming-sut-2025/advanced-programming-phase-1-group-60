package controller;

import models.Question;
import models.Result;

public class LoginMenuController {
    //Login
    // TODO : Validate the info and let the user log into his account
    public Result Login (String username, String password, boolean stayLoggedIn) {
        return null;
    }

    //Password
    // TODO : Validate the info and let the user change his password
    public String forgetPassword (String username , String Question) { return null; }

    //Security question
    // TODO : check if the security question is correct
    public Result processAnswerQuestion (Question question, String answer) {
        return null;
    }
}
