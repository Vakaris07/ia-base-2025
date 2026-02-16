package com.ia.ia_base.util;

import com.ia.ia_base.models.AdminUser;
import com.ia.ia_base.models.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    public SessionManager() {}

    public static synchronized SessionManager getInstance(){
        if(instance == null){
            instance = new SessionManager();
        }
        return instance;
    }
    public User getCurrentUser(){
        return currentUser;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public void logout(){
        this.currentUser = null;
    }

    public boolean isLoggedIn(){
       return currentUser!=null;
    }
    public boolean isAdmin(){
        return currentUser != null && currentUser instanceof AdminUser;
    }
}
