package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SqlRuParse implements Parse {
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
            } else if (strDate.contains("вчера")) {
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
            this.getTimeOfDay(strDate, calendar);
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

    /**
     * Получаем список всех постов по ссылке
     * @param link ссылка на раздел Вакансий
     * @return Список всех тем в данном разделе
     */
    @Override
    public List<Post> list(String link) {
        List<Post> list = new ArrayList<>();
        Post post;
        for (int i = 1; i <= 5; i++) {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    Element href = td.child(0);
                    post = this.detail(href.attr("href"));
                    list.add(post);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * Получаем детали поста по ссылке
     * @param link ссылка на пост
     * @return Объект класса Post
     */
    @Override
    public Post detail(String link) {
        Post post = null;
        try {
            post = this.getPost(link);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return post;
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

    /**
     * Получаем сведения (автор String name, String description текст, String strData дата поста) о посте по ссылке
     * @param url ссылка на пост
     * @return Объект класса Post полученный по входной ссылке
     * @throws IOException исключение ввода-вывода
     * @throws ParseException исключение при парсинге строки по предлагемому шаблону
     */
    public Post getPost(String url) throws IOException, ParseException {
        Document doc = Jsoup.connect(url).get();
        Elements descs = doc.select(".msgBody");
        String name = descs.get(0).child(0).text();
        String description = descs.get(1).text();
        Elements dates = doc.select(".msgFooter");
        Element date = dates.get(0);
        int endTimeSubStr = date.text().indexOf(":");
        String strDate = date.text()
                                .substring(0, endTimeSubStr + 3);
        return new Post(name, description, url, this.convertDate(strDate));
    }

    public static void main(String[] args) {
        SqlRuParse sqlRuParse = new SqlRuParse();
        List<Post> list = sqlRuParse.list("https://www.sql.ru/forum/job-offers/");
        System.out.println(list.toString() + System.lineSeparator());
//        for (int i = 1; i <= 5; i++) {
//            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
//            Elements row = doc.select(".postslisttopic");
//            for (Element td : row) {
//                Element href = td.child(0);
//                System.out.println(href.attr("href"));
//                System.out.println(href.text());
//            }
//            Elements els = doc.select("[style=text-align:center].altCol");
//            for (Element td : els) {
//                System.out.println(sqlRuParse.convertDate(td.text()));
//            }
//        }
//        Post post = sqlRuParse
//                .getPost("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
//        System.out.println(post);
    }
}