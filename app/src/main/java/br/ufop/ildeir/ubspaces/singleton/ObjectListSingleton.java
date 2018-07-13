package br.ufop.ildeir.ubspaces.singleton;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;

/**
 * Created by Ildeir on 24/05/2018.
 */

public class ObjectListSingleton {

    public static ObjectListSingleton singleton = null;

    private static ArrayList<RecyclerViewItem> objectList;

    public static ObjectListSingleton getInstance(){
        if(singleton == null){
            singleton = new ObjectListSingleton();
        }
        return singleton;
    }

    private ObjectListSingleton(){
        objectList = new ArrayList<>();
    }

    public ArrayList<RecyclerViewItem> getObjectList() {
        return objectList;
    }

    public void setObjectList(ArrayList<RecyclerViewItem> objectList) {
        ObjectListSingleton.objectList = objectList;
    }


}
