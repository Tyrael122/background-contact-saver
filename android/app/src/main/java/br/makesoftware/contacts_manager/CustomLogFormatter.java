package br.makesoftware.contacts_manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class CustomLogFormatter extends SimpleFormatter {
    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        Date timestamp = new Date(record.getMillis());
        SimpleDateFormat dataFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        sb.append(dataFormatter.format(timestamp)).append(" - ");
        sb.append(record.getMessage());
        sb.append("\n");

        return sb.toString();
    }
}
