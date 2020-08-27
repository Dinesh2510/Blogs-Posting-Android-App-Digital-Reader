package com.digital.reader.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.digital.reader.Activities.ActivityPostDetails;
import com.digital.reader.Activities.ActivityUserBookMarkList;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.Model.Post;
import com.digital.reader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.DeleteBookmarkPost;
import static com.digital.reader.Helper.API.ImageUrl;

public class AdapterBookmarkList extends RecyclerView.Adapter<AdapterBookmarkList.BookmarkViewHolder> {
    String str_userid, str_postid;
    SharedPreferences sharedPreferences;
    private Context mCtx;
    private List<Post> postList;

    public AdapterBookmarkList(Context mCtx, List<Post> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
    }

    @NonNull
    @Override
    public AdapterBookmarkList.BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_bookmark_list, null);
        return new AdapterBookmarkList.BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterBookmarkList.BookmarkViewHolder holder, final int position) {
        final Post post = postList.get(position);
        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");

        holder.post_main_name.setText(post.getPost_title());
        holder.title_content.setText(post.getPost_content());
        holder.post_username.setText(post.getPost_username());
        holder.post_date.setText(post.getPost_date());

        Log.d("Post_name", "onBindViewHolder: " + post.getPost_title());
        Glide.with(mCtx)
                .load(ImageUrl + post.getPost_image())
                .placeholder(R.drawable.ic_gradient_bg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.post_image);

        Log.d("img_of", "onBindViewHolder: " + post.getPost_image());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, ActivityPostDetails.class);
                intent.putExtra("title", post.getPost_title());
                intent.putExtra("content", post.getPost_content());
                intent.putExtra("username", post.getPost_username());
                intent.putExtra("date", post.getPost_date());
                intent.putExtra("id", post.getPost_id());
                intent.putExtra("image", post.getPost_image());
                intent.putExtra("link", post.getPost_link());
                intent.putExtra("premium", postList.get(position).getPremium_flag());
                mCtx.startActivity(intent);
            }
        });


        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_postid = postList.get(position).getPost_id();
                if (NetworkCheck.isConnect(mCtx)) {

                    DeleteBookmarkPost(holder);
                } else {
                    Toast.makeText(mCtx, "Your Are Offline!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void DeleteBookmarkPost(final BookmarkViewHolder holder) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DeleteBookmarkPost,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("logrrd", "onResponse: " + jsonObject);
                            String res = jsonObject.getString("response");
                            if (res.equals("success")) {
                                Toast.makeText(mCtx, "Post Removed", Toast.LENGTH_SHORT).show();
                                ActivityUserBookMarkList.aubl.loadBkList();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", str_userid);
                params.put("post_id", str_postid);
                Log.d("parameter", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);


    }


    @Override
    public int getItemCount() {
        return postList.size();

    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView post_main_name, title_content, post_content, post_username, post_date;
        ImageView post_image, delete_btn, bookmark;
        ;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);


            post_main_name = itemView.findViewById(R.id.title);
            title_content = itemView.findViewById(R.id.title_content);
            post_username = itemView.findViewById(R.id.post_username);
            post_date = itemView.findViewById(R.id.date);
            post_image = itemView.findViewById(R.id.image);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
    }
}
