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
import com.digital.reader.Activities.ActivityTopicWisePostList;
import com.digital.reader.Model.Topic;
import com.digital.reader.R;

import java.util.List;

import static com.digital.reader.Helper.API.ImageUrl;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private Context mCtx;
    private List<Topic> topicList;


    public TopicAdapter(Context mCtx, List<Topic> topicList) {
        this.mCtx = mCtx;
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public TopicAdapter.TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_topics, null);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicAdapter.TopicViewHolder holder, int position) {
        final Topic topic = topicList.get(position);
        holder.title.setText(topic.getTopic_title());
        Glide.with(mCtx)
                .load(ImageUrl + topic.getTopic_image())
                .placeholder(R.drawable.ic_gradient_bg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.lyt_icon);
        Log.d("img_url", "onBindViewHolder: " + topic.getTopic_image());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, ActivityTopicWisePostList.class);
                intent.putExtra("title", topic.getTopic_title());
                intent.putExtra("id", topic.getTopic_id());

                mCtx.startActivity(intent);
            }
        });
        Log.d("TAG_On", "onBindViewHolder: " + topic.getTopic_id());
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        ImageView lyt_icon;
        TextView title;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            lyt_icon = itemView.findViewById(R.id.lyt_icon);
            title = itemView.findViewById(R.id.title);


        }
    }
}
