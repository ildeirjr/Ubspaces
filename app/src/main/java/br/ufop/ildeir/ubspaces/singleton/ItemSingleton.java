package br.ufop.ildeir.ubspaces.singleton;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.objects.Item;

/**
 * Created by Ildeir on 25/05/2018.
 */

public class ItemSingleton {

    public static ItemSingleton singleton = null;

    private static Item itemSingleton;

    public static ItemSingleton getInstance(){
        if(singleton == null){
            singleton = new ItemSingleton();
        }
        return singleton;
    }

    private ItemSingleton(){
        itemSingleton = new Item();
    }

    public Item getItemSingleton() {
        return itemSingleton;
    }

    public void setItemSingleton(Item itemSingleton) {
        ItemSingleton.itemSingleton = itemSingleton;
    }

    public void setItemSingletonNull(){
        itemSingleton = null;
    }

}
