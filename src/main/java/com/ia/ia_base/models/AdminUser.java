package com.ia.ia_base.models;

public class AdminUser extends User{

    public AdminUser() { super();}

    public AdminUser(int id, String email, String passwordHash, Role role, boolean isBlocked, boolean mustChangePassword) {
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

    @Override
    public boolean isAdmin() {
        return false;
    }


}
