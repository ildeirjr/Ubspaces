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
    private int dia, mes, ano;
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
        dia = 0;
        mes = 0;
        ano = 0;
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

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
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
