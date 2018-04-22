package worknasi.worknasiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import worknasi.adapter.ChatMessageAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.helper.CircleTransform;
import worknasi.model.ChatMessage;
import worknasi.preference.CreateChat;
import worknasi.preference.PreviewData;
import worknasi.preference.UserSession;

public class Chat extends AppCompatActivity {

    private static String TAG = Chat.class.getSimpleName();
    private static String URL_CHAT = AppConfig.LINK_CHAT;
    private static String URL_SENDTEXT = AppConfig.LINK_CHAT_SEND;
    private RecyclerView mRecyclerView;
    private TextView mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    private ProgressBar sendprogress;

    private ChatMessageAdapter mAdapter;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;
    private static int offsetsize = 0;

    List<ChatMessage> chatMessageList;

    UserSession userSession;
    CreateChat createChat;
    PreviewData previewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userSession = new UserSession(getApplicationContext());
        createChat = new CreateChat(getApplicationContext());
        previewData = new PreviewData(getApplicationContext());

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(AppConfig.CHAT_BROADCAST));

        ImageView back_arrow = (ImageView) findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CircleImageView dp = (CircleImageView) findViewById(R.id.dp);
        TextView from = (TextView) findViewById(R.id.from);

        Glide.with(getApplicationContext()).load(createChat.getKeyTargetImage())
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.avarta)
                .transform(new CircleTransform(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(dp);
        from.setText(previewData.getKeyFirstname()+" "+previewData.getKeyLastname());

        chatMessageList = new ArrayList<ChatMessage>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mButtonSend = (TextView) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        sendprogress = (ProgressBar) findViewById(R.id.sendprogress);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            //this is the top of the RecyclerView
            Toast.makeText(getApplicationContext(),"Load more messages",Toast.LENGTH_LONG).show();
        }

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            mRefreshLayout.autoRefresh();
        }


        mAdapter = new ChatMessageAdapter(this,chatMessageList);
        mRecyclerView.setAdapter(mAdapter);


        // show loader and fetch messages
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                offsetsize = 0;
                getMessagesByVolley(offsetsize);
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                offsetsize = offsetsize+5;
                mRefreshLayout.finishLoadmore();
            }
        });


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessageByVolley(message);
            }
        });

    }

    private void getMessage(String message,String time) {
        ChatMessage chatMessage = new ChatMessage(message,time, true, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message,new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date()), true, false);
        mAdapter.add(chatMessage);

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void getOtherMessage(String message,String time) {
        ChatMessage chatMessage = new ChatMessage(message,time, false, false);
        mAdapter.add(chatMessage);

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            String json = intent.getStringExtra("data");
            //Toast.makeText(getApplicationContext(),json,Toast.LENGTH_LONG).show();

            try {
                JSONObject jObject  = new JSONObject(json);
                JSONObject  data = jObject.getJSONObject("data");

                String title = data.getString("title");
                String message = data.getString("message");
                boolean isBackground = data.getBoolean("is_background");
                String imageUrl = data.getString("image");
                String timestamp = data.getString("timestamp");
                JSONObject payload = data.getJSONObject("payload");

                //JSONObject jObjectpayload  = new JSONObject(payload.toString());
                JSONObject  datapayload = data.getJSONObject("payload");
                if(datapayload.getString("id").equals( getIntent().getExtras().getString("target_id").toString())) {
                    getOtherMessage(datapayload.getString("msg"), datapayload.getString("timestamp"));
                }

            } catch (JSONException e) {
                Log.e("Chatika error1",e.getMessage());
            } catch (Exception e) {
                Log.e("Chatika error2",e.getMessage());
            }
        }
    };


    //load all messages
    private void getMessagesByVolley(final int offset){
        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_CHAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    Log.d(TAG, response.toString());
                    try {
                        //status
                        mRefreshLayout.finishRefresh();
                        chatMessageList.clear();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray feedArray = jsonObject.getJSONArray("chat");

                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject feedObj = (JSONObject) feedArray.get(i);

                            if(feedObj.getString("msg_from").equals(userSession.getUserId()))
                            {
                                getMessage(feedObj.getString("msg_content"),feedObj.getString("msg_timestamp"));
                            }else {
                                getOtherMessage(feedObj.getString("msg_content"),feedObj.getString("msg_timestamp"));
                            }
                        }
                        // notify data changes to list adapater
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

                    }catch (JSONException e) {e.printStackTrace();}

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userSession.getUserId());
                params.put("target_id", previewData.getKeyUserId()); //getIntent().getExtras().getString("target_id").toString()
                params.put("offset", Integer.toString(offset));

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }


    private void sendMessageByVolley(final String content){
        sendprogress.setVisibility(View.VISIBLE);
        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_SENDTEXT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    sendMessage(content);
                    sendprogress.setVisibility(View.GONE);
                    mEditTextMessage.setText("");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userSession.getUserId());
                params.put("target_id", previewData.getKeyUserId()); //getIntent().getExtras().getString("target_id").toString()
                params.put("content",content);
                params.put("type", "TEXT");
                params.put("key", userSession.getUserKey());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

}
