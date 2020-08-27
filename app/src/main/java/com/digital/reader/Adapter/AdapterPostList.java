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

public class AdapterPostList extends RecyclerView.Adapter<AdapterPostList.PostViewHolder> {
    private Context mCtx;
    private List<Post> postList;

    public AdapterPostList(Context mCtx, List<Post> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
    }

    @NonNull
    @Override
    public AdapterPostList.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_post, null);
        return new AdapterPostList.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPostList.PostViewHolder holder, final int position) {
        final Post post = postList.get(position);

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
        Log.d("img_url", "onBindViewHolder: " + ImageUrl + post.getPost_image());

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
                intent.putExtra("link", postList.get(position).getPost_link());
                intent.putExtra("like", postList.get(position).getPost_like());
                intent.putExtra("premium", postList.get(position).getPremium_flag());
                Log.d("oo", "onClick: " + postList.get(position).getPremium_flag());
                mCtx.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView post_main_name, title_content, post_content, post_username, post_date;
        ImageView post_image, bookmark;
        ;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            post_main_name = itemView.findViewById(R.id.title);
            title_content = itemView.findViewById(R.id.title_content);
            post_username = itemView.findViewById(R.id.post_username);
            post_date = itemView.findViewById(R.id.date);
            post_image = itemView.findViewById(R.id.image);
        }
    }
}
