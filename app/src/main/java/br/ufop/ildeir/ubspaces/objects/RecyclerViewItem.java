package br.ufop.ildeir.ubspaces.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Ildeir on 12/07/2018.
 */

public class RecyclerViewItem {

    private String codigo;
    private String nome;

    @SerializedName("data_entrada")
    private String dataEntrada;

    private String foto;
    private byte[] img;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public void JSONtoItem(JSONObject jsonItem){
        try {
            setCodigo(jsonItem.getString("codigo"));
            setNome(jsonItem.getString("nome"));
            setDataEntrada(jsonItem.getString("data_entrada"));
            setFoto(jsonItem.getString("foto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject ItemtoJSON(){
        JSONObject jsonItem = new JSONObject();
        try {
            jsonItem.put("codigo",getCodigo());
            jsonItem.put("nome",getNome());
            jsonItem.put("data_entrada",getDataEntrada());
            jsonItem.put("foto",getFoto());
            return jsonItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap createImgBitmap(){
        Bitmap bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
        return bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecyclerViewItem)) return false;

        RecyclerViewItem that = (RecyclerViewItem) o;

        if (codigo != null ? !codigo.equals(that.codigo) : that.codigo != null) return false;
        if (nome != null ? !nome.equals(that.nome) : that.nome != null) return false;
        if (dataEntrada != null ? !dataEntrada.equals(that.dataEntrada) : that.dataEntrada != null)
            return false;
        if (foto != null ? !foto.equals(that.foto) : that.foto != null) return false;
        return Arrays.equals(img, that.img);
    }

    @Override
    public int hashCode() {
        int result = codigo != null ? codigo.hashCode() : 0;
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        result = 31 * result + (dataEntrada != null ? dataEntrada.hashCode() : 0);
        result = 31 * result + (foto != null ? foto.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(img);
        return result;
    }
}
