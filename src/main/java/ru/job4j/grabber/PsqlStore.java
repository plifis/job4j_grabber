package ru.job4j.grabber;


import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                                cfg.getProperty("jdbc.url"),
                                cfg.getProperty("jdbc.username"),
                                cfg.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        String strQuery = "INSERT INTO post(name, text, link, created) values (?, ?, ?, ?) ";
        try (PreparedStatement statement = cnn.prepareStatement(strQuery)) {
            statement.setString(1, post.getName());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getUrl());
            statement.setDate(4, post.getSqlDate());
            statement.executeUpdate();
        } catch (Exception e) {
           System.out.println("Данный пост уже добавлен ранее");
        }
    }

    @Override
    public List<Post> getAll() {
        String strQuery = "SELECT * FROM post";
        List<Post> listPost = new ArrayList<>();
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement(strQuery)) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                    post = new Post(set.getString("name"),
                                    set.getString("text"),
                                    set.getString("link"),
                                    set.getDate("created"));
                    listPost.add(post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listPost;
    }

    @Override
    public Post findById(String id) {
        String strQuery = "SELECT * FROM post WHERE id = ?";
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement(strQuery)) {
            statement.setInt(1, Integer.parseInt(id));
            ResultSet set = statement.executeQuery();
                while (set.next()) {
                    post = new Post(set.getString("name"),
                                    set.getString("text"),
                                    set.getString("link"),
                                    set.getDate("created"));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }



    public static void main(String[] args) throws IOException, ParseException {
        InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties");
        Properties config = new Properties();
        config.load(in);
        PsqlStore psql = new PsqlStore(config);
        SqlRuParse ruParse = new SqlRuParse();
        Post post = ruParse.getPost("https://www.sql.ru/forum/1332113/arhitektor-prikladnogo-po-v-produkt-polator");
        psql.save(post);
        System.out.println(psql.getAll());
        System.out.println(psql.findById("1"));
    }
}