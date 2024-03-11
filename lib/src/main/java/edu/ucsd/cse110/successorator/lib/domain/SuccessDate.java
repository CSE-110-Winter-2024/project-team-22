package edu.ucsd.cse110.successorator.lib.domain;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class SuccessDate {
    private static String formatString = "MMM-d-yyyy";
    static public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    static public LocalDate stringToDate(String s) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(getFormatString(), Locale.US);
        return LocalDate.parse(s, f);
    }

    static public String dateToString(LocalDate d) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(getFormatString(), Locale.US);
        return d.format(f);
    }

     static public LocalDate dateToLocalDate(Date date) {
         return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
     }

    static public String getFormatString() {
        return formatString;
    }

}
