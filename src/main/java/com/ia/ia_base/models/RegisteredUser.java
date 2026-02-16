package com.ia.ia_base.models;

public class RegisteredUser extends User {

    public RegisteredUser(){
        super();
    }

    public RegisteredUser(int id, String email, String passwordHash, Role role, boolean isBlocked, boolean mustChangePassword) {
        super(id, email, passwordHash, role, isBlocked, mustChangePassword);
    }




    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canManagerCategories() {
        return false;
    }

    public boolean isAdmin(){
        return false;
    }

}
