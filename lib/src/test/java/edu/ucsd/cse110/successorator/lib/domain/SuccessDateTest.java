package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SuccessDateTest {
    private static String formatString = "MMM-d-yyyy";
    DateTimeFormatter f = DateTimeFormatter.ofPattern(formatString, Locale.US);
    @Test
    public void getCurrentDate() {
        assertEquals(LocalDate.now(),SuccessDate.getCurrentDate());
    }

    @Test
    public void getCurrentDateAsString() {
        assertEquals( LocalDate.now().format(f),SuccessDate.getCurrentDateAsString());
    }

    @Test
    public void getTmwsDateAsString() {
        assertEquals( LocalDate.now().plusDays(1).format(f),SuccessDate.getTmwsDateAsString());
    }

    @Test
    public void stringToDate() {
        assertEquals(SuccessDate.getCurrentDate(), SuccessDate.stringToDate(SuccessDate.getCurrentDateAsString()));
    }

    @Test
    public void dateToString() {
        assertEquals(SuccessDate.getCurrentDateAsString(), SuccessDate.dateToString(SuccessDate.getCurrentDate()));
    }

}