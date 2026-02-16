package com.ia.ia_base.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public static String hashPassword(String  password){
        if(password == null || password.isEmpty()){
            throw new IllegalArgumentException("Password must be entered");
        }
        return BCrypt.hashpw(password,BCrypt.gensalt());

    }
    public static boolean verifyPassword(String password, String hash){
        if(password == null || hash == null){
            return false;
        }
        try{
            return BCrypt.checkpw(password, hash);
        }catch(Exception e){
            return false;
        }

    }
}
