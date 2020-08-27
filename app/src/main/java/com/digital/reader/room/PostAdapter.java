package com.digital.reader.room;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.digital.reader.Activities.ActivityPostDetails;
import com.digital.reader.R;


public class PostAdapter extends ListAdapter<PostData, PostAdapter.MyViewHolder> {

    public static final DiffUtil.ItemCallback<PostData> diffUtilCallBack = new DiffUtil.ItemCallback<PostData>() {

        @Override
        public boolean areItemsTheSame(@NonNull PostData oldItem, @NonNull PostData newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PostData oldItem, @NonNull PostData newItem) {
            return false;
        }
    };
    private boolean mapClick = false;

    public PostAdapter() {
        super(diffUtilCallBack);
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.MyViewHolder holder, int position) {
        final PostData post = getItem(position);
        holder.post_main_name.setText(post.getPost_title());
        holder.title_content.setText(post.getPost_content());
        holder.post_username.setText(post.getPost_username());
        holder.post_date.setText(post.getPost_date());

        Log.d("Post_name", "onBindViewHolder: " + post.getPost_title());
        Glide.with(holder.itemView.getContext())
                .load("http://pixeldev.in/webservices/digital_reader/admin/" + post.getPost_image())
                .placeholder(R.drawable.ic_launcher_background)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.post_image);
        Log.d("img_url", "onBindViewHolder: " + post.getPost_image());

        Log.d("img_of", "onBindViewHolder: " + post.getPost_image());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ActivityPostDetails.class);
                intent.putExtra("title", post.getPost_title());
                intent.putExtra("content", post.getPost_content());
                intent.putExtra("username", post.getPost_username());
                intent.putExtra("date", post.getPost_date());
                intent.putExtra("id", post.getPost_id());
                intent.putExtra("image", post.getPost_image());
                intent.putExtra("link", post.getPost_link());
                intent.putExtra("like", post.getPost_like());
                intent.putExtra("premium", post.getPremium_flag());
                Log.d("oo", "onClick: " + post.getPremium_flag());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    public PostData getDealerAt(int position) {
        return getItem(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView post_main_name, title_content, post_content, post_username, post_date;
        ImageView post_image, bookmark;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            post_main_name = itemView.findViewById(R.id.title);
            title_content = itemView.findViewById(R.id.title_content);
            post_username = itemView.findViewById(R.id.post_username);
            post_date = itemView.findViewById(R.id.date);
            post_image = itemView.findViewById(R.id.image);


        }
    }


}
