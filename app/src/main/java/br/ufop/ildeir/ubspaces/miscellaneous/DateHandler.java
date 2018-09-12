package br.ufop.ildeir.ubspaces.miscellaneous;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Ildeir on 12/09/2018.
 */

public class DateHandler {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    static SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String sqlDateToString(String sqlDate){
        try {
            return simpleDateFormat.format(sqlDateFormat.parse(sqlDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toSqlDate(Date date){
        return sqlDateFormat.format(date);
    }

    public static String toStringDate(Date date){
        return simpleDateFormat.format(date);
    }

    public static Date sqlToDate(String date){
        try {
            return sqlDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
