package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SqlRuParse {
    /**
     * Конвертирование строки в объект класса Date
     * @param strDate дата в формате String
     * @return дата в формате Date
     * @throws ParseException исключение при парсинге строки по предлагемому шаблону
     */
        public Date convertDate(String strDate) throws ParseException {
            ShortRusMonth formatSymbols = new ShortRusMonth();
            Calendar calendar = new GregorianCalendar();
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yy, hh:mm", formatSymbols.dateFormatSymbols);
            if (strDate.contains("сегодня")) {
                date = this.getTimeToday(strDate, calendar);
            } else if(strDate.contains("вчера")) {
                date = this.getTimeYesterday(strDate, calendar);
            } else {
                date = sdf.parse(strDate);
            }
            return date;
        }
    /**
     * Получаем сегодняшнюю дату из строки со словом "сегодня"
     * в объект класса Date  с датой и временем суток
     * @param strDate строка со словом "сегодня" и временем суток
     * @param calendar объект Calendar с текущей датой и временем,
     *                 значения меньше минуты в расчет не берутся
     * @return дату в виде объекта класса Date,
     * содержащего текущую дату и время суток из входящей строки
     */
    private Date getTimeToday(String strDate, Calendar calendar) {
          return  this.getTimeOfDay(strDate, calendar).getTime();
        }
    /**
     * Получаем вчерашнюю дату из строки со словом "вчера"
     * в объект класса Date с датой и временем суток
     * @param strDate строка со словом "вчера" и временем суток
     * @param calendar объект Calendar с текущей датой и временем,
     *                 значения меньше минуты в расчет не берутся
     * @return дату в виде объекта класса Date,
     * содержащего вчерашнюю дату и время суток из входящей строки
     */
        private Date getTimeYesterday(String strDate, Calendar calendar) {
            calendar = this.getTimeOfDay(strDate, calendar);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return calendar.getTime();
        }

    /**
     * Получаем время суток из входящей строки
     * @param strDate строка с датой и временем суток("вчера", "сегодня")
     * @param calendar объект Calendar с текущей датой и временем,
     *                 значения меньше минуты в расчет не берутся
     * @return объект Calendar с текущей датой и временем суток из входящей строки
     */
    private Calendar getTimeOfDay(String strDate, Calendar calendar) {
            int hour = Integer.parseInt(
                    strDate.substring(
                            strDate.lastIndexOf(" ") + 1,
                            strDate.lastIndexOf(" ") + 3));
            int minutes = Integer.parseInt(
                    strDate.substring(
                            strDate.lastIndexOf(":") + 1));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            return calendar;
        }

        static class ShortRusMonth {
        private final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols() {
            @Override
            public String[] getMonths() {
                return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                        "июл", "авг", "сен", "окт", "ноя", "дек"};
            }
        };
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse();
        for (int i = 1; i <= 3; i++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
            }
            Elements els = doc.select("[style=text-align:center].altCol");
            for (Element td : els) {
                System.out.println(sqlRuParse.convertDate(td.text()));
            }
        }
    }
}