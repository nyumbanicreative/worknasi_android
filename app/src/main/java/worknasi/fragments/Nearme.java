package worknasi.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
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

import worknasi.adapter.NearmeAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.NearmeItem;
import worknasi.preference.CurrentLocationHistory;
import worknasi.preference.DistanceHistory;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;

/**
 * Created by user on 2/15/2018.
 */

@SuppressLint("ValidFragment")
public class Nearme extends Fragment {
    private static String TAG = Nearme.class.getSimpleName();
    private static String URL_NEARME = AppConfig.LINK_NEARME;

    private ListView listView;
    private NearmeAdapter nearmeAdapter;
    private List<NearmeItem> nearmeItemList;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;
    private static int offsetsize = 0;

    CurrentLocationHistory currentLocationHistory;
    DistanceHistory distanceHistory;
    UserSession userSession;

    LinearLayout nocontent;
    LinearLayout viewcontent;

    AppCompatActivity appCompatActivity;
    Context context;

    @SuppressLint("ValidFragment")
    public Nearme(AppCompatActivity appCompatActivity, Context context) {
        // Required empty public constructor
        this.appCompatActivity = appCompatActivity;
        this.context = context;
        currentLocationHistory = new CurrentLocationHistory(context);
        distanceHistory = new DistanceHistory(context);
        userSession = new UserSession(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearme, container, false);

        nocontent = (LinearLayout) view.findViewById(R.id.nocontent);
        viewcontent = (LinearLayout) view.findViewById(R.id.viewcontent);

        nocontent.setVisibility(View.GONE);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = true;
            mRefreshLayout.autoRefresh();
        }

        nearmeItemList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listfeed);
        nearmeAdapter = new NearmeAdapter(appCompatActivity,context,nearmeItemList,"nearme");
        listView.setAdapter(nearmeAdapter);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                offsetsize = 0;
                LoadFeed(currentLocationHistory.getKeyLatitude(),currentLocationHistory.getKeyLongitude(),offsetsize);
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                offsetsize = offsetsize+AppConfig.OFFSETSIZE;
                LoadFeed(currentLocationHistory.getKeyLatitude(),currentLocationHistory.getKeyLongitude(),offsetsize);
                mRefreshLayout.finishLoadmore();
            }
        });

        return view;

    }

    private void LoadFeed(final String latitude, final String longitude, final int offsetsize) {
        final String offset = Integer.toString(offsetsize);

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_NEARME, new Response.Listener<String>() {

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
                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
                        }else {
                        }

                        JSONArray feedArray = jsonObject.getJSONArray("properties");
                        if(offsetsize<1) {
                            nearmeItemList.clear();
                        }

                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject feedObj = (JSONObject) feedArray.get(i);

                            NearmeItem item = new NearmeItem();
                            item.setProperty_id(feedObj.getString("property_id"));
                            item.setFeedimage(feedObj.getString("thumb_square"));
                            item.setText_property_name(feedObj.getString("property_name"));
                            item.setText_distance(feedObj.getString("distance") + " Km");
                            item.setText_address(feedObj.getString("address"));
                            item.setText_price(feedObj.getString("price"));
                            item.setText_currency(feedObj.getString("currency_name"));
                            item.setRatingBar(feedObj.getString("ratings"));
                            item.setFavorite(feedObj.getBoolean("favoured"));
                            nearmeItemList.add(item);

                        }

                        if(nearmeItemList.size()==0){
                            viewcontent.setVisibility(View.GONE);
                            nocontent.setVisibility(View.VISIBLE);
                        }

                        // notify data changes to list adapater
                        nearmeAdapter.notifyDataSetChanged();

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
                params.put("req_lat", latitude);
                params.put("req_lon", longitude);
                params.put("offset", offset);
                params.put("distance", distanceHistory.getDistance());
                params.put("user_id", userSession.getUserId());
                params.put("near_me_type","list");

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {

            int default_distance = Integer.parseInt(distanceHistory.getDistance());
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(appCompatActivity);
            // ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = (appCompatActivity).getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.item_filter_distance, null);
            dialogBuilder.setView(dialogView);

            SeekBar seekBar = (SeekBar) dialogView.findViewById(R.id.seekBar);
            final TextView text_seekBar = (TextView) dialogView.findViewById(R.id.text_seekBar);

            Typeface face = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/wregular.ttf");
            text_seekBar.setTypeface(face);
            text_seekBar.setText("Distance : "+ default_distance +" Km");
            seekBar.setProgress(default_distance);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    text_seekBar.setText("Distance : "+i+" Km");
                    distanceHistory.setDistance(Integer.toString(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.hide();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Filter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nocontent.setVisibility(View.GONE);
                    viewcontent.setVisibility(View.VISIBLE);
                    mRefreshLayout.autoRefresh();
                }
            });
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}