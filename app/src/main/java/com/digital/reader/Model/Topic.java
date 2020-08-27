package com.digital.reader.Model;

public class Topic {

    String topic_id;
    String topic_title;
    String topic_image;

    public Topic(String topic_id, String topic_title, String topic_image) {
        this.topic_id = topic_id;
        this.topic_title = topic_title;
        this.topic_image = topic_image;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }

    public String getTopic_image() {
        return topic_image;
    }

    public void setTopic_image(String topic_image) {
        this.topic_image = topic_image;
    }
}
