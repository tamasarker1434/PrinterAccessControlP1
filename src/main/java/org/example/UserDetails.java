package org.example;

import java.util.Dictionary;
import java.io.Serializable;
public class UserDetails implements Serializable{
    public int userId;
    public String userName;
    public String password;
    public Dictionary<Integer,String> function;
}
