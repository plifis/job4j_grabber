package ru.job4j.html;

import java.util.Date;

public class Post {
    private String name;
    private String description;
    private String url;
    private Date date;

    public Post (String name, String description, String url, Date date) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Post{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", date=" + date +
                '}';
    }
}
