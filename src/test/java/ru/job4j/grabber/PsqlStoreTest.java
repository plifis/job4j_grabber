package ru.job4j.grabber;

import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;


public class PsqlStoreTest {
    @Test
    public void testConnection() {
        InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties");
        Properties config = new Properties();
        try {
            config.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(config.getProperty("jdbc.url"), "jdbc:postgresql://127.0.0.1:5432/grabber");
    }
}