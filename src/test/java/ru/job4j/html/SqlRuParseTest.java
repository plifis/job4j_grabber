package ru.job4j.html;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class SqlRuParseTest {

    @Test
    public void convertDateFromString()  {
        Calendar calendar = new GregorianCalendar(2020, Calendar.DECEMBER, 22, 8, 22);
        String str = "22 дек 20, 8:22";
        SqlRuParse sqlRuParse = new SqlRuParse();
        Date actual = null;
        try {
            actual = sqlRuParse.convertDate(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(calendar.getTime(), actual);
    }
}