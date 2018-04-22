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

import worknasi.adapter.MyfavoriteAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.ItemMyfavorite;
import worknasi.preference.CurrentLocationHistory;
import worknasi.preference.UserSession;

public class MyFavorite extends AppCompatActivity {

    private static String TAG = MyFavorite.class.getSimpleName();
    private static String URL_MYFAVORITES = AppConfig.LINK_MYFAVORITE;

    private ListView listView;
    private MyfavoriteAdapter myfavoriteAdapter;
    private List<ItemMyfavorite> myfavoriteList;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;
    private static int offsetsize = 0;

    UserSession userSession;
    CurrentLocationHistory currentLocationHistory;
    LinearLayout nocontent;
    LinearLayout viewcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);
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
        currentLocationHistory = new CurrentLocationHistory(getApplicationContext());

        nocontent = (LinearLayout) findViewById(R.id.nocontent);
        viewcontent = (LinearLayout) findViewById(R.id.viewcontent);

        nocontent.setVisibility(View.GONE);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = true;
            mRefreshLayout.autoRefresh();
        }

        myfavoriteList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listfeed);
        myfavoriteAdapter = new MyfavoriteAdapter(MyFavorite.this,getApplicationContext(),myfavoriteList);
        listView.setAdapter(myfavoriteAdapter);

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
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_MYFAVORITES, new Response.Listener<String>() {

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
                        JSONArray feedArray = jsonObject.getJSONArray("properties");
                        if(offsetsize<1) {
                            myfavoriteList.clear();
                        }
                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject feedObj = (JSONObject) feedArray.get(i);

                            ItemMyfavorite item = new ItemMyfavorite();
                            item.setProperty_id(feedObj.getString("property_id"));
                            item.setFeedimage(feedObj.getString("thumb_square"));
                            item.setText_property_name(feedObj.getString("property_name"));
                            item.setText_distance(feedObj.getString("distance") + " Km");
                            item.setText_address(feedObj.getString("address"));
                            item.setText_price(feedObj.getString("price"));
                            item.setText_currency(feedObj.getString("currency_name"));
                            item.setRatingBar(feedObj.getString("ratings"));
                            item.setPosition(i);
                            myfavoriteList.add(item);
                        }

                        if(myfavoriteList.size()==0){
                            viewcontent.setVisibility(View.GONE);
                            nocontent.setVisibility(View.VISIBLE);
                        }

                        // notify data changes to list adapater
                        myfavoriteAdapter.notifyDataSetChanged();

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
                params.put("req_lat", currentLocationHistory.getKeyLatitude());
                params.put("req_lon", currentLocationHistory.getKeyLongitude());
                params.put("user_id", userSession.getUserId());
                params.put("key", userSession.getUserKey());
                params.put("offset", offset);

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


}
