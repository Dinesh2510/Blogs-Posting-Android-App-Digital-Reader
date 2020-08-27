package com.digital.reader.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PostViewModel extends AndroidViewModel {

    PostRepository postRepository;
    LiveData<List<PostData>> postList;
    PostData postData;


    public PostViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }


    public void insert(PostData postData) {
        postRepository.insert(postData);
    }


    public void deleteAllPost() {
        postRepository.DeleteAllPostData();
    }

    public LiveData<List<PostData>> GetAllPostList() {
        return postRepository.getAllPostData();
    }


}