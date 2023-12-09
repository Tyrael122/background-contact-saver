package br.makesoftware.contacts_manager.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateUtil {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formateDate(LocalDateTime date) {
        return formateDate(date, "dd/MM/yyyy HH:mm:ss");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formateDate(LocalDateTime date, String pattern) {
        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter();

        return date.format(dtf);
    }
}
