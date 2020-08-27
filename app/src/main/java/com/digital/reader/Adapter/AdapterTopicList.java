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
import com.digital.reader.Activities.ActivityTopicWisePostList;
import com.digital.reader.Activities.ActivityUserFollowTopic;
import com.digital.reader.Forms.LoginActivity;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.Model.Topic;
import com.digital.reader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.FollowTopic_URL;
import static com.digital.reader.Helper.API.ImageUrl;
import static com.digital.reader.Helper.API.UnFollowTopic_URL;

public class AdapterTopicList extends RecyclerView.Adapter<AdapterTopicList.TopicViewHolder> {
    public static String firstid = " ", secondid = " ";
    String str_topicid, str_get_follow, str_userid;
    SharedPreferences sharedPreferences;
    ArrayList<Topic> topicArrayList = new ArrayList<>();
    Topic topicResponse1;
    private Context mCtx;
    private List<Topic> topicList = new ArrayList<>();
    private List<Topic> topicFollowList;

    public AdapterTopicList(Context mCtx, List<Topic> topicList) {
        this.mCtx = mCtx;
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public AdapterTopicList.TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_topic_list, null);
        return new AdapterTopicList.TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterTopicList.TopicViewHolder holder, final int position) {

        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");

        final Topic topic = topicList.get(position);
        holder.title.setText(topic.getTopic_title());
        holder.follow_me.setText("FOLLOW");
        Glide.with(mCtx)
                .load(ImageUrl + topic.getTopic_image())
                .placeholder(R.drawable.ic_gradient_bg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.lyt_icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, ActivityTopicWisePostList.class);
                intent.putExtra("title", topic.getTopic_title());
                intent.putExtra("id", topic.getTopic_id());
                mCtx.startActivity(intent);
            }
        });


        holder.follow_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkCheck.isConnect(mCtx)) {

                    if (!str_userid.equals("")) {
                        str_topicid = topicList.get(position).getTopic_id();

                        followTopic(holder);
                    } else {
                        Toast.makeText(mCtx, "Please Login!", Toast.LENGTH_SHORT).show();
                        Intent intentsave = new Intent(mCtx, LoginActivity.class);
                        mCtx.startActivity(intentsave);
                    }
                } else {
                    Toast.makeText(mCtx, "You'r offline!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void followTopic(final TopicViewHolder holder) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FollowTopic_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equalsIgnoreCase("failure"))
                            try {
                                JSONObject jo = new JSONObject(response);
                                String res = jo.getString("response");
                                Log.d("up_res", "parseUpdateData: " + res);

                                if (res.equals("success")) {
                                    Toast.makeText(mCtx, "following...", Toast.LENGTH_SHORT).show();
                                    holder.follow_me.setText("Following");

                                } else {
                                    Toast.makeText(mCtx, "Opps! Something wents Wrong!", Toast.LENGTH_SHORT).show();
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
                params.put("topic_id", str_topicid);
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
        return topicList.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        ImageView lyt_icon;
        TextView title, follow_me;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            lyt_icon = itemView.findViewById(R.id.lyt_icon);
            title = itemView.findViewById(R.id.title);
            follow_me = itemView.findViewById(R.id.follow_me);

        }
    }
}
