package com.hmdp.utils;

import com.hmdp.entity.User;

public class UserHolder {
    private static final ThreadLocal<User> tl = new ThreadLocal<>();

    public static void saveUser(User user){
        tl.set(user);
    }

    public static com.hmdp.entity.User getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
