package com.digital.reader.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.digital.reader.Activities.ActivityPostDetails;
import com.digital.reader.Model.Post;
import com.digital.reader.R;

import java.util.List;

import static com.digital.reader.Helper.API.ImageUrl;

public class AdapterTrendingPost extends RecyclerView.Adapter<AdapterTrendingPost.PostViewHolder> {

    private Context mCtx;
    private List<Post> postList;

    public AdapterTrendingPost(Context mCtx, List<Post> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
    }

    @NonNull
    @Override
    public AdapterTrendingPost.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_trending, null);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterTrendingPost.PostViewHolder holder, final int position) {

        final Post post = postList.get(position);

        holder.post_main_name.setText(post.getPost_title());
        holder.post_sub_name.setText(post.getPost_content());
        holder.post_username.setText(post.getPost_username());
        holder.post_date.setText(post.getPost_date());

        Log.d("Post_name", "onBindViewHolder: " + post.getPost_title());
        Glide.with(mCtx)
                .load(ImageUrl + post.getPost_image())
                .placeholder(R.drawable.ic_gradient_bg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.post_image);

        Log.d("img_of", "onBindViewHolder: " + ImageUrl + post.getPost_image());

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
                intent.putExtra("premium", post.getPremium_flag());
                intent.putExtra("like", post.getPost_like());

                mCtx.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView post_main_name, post_sub_name, post_content, post_username, post_date;
        ImageView post_image;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            post_main_name = itemView.findViewById(R.id.trending_maintext);
            post_sub_name = itemView.findViewById(R.id.trending_subtext);
            post_username = itemView.findViewById(R.id.trending_post_username);
            post_date = itemView.findViewById(R.id.trending_postdate);
            post_image = itemView.findViewById(R.id.trending_image);


        }
    }
}
