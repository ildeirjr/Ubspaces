package br.ufop.ildeir.ubspaces.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Ildeir on 12/07/2018.
 */

public class RecyclerViewItem {

    private String codigo;
    private String nome;
    private int dia;
    private int mes;
    private int ano;
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
            setDia(Integer.parseInt(jsonItem.getString("dia")));
            setMes(Integer.parseInt(jsonItem.getString("mes")));
            setAno(Integer.parseInt(jsonItem.getString("ano")));
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
            jsonItem.put("dia",getDia());
            jsonItem.put("mes",getMes());
            jsonItem.put("ano",getAno());
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

    public int compareItemDate(Item item){
        if(dia == item.getDia() && mes == item.getMes() && ano == item.getAno()){
            return 0;
        } else if(dia > item.getDia() && mes == item.getMes() && ano == item.getAno()){
            return 1;
        } else if(dia == item.getDia() && mes > item.getMes() && ano == item.getAno()){
            return 1;
        } else if(dia == item.getDia() && mes == item.getMes() && ano > item.getAno()){
            return 1;
        } else if(dia > item.getDia() && mes > item.getMes() && ano == item.getAno()){
            return 1;
        } else if(dia > item.getDia() && mes == item.getMes() && ano > item.getAno()){
            return 1;
        } else if(dia == item.getDia() && mes > item.getMes() && ano > item.getAno()){
            return 1;
        } else if(dia > item.getDia() && mes > item.getMes() && ano > item.getAno()){
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecyclerViewItem)) return false;

        RecyclerViewItem that = (RecyclerViewItem) o;

        if (getDia() != that.getDia()) return false;
        if (getMes() != that.getMes()) return false;
        if (getAno() != that.getAno()) return false;
        if (!getCodigo().equals(that.getCodigo())) return false;
        if (!getNome().equals(that.getNome())) return false;
        if (getFoto() != null ? !getFoto().equals(that.getFoto()) : that.getFoto() != null)
            return false;
        return Arrays.equals(getImg(), that.getImg());
    }

    @Override
    public int hashCode() {
        int result = getCodigo().hashCode();
        result = 31 * result + getNome().hashCode();
        result = 31 * result + getDia();
        result = 31 * result + getMes();
        result = 31 * result + getAno();
        result = 31 * result + (getFoto() != null ? getFoto().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getImg());
        return result;
    }
}
