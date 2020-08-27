package com.digital.reader.Model;

public class Comment {
    String comment_id;
    String post_userid;
    String post_id;
    String post_username;
    String post_comment;
    String post_date;

    public Comment(String comment_id, String post_userid, String post_id, String post_username, String post_comment, String post_date) {
        this.comment_id = comment_id;
        this.post_userid = post_userid;
        this.post_id = post_id;
        this.post_username = post_username;
        this.post_comment = post_comment;
        this.post_date = post_date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getPost_userid() {
        return post_userid;
    }

    public void setPost_userid(String post_userid) {
        this.post_userid = post_userid;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_username() {
        return post_username;
    }

    public void setPost_username(String post_username) {
        this.post_username = post_username;
    }

    public String getPost_comment() {
        return post_comment;
    }

    public void setPost_comment(String post_comment) {
        this.post_comment = post_comment;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }
}
