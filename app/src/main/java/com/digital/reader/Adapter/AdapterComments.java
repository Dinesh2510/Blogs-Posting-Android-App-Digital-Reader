package com.digital.reader.Adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digital.reader.Model.Comment;
import com.digital.reader.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.AdapterCommentViewHolder> {
    private Context mCtx;
    private List<Comment> commentArrayList = new ArrayList<>();

    public AdapterComments(Context mCtx, List<Comment> commentArrayList) {
        this.mCtx = mCtx;
        this.commentArrayList = commentArrayList;
    }

    @NonNull
    @Override
    public AdapterComments.AdapterCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_comment, null);
        return new AdapterComments.AdapterCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComments.AdapterCommentViewHolder holder, int position) {
        final Comment comment = commentArrayList.get(position);
        holder.name.setText(comment.getPost_username());
        holder.date.setText(comment.getPost_date());
        Log.d("date", "onBindViewHolder: " + comment.getPost_date());
        holder.comment.setText(comment.getPost_comment());

    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class AdapterCommentViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, comment, date;

        public AdapterCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
            image = itemView.findViewById(R.id.image_img);
        }
    }
}
