package com.digital.reader.Model;

public class Count {
    String user_id;
    String row_post;
    String row_follow;
    String row_bk;

    public Count(String user_id, String row_post, String row_follow, String row_bk) {
        this.user_id = user_id;
        this.row_post = row_post;
        this.row_follow = row_follow;
        this.row_bk = row_bk;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRow_post() {
        return row_post;
    }

    public void setRow_post(String row_post) {
        this.row_post = row_post;
    }

    public String getRow_follow() {
        return row_follow;
    }

    public void setRow_follow(String row_follow) {
        this.row_follow = row_follow;
    }

    public String getRow_bk() {
        return row_bk;
    }

    public void setRow_bk(String row_bk) {
        this.row_bk = row_bk;
    }
}
