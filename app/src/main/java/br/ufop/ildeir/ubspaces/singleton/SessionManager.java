package br.ufop.ildeir.ubspaces.singleton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import br.ufop.ildeir.ubspaces.activities.LoginActivity;

/**
 * Created by Ildeir on 31/05/2018.
 */

public class SessionManager {

    public static SessionManager session;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UbspacesPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String STRING_TOKEN = "token";
    public static final String USER_ID = "id";

    public SessionManager(){

    }

    public static SessionManager getInstance(){
        if(session == null){
            session = new SessionManager();
        }
        return session;
    }

    public void setSession(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String token, String id){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(STRING_TOKEN,token);
        editor.putString(USER_ID,id);
        editor.commit();
    }

    public String getUserToken(){
        return pref.getString(STRING_TOKEN,null);
    }

    public String getUserId(){
        return pref.getString(USER_ID,null);
    }

    public void toLoginActivity(){
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN,false);
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public void setPref(SharedPreferences pref) {
        this.pref = pref;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
