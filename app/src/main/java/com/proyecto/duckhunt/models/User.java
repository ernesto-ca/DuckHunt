package com.proyecto.duckhunt.models;

public class User {

    private String nick;
    private int Ducks;

    public User() {
    }

    public User(String nick, int ducks) {
        this.nick = nick;
        Ducks = ducks;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getDucks() {
        return Ducks;
    }

    public void setDucks(int ducks) {
        Ducks = ducks;
    }
}
