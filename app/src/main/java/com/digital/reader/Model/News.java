package com.digital.reader.Model;

public class News {
    String news_id;
    String news_title;
    String news_detail;
    String news_date;
    String news_image;
    String news_userid;
    String news_username;

    public News(String news_id, String news_title, String news_detail, String news_date, String news_image, String news_userid, String news_username) {
        this.news_id = news_id;
        this.news_title = news_title;
        this.news_detail = news_detail;
        this.news_date = news_date;
        this.news_image = news_image;
        this.news_userid = news_userid;
        this.news_username = news_username;
    }

    public String getNews_userid() {
        return news_userid;
    }

    public void setNews_userid(String news_userid) {
        this.news_userid = news_userid;
    }

    public String getNews_username() {
        return news_username;
    }

    public void setNews_username(String news_username) {
        this.news_username = news_username;
    }

    public String getNews_image() {
        return news_image;
    }

    public void setNews_image(String news_image) {
        this.news_image = news_image;
    }

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getNews_detail() {
        return news_detail;
    }

    public void setNews_detail(String news_detail) {
        this.news_detail = news_detail;
    }

    public String getNews_date() {
        return news_date;
    }

    public void setNews_date(String news_date) {
        this.news_date = news_date;
    }
}
