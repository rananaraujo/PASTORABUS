package com.example.pastorabus.model;

public class User {
    public long id_user;
    public String login;
    public String senha;

    public User(long id_user, String login, String senha) {
        this.id_user = id_user;
        this.login = login;
        this.senha = senha;
    }

    public long getId_user() {
        return id_user;
    }

    public String getSenha() {
        return senha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    public void setId_user(long id_user) {
        this.id_user = id_user;
    }


}
