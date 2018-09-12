package br.ufop.ildeir.ubspaces.singleton;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Ildeir on 17/05/2018.
 */

public class UserSingleton {

    public static UserSingleton singleton;

    private int id;
    private String nome;
    private String email;
    private String dataNasc;
    private String depto;
    private String authenticationToken;

    public static UserSingleton getInstance(){
        if(singleton == null){
            singleton = new UserSingleton();
        }
        return singleton;
    }

    private UserSingleton(){
        id = 0;
        nome = "";
        email = "";
        dataNasc = "";
        depto = "";
        authenticationToken = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(String dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public void setNull(){
        singleton = new UserSingleton();
    }

}
