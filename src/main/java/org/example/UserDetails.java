package org.example;

import java.util.Dictionary;
import java.io.Serializable;
public class UserDetails implements Serializable{
    public int userId;
    public String userName;
    public String password;
    public String userRole;
    public Dictionary<Integer,String> function;
    public int userStatus;
    public static String ulManager ="Management";
    public static String ulServiceTech ="Service Technician";
    public static String ulPUser ="Power User";
    public static String ulOUser ="Ordinary User";
}
