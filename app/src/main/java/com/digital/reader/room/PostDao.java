package com.digital.reader.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {

    @Insert
    public void insert(PostData postData);

    @Query("DELETE FROM post_data")
    public void DeleteAllPostData();

    @Query("SELECT * FROM post_data ")
    public LiveData<List<PostData>> getAllPostData();

}
