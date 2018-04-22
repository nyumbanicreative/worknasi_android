package worknasi.worknasiapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import worknasi.adapter.BookedSpaceAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.ItemReservedSpace;
import worknasi.preference.UserSession;

public class BookedSpace extends AppCompatActivity {

    private static String TAG = BookedSpace.class.getSimpleName();
    private static String URL_BOOKEDSPACE = AppConfig.LINK_BOOKEDSPACE;

    private ListView listView;
    private BookedSpaceAdapter reservedSpaceAdapetr;
    private List<ItemReservedSpace> reservedSpaceList;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;
    private static int offsetsize = 0;

    UserSession userSession;

    LinearLayout nocontent;
    LinearLayout viewcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_space);
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

        nocontent = (LinearLayout) findViewById(R.id.nocontent);
        viewcontent = (LinearLayout) findViewById(R.id.viewcontent);

        nocontent.setVisibility(View.GONE);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = true;
            mRefreshLayout.autoRefresh();
        }

        reservedSpaceList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listfeed);
        reservedSpaceAdapetr = new BookedSpaceAdapter(BookedSpace.this,getApplicationContext(),reservedSpaceList);
        listView.setAdapter(reservedSpaceAdapetr);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                offsetsize = 0;
                LoadFeed(offsetsize);
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                offsetsize = offsetsize+AppConfig.OFFSETSIZE;
                LoadFeed(offsetsize);
                mRefreshLayout.finishLoadmore();
            }
        });

    }


    private void LoadFeed(final int offsetsize) {
        final String offset = Integer.toString(offsetsize);

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_BOOKEDSPACE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    mRefreshLayout.finishRefresh();
                    Log.d(TAG, response.toString());
                    try {
                        //status
                        JSONObject jsonObject = new JSONObject(response);

                        //check status
                        JSONObject  statusObj = jsonObject.getJSONObject("status");
                        if(statusObj.getBoolean("error")){
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                        }else {
                        }

                        JSONArray feedArray = jsonObject.getJSONArray("reserved");
                        if(offsetsize<1) {
                            reservedSpaceList.clear();
                        }

                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject feedObj = (JSONObject) feedArray.get(i);

                            ItemReservedSpace item = new ItemReservedSpace();
                            item.setBooking_id(feedObj.getString("booking_id"));
                            item.setBooking_date(feedObj.getString("booking_date"));
                            item.setCheck_in_date(feedObj.getString("check_in_date"));
                            item.setCheck_out_date(feedObj.getString("check_out_date"));
                            item.setNumber_of_people(feedObj.getString("no_of_people"));
                            item.setStatus(feedObj.getString("status"));
                            item.setEffective_amount(feedObj.getString("effective_amount"));
                            item.setCurrency_name(feedObj.getString("currency_name"));
                            item.setAdmin_id(feedObj.getString("admin_id"));
                            item.setAdmin_first_name(feedObj.getString("admin_first_name"));
                            item.setAdmin_last_name(feedObj.getString("admin_last_name"));
                            item.setAdmin_email(feedObj.getString("admin_email"));
                            item.setAdmin_profile_image(feedObj.getString("admin_profile_image"));
                            item.setUser_id(feedObj.getString("user_id"));
                            item.setUser_first_name(feedObj.getString("user_first_name"));
                            item.setUser_last_name(feedObj.getString("user_last_name"));
                            item.setUser_email(feedObj.getString("user_email"));
                            item.setUser_profile_image(feedObj.getString("user_profile_image"));
                            item.setProperty_id(feedObj.getString("property_id"));
                            item.setProperty_name(feedObj.getString("property_name"));
                            reservedSpaceList.add(item);
                        }

                        if(reservedSpaceList.size()==0){
                            viewcontent.setVisibility(View.GONE);
                            nocontent.setVisibility(View.VISIBLE);
                        }

                        // notify data changes to list adapater
                        reservedSpaceAdapetr.notifyDataSetChanged();

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
                params.put("user_id",userSession.getUserId());
                params.put("key",userSession.getUserKey());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


}
