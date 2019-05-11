package br.ufop.ildeir.ubspaces.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Ildeir on 24/05/2018.
 */

public class Item {

    private String codigo;
    private String nome;
    private String estado;

    @SerializedName("data_entrada")
    private String dataEntrada;
    private String bloco;
    private String nota;

    @SerializedName("quem_recebeu")
    private String recebeu;
    private String sala;
    private String descricao;
    private String unidade;
    private String foto;
    private String empenho;
    private String conservacao;

    @SerializedName("op_exclusao_id")
    private String nomeUsrExclusao;

    @SerializedName("tempo_exclusao")
    private String dataExclusao;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
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

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getEmpenho() {
        return empenho;
    }

    public void setEmpenho(String empenho) {
        this.empenho = empenho;
    }

    public String getConservacao() {
        return conservacao;
    }

    public void setConservacao(String conservacao) {
        this.conservacao = conservacao;
    }

    public String getNomeUsrExclusao() {
        return nomeUsrExclusao;
    }

    public void setNomeUsrExclusao(String nomeUsrExclusao) {
        this.nomeUsrExclusao = nomeUsrExclusao;
    }

    public String getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(String dataExclusao) {
        this.dataExclusao = dataExclusao;
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
            setEstado(jsonItem.getString("estado"));
            setDataEntrada(jsonItem.getString("data_entrada"));
            setBloco(jsonItem.getString("bloco"));
            setNota(jsonItem.getString("nota"));
            setRecebeu(jsonItem.getString("quem_recebeu"));
            setSala(jsonItem.getString("sala"));
            setDescricao(jsonItem.getString("descricao"));
            setUnidade(jsonItem.getString("unidade"));
            setFoto(jsonItem.getString("foto"));
            setEmpenho(jsonItem.getString("empenho"));
            setConservacao(jsonItem.getString("conservacao"));
            setNomeUsrExclusao(jsonItem.getString("op_exclusao_id"));
            setDataExclusao(jsonItem.getString("tempo_exclusao"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject ItemtoJSON(){
        JSONObject jsonItem = new JSONObject();
        try {
            jsonItem.put("codigo",getCodigo());
            jsonItem.put("nome",getNome());
            jsonItem.put("estado",getEstado());
            jsonItem.put("data_entrada",getDataEntrada());
            jsonItem.put("bloco", getBloco());
            jsonItem.put("nota",getNota());
            jsonItem.put("recebeu",getRecebeu());
            jsonItem.put("sala", getSala());
            jsonItem.put("descricao",getDescricao());
            jsonItem.put("unidade",getUnidade());
            jsonItem.put("foto",getFoto());
            jsonItem.put("empenho",getEmpenho());
            jsonItem.put("conservacao",getConservacao());
            jsonItem.put("op_exclusao_id",getNomeUsrExclusao());
            jsonItem.put("tempo_exclusao",getDataExclusao());
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
        if (!(o instanceof Item)) return false;

        Item item = (Item) o;

        if (codigo != null ? !codigo.equals(item.codigo) : item.codigo != null) return false;
        if (nome != null ? !nome.equals(item.nome) : item.nome != null) return false;
        if (estado != null ? !estado.equals(item.estado) : item.estado != null) return false;
        if (dataEntrada != null ? !dataEntrada.equals(item.dataEntrada) : item.dataEntrada != null)
            return false;
        if (bloco != null ? !bloco.equals(item.bloco) : item.bloco != null) return false;
        if (nota != null ? !nota.equals(item.nota) : item.nota != null) return false;
        if (recebeu != null ? !recebeu.equals(item.recebeu) : item.recebeu != null) return false;
        if (sala != null ? !sala.equals(item.sala) : item.sala != null) return false;
        if (descricao != null ? !descricao.equals(item.descricao) : item.descricao != null)
            return false;
        if (unidade != null ? !unidade.equals(item.unidade) : item.unidade != null) return false;
        if (foto != null ? !foto.equals(item.foto) : item.foto != null) return false;
        if (empenho != null ? !empenho.equals(item.empenho) : item.empenho != null) return false;
        if (conservacao != null ? !conservacao.equals(item.conservacao) : item.conservacao != null)
            return false;
        if (nomeUsrExclusao != null ? !nomeUsrExclusao.equals(item.nomeUsrExclusao) : item.nomeUsrExclusao != null)
            return false;
        if (dataExclusao != null ? !dataExclusao.equals(item.dataExclusao) : item.dataExclusao != null)
            return false;
        return Arrays.equals(img, item.img);
    }

    @Override
    public int hashCode() {
        int result = codigo != null ? codigo.hashCode() : 0;
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        result = 31 * result + (estado != null ? estado.hashCode() : 0);
        result = 31 * result + (dataEntrada != null ? dataEntrada.hashCode() : 0);
        result = 31 * result + (bloco != null ? bloco.hashCode() : 0);
        result = 31 * result + (nota != null ? nota.hashCode() : 0);
        result = 31 * result + (recebeu != null ? recebeu.hashCode() : 0);
        result = 31 * result + (sala != null ? sala.hashCode() : 0);
        result = 31 * result + (descricao != null ? descricao.hashCode() : 0);
        result = 31 * result + (unidade != null ? unidade.hashCode() : 0);
        result = 31 * result + (foto != null ? foto.hashCode() : 0);
        result = 31 * result + (empenho != null ? empenho.hashCode() : 0);
        result = 31 * result + (conservacao != null ? conservacao.hashCode() : 0);
        result = 31 * result + (nomeUsrExclusao != null ? nomeUsrExclusao.hashCode() : 0);
        result = 31 * result + (dataExclusao != null ? dataExclusao.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(img);
        return result;
    }
}
