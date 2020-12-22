package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.lang.model.util.ElementScanner6;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Locale;
import java.util.Properties;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        Rabbit rabbit = new Rabbit();
        rabbit.loadProperties();
        try (Connection connection = DriverManager.getConnection(
                rabbit.getExternalProperties("jdbc.url"),
                rabbit.getExternalProperties("jdbc.username"),
                rabbit.getExternalProperties("jdbc.password")
        )) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap map = new JobDataMap(); // карта для сохранения инфомрации о подключении
            map.put("connection", connection); // добавляем в объект Карты ключ и данные для соединения с БД
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(map)
                    .build(); // выполняем Задание с информацией о подключении
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(rabbit.getInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        private Properties externalProperties;

        /**
         * Добавляет текущее время в таблицу
         * @param context конекст выполнения Задания
         *                из которого получаем информацию для подклчения
         */
        @Override
        public void execute(JobExecutionContext context) {
            String query = "INSERT INTO rabbit(create_date) values(current_date)";
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void loadProperties() {
            Properties internalProperties = new Properties();
            ClassLoader loader = AlertRabbit.class.getClassLoader();
            try (InputStream io = loader.getResourceAsStream("rabbit.properties")) {
                internalProperties.load(io);
                this.setExternalProperties(internalProperties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Integer getInterval() {
//            ClassLoader loader = AlertRabbit.class.getClassLoader();
//            Properties properties = new Properties();
//            Integer interval = null;
//            try (InputStream io = loader.getResourceAsStream("rabbit.properties")) {
//                properties.load(io);
//                interval = Integer.valueOf(properties.getProperty("rabbit.interval"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return Integer.valueOf(this.externalProperties.getProperty("rabbit.interval"));
        }

        public String getExternalProperties(String key) {
            return externalProperties.get(key).toString();
        }

        public void setExternalProperties(Properties externalProperties) {
            this.externalProperties = externalProperties;
        }

    }
}