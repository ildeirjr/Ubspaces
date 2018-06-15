package br.ufop.ildeir.ubspaces.miscellaneous;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import br.ufop.ildeir.ubspaces.R;

/**
 * Created by Ildeir on 09/05/2018.
 */

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText etDate;
    private int dia, mes, ano;
    private boolean flagDateSeted;

    public DateDialog(View v){
        etDate = v.findViewById(R.id.editData);
        flagDateSeted = false;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,ano,mes,dia);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String data = i2+"/"+(i1+1)+"/"+i;
        etDate.setText(data);
        dia = i2;
        mes = i1+1;
        ano = i;
        setFlagDateSeted(true);
    }

    public EditText getEtDate() {
        return etDate;
    }

    public void setEtDate(EditText etDate) {
        this.etDate = etDate;
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

    public boolean isFlagDateSeted() {
        return flagDateSeted;
    }

    public void setFlagDateSeted(boolean flagDateSeted) {
        this.flagDateSeted = flagDateSeted;
    }
}
