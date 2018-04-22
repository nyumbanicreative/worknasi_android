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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import worknasi.adapter.NearmeAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.helper.PopDialog;
import worknasi.model.NearmeItem;
import worknasi.preference.DistanceHistory;
import worknasi.preference.SearchHistory;
import worknasi.worknasiapp.R;

/**
 * Created by user on 2/19/2018.
 */

public class Result_list extends Fragment {
    private static String TAG = Result_list.class.getSimpleName();
    private static String URL_RESULT_LIST = AppConfig.LINK_RESULT_SEARCH_LIST;

    private ListView listView;
    private NearmeAdapter listAdapter;
    private List<NearmeItem> nearmeItemList;
    SearchHistory searchHistory;
    DistanceHistory distanceHistory;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;
    private static int offsetsize = 0;

    AppCompatActivity appCompatActivity;
    Context context;

    LinearLayout nocontent;
    LinearLayout viewcontent;

    public Result_list() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Result_list(Context context, AppCompatActivity appCompatActivity, List<NearmeItem> nearmeItemList) {
        // Required empty public constructor
        this.appCompatActivity = appCompatActivity;
        this.context = context;
        this.nearmeItemList=nearmeItemList;
        this.searchHistory = new SearchHistory(context);
        this.distanceHistory = new DistanceHistory(context);
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
        View view = inflater.inflate(R.layout.result_list, container, false);

        nocontent = (LinearLayout) view.findViewById(R.id.nocontent);
        viewcontent = (LinearLayout) view.findViewById(R.id.viewcontent);

        nocontent.setVisibility(View.GONE);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = true;
            mRefreshLayout.finishRefresh();
        }

        listView = (ListView) view.findViewById(R.id.listfeed);
        listAdapter = new NearmeAdapter(appCompatActivity,context,nearmeItemList,"searchresult");
        listView.setAdapter(listAdapter);

        /*mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                offsetsize = 0;
                LoadFeed(searchHistory.getKeyLatitude(),searchHistory.getKeyLongitude(),offsetsize);
            }
        });*/

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                offsetsize = offsetsize+AppConfig.OFFSETSIZE;
                LoadFeed(searchHistory.getKeyLatitude(),searchHistory.getKeyLongitude(),offsetsize);
                mRefreshLayout.finishLoadmore();
            }
        });

        return view;

    }


    private void LoadFeed(final String latitude, final String longitude, final int offsetsize) {
        final String offset = Integer.toString(offsetsize);

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_RESULT_LIST, new Response.Listener<String>() {

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
                        JSONArray mapArray = jsonObject.getJSONArray("mapproperties");

                        if(offsetsize<5) {
                            nearmeItemList.clear();
                        }

                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject feedObj = (JSONObject) feedArray.get(i);

                            Random random = new Random();
                            int r = 10 + random.nextInt(20);
                            random.nextInt();

                            NearmeItem item = new NearmeItem();
                            item.setProperty_id(feedObj.getString("property_id"));
                            item.setFeedimage(feedObj.getString("thumb_square"));
                            item.setText_property_name(feedObj.getString("property_name"));
                            item.setText_distance(r+ " km");
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

                        listAdapter.notifyDataSetChanged();
                    }catch (JSONException e) {
                        e.printStackTrace();}

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
                params.put("req_lon", latitude);
                params.put("req_lat", longitude);
                params.put("offset", offset);
                params.put("distance", distanceHistory.getDistance());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }




    //----------------------------------------------------------------------------------------------
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