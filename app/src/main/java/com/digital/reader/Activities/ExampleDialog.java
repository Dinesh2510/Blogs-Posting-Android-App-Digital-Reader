package com.digital.reader.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Adapter.AdapterComments;
import com.digital.reader.Adapter.AdapterTopicList;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.Model.Comment;
import com.digital.reader.Model.Topic;
import com.digital.reader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.digital.reader.Helper.API.GET_ALL_TOPIC_URL;
import static com.digital.reader.Helper.API.GetCommentList;

public class ExampleDialog extends DialogFragment {

    public static final String TAG = "example_dialog";
    RecyclerView recyclerView_topic;
    ArrayList<Comment> commentArrayList = new ArrayList<>();
    AdapterComments adapterComments;
    String post_id, str_cmt;
    ImageButton submit_cmt;
    EditText et_cmt;
    private Toolbar toolbar;

    public static ExampleDialog display(FragmentManager fragmentManager) {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.example_dialog, container, false);

        post_id = getActivity().getIntent().getStringExtra("id");
        Log.d("postsis", "onCreateView: " + post_id);

        toolbar = view.findViewById(R.id.toolbar);
        et_cmt = view.findViewById(R.id.et_cmt);
        submit_cmt = view.findViewById(R.id.submit_cmt);
        recyclerView_topic = view.findViewById(R.id.recyclerView_all_cmt);
        adapterComments = new AdapterComments(getContext(), commentArrayList);
        recyclerView_topic.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_topic.setAdapter(adapterComments);
        adapterComments.notifyDataSetChanged();
        if (NetworkCheck.isConnect(getContext())) {
            loadCmtData();
        } else {
            Toast.makeText(getContext(), "You'r offline!", Toast.LENGTH_SHORT).show();
        }
        submit_cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_cmt = et_cmt.getText().toString();
                if (str_cmt.equals("")) {
                    Toast.makeText(getContext(), "enter the comment", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "" + str_cmt, Toast.LENGTH_SHORT).show();

                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleDialog.this.dismiss();
            }
        });
        toolbar.setTitle("Comments");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextAction));
//        toolbar.inflateMenu(R.menu.example_dialog);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ExampleDialog.this.dismiss();
//                return true;
//            }
//        });
    }

    private void loadCmtData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GetCommentList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equalsIgnoreCase("failure"))
                            try {
                                JSONObject jo = new JSONObject(response);
                                String res = jo.getString("response");
                                Log.d("up_res", "parseUpdateData: " + res);

                                if (res.equals("failure")) {
                                    Toast.makeText(getContext(), "Comments not found!", Toast.LENGTH_SHORT).show();


                                } else {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray array = obj.getJSONArray("response");


                                    Log.d("respondsf", "" + response);
                                    // JSONArray array = new JSONArray("response");

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject rlist = array.getJSONObject(i);
                                        commentArrayList.add(new Comment(
                                                rlist.getString("comment_id"),
                                                rlist.getString("post_userid"),
                                                rlist.getString("post_id"),
                                                rlist.getString("post_username"),
                                                rlist.getString("post_comment"),
                                                rlist.getString("post_date")
                                        ));
                                        adapterComments = new AdapterComments(getContext(), commentArrayList);
                                        setDataAdaptercmtList();

                                    }
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

                params.put("post_id", post_id);
                Log.d("parameter", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void setDataAdaptercmtList() {
        if (commentArrayList.size() > 0) {

            adapterComments = new AdapterComments(getContext(), commentArrayList);
            recyclerView_topic.setVisibility(View.VISIBLE);
            recyclerView_topic.setAdapter(adapterComments);

        } else {
            recyclerView_topic.setVisibility(View.GONE);

        }
    }

}