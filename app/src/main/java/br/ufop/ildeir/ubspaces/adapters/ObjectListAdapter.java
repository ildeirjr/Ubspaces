package br.ufop.ildeir.ubspaces.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.objects.Item;

/**
 * Created by Ildeir on 24/05/2018.
 */

public class ObjectListAdapter extends BaseAdapter{

    private ArrayList<Item> objects;
    private Context context;

    public ObjectListAdapter(ArrayList<Item> objects, Context context) {
        this.objects = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Item getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Item item = objects.get(i);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.object_list_adapter,null);

        TextView objectName = v.findViewById(R.id.objectName);
        objectName.setText(item.getNome());

        TextView objectCode = v.findViewById(R.id.objectCode);
        objectCode.setText("Código: " + item.getCodigo());

        ImageView objectImg = v.findViewById(R.id.objectImg);
        if(!item.getFoto().equals("null.jpg")){
            objectImg.setImageBitmap(item.createImgThumb());
        }


        return v;
    }
}
