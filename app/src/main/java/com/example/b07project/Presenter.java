package com.example.b07project;

import android.view.Display;

public class Presenter {
    private Model model;
    private DisplayLogin view;

    public Presenter(Model model, DisplayLogin view){
        this.model = model;
        this.view = view;
    }

    public void checkPassword(){
        String username = view.getUsername();
        String password = view.getPassword();
        if(username.equals("")){
            view.displayMessage("Username is Empty");
        }
        else if(model.authenticateUser(username, password)){
            view.hideMessage();
            view.logIn();
        }
        else{
            view.displayMessage("Incorrect password");
        }
    }

    public boolean createUser(User u){
        if(!model.createUser(u)){
            view.displayMessage("Username taken!");
            return false;
        }
        view.hideMessage();
        view.logIn();
        return true;
    }
}
