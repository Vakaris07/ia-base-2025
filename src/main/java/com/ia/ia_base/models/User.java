package com.ia.ia_base.models;

public abstract class User {
    private int id;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean isBlocked;
    private boolean mustChangePassword;

    public User() {

    }

    public User(int id, String email, String passwordHash, Role role, boolean isBlocked, boolean mustChangePassword) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isBlocked = isBlocked;
        this.mustChangePassword = mustChangePassword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public abstract boolean canManageUsers();

    public abstract boolean canManagerCategories();

    public abstract boolean isAdmin();



    @Override
    public String toString() {
        return email + " (" + (role != null ? role.getName() : "No role") + ")";
    }
}

