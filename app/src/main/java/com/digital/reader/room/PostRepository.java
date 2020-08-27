package com.digital.reader.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PostRepository {
    public PostDao postDao;
    LiveData<List<PostData>> postList;
    PostData postData;

    public PostRepository(Application application) {
        ProjectDatabase database = ProjectDatabase.getDatabaseInstance(application);
        postDao = database.postDao();
    }


    public void insert(PostData postData) {
        new PostRepository.InsertPostAsyncTask(postDao).execute(postData);
    }


    public LiveData<List<PostData>> getAllPostData() {

        return postDao.getAllPostData();
    }


    public void DeleteAllPostData() {

        new PostRepository.DeleteAllPostDataAsyncTask(postDao).execute();
    }


    public static class InsertPostAsyncTask extends AsyncTask<PostData, Void, Void> {
        PostDao postDao;

        public InsertPostAsyncTask(PostDao postDao) {
            this.postDao = postDao;
        }

        @Override
        protected Void doInBackground(PostData... pos) {
            postDao.insert(pos[0]);
            return null;
        }
    }


    public class DeleteAllPostDataAsyncTask extends AsyncTask<Void, Void, Void> {

        PostDao postDao;

        public DeleteAllPostDataAsyncTask(PostDao postDao) {
            this.postDao = postDao;

        }

        @Override
        protected Void doInBackground(Void... Void) {

            postDao.DeleteAllPostData();
            return null;
        }


    }


}
