package br.ufop.ildeir.ubspaces.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Ildeir on 24/05/2018.
 */

public class Item {

    private String codigo;
    private String nome;
    private int dia;
    private int mes;
    private int ano;
    private String local;
    private String nota;
    private String recebeu;
    private String depto;
    private String descricao;
    private String foto;
    private byte[] img;

//    public Item(String codigo){
//        this.codigo = codigo;
//    }

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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getRecebeu() {
        return recebeu;
    }

    public void setRecebeu(String recebeu) {
        this.recebeu = recebeu;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
            setLocal(jsonItem.getString("local"));
            setNota(jsonItem.getString("nota"));
            setRecebeu(jsonItem.getString("quem_recebeu"));
            setDepto(jsonItem.getString("depto"));
            setDescricao(jsonItem.getString("descricao"));
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
            jsonItem.put("local",getLocal());
            jsonItem.put("nota",getNota());
            jsonItem.put("recebeu",getRecebeu());
            jsonItem.put("depto",getDepto());
            jsonItem.put("descricao",getDescricao());
            jsonItem.put("foto",getFoto());
            return jsonItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap createImgThumb(){
        Bitmap bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
        bitmap = Bitmap.createScaledBitmap(bitmap,(int) (bitmap.getWidth()*0.1),(int) (bitmap.getHeight()*0.1),true);
        return bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (dia != item.dia) return false;
        if (mes != item.mes) return false;
        if (ano != item.ano) return false;
        if (!codigo.equals(item.codigo)) return false;
        if (!nome.equals(item.nome)) return false;
        if (!local.equals(item.local)) return false;
        if (!nota.equals(item.nota)) return false;
        if (!recebeu.equals(item.recebeu)) return false;
        if (!depto.equals(item.depto)) return false;
        if (!descricao.equals(item.descricao)) return false;
        if (!foto.equals(item.foto)) return false;
        return Arrays.equals(img, item.img);
    }

    @Override
    public int hashCode() {
        int result = codigo.hashCode();
        result = 31 * result + nome.hashCode();
        result = 31 * result + dia;
        result = 31 * result + mes;
        result = 31 * result + ano;
        result = 31 * result + local.hashCode();
        result = 31 * result + nota.hashCode();
        result = 31 * result + recebeu.hashCode();
        result = 31 * result + depto.hashCode();
        result = 31 * result + descricao.hashCode();
        result = 31 * result + foto.hashCode();
        result = 31 * result + Arrays.hashCode(img);
        return result;
    }
}
