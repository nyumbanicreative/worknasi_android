package worknasi.worknasiapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.fragments.Result_Map;
import worknasi.fragments.Result_list;
import worknasi.model.LocationItem;
import worknasi.model.NearmeItem;
import worknasi.preference.CurrentLocationHistory;
import worknasi.preference.DistanceHistory;
import worknasi.preference.SearchHistory;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;

public class SearchResults extends AppCompatActivity {

    private static String TAG = SearchResults.class.getSimpleName();
    private static String URL_RESULT_LIST = AppConfig.LINK_RESULT_SEARCH_LIST;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SearchHistory searchHistory;
    DistanceHistory distanceHistory;
    UserSession userSession;
    CurrentLocationHistory currentLocationHistory;
    List<NearmeItem> nearmeItemList;
    List<LocationItem> locationItemList;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchHistory = new SearchHistory(getApplicationContext());
        distanceHistory = new DistanceHistory(getApplicationContext());
        userSession = new UserSession(getApplicationContext());
        currentLocationHistory = new CurrentLocationHistory(getApplicationContext());
        nearmeItemList = new ArrayList<>();
        locationItemList = new ArrayList<>();
        progressDialog = new ProgressDialog(SearchResults.this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        LoadFeed(searchHistory.getKeyLatitude(),searchHistory.getKeyLongitude(),0);

    }

    private void setupViewPager(ViewPager viewPager) {
        SearchResults.ViewPagerAdapter adapter = new SearchResults.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Result_list(getApplicationContext(),SearchResults.this,nearmeItemList), "LIST");
        adapter.addFragment(new Result_Map(getApplicationContext(),SearchResults.this,locationItemList), "MAP");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void LoadFeed(final String latitude, final String longitude, final int offsetsize) {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        progressDialog.show();
        final String offset = Integer.toString(offsetsize);

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_RESULT_LIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {

                    progressDialog.dismiss();

                    Log.d(TAG, response.toString());
                    try {
                        //status
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray feedArray = jsonObject.getJSONArray("properties");
                        JSONArray mapArray = jsonObject.getJSONArray("mapproperties");

                        nearmeItemList.clear();
                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject feedObj = (JSONObject) feedArray.get(i);

                            NearmeItem item = new NearmeItem();
                            item.setProperty_id(feedObj.getString("property_id"));
                            item.setFeedimage(feedObj.getString("thumb_square"));
                            item.setText_property_name(feedObj.getString("property_name"));
                            item.setText_distance(feedObj.getString("user_distance") + " Km");
                            item.setText_address(feedObj.getString("address"));
                            item.setText_price(feedObj.getString("price"));
                            item.setText_currency(feedObj.getString("currency_name"));
                            item.setRatingBar(feedObj.getString("ratings"));
                            item.setFavorite(feedObj.getBoolean("favoured"));
                            nearmeItemList.add(item);

                        }

                        if(offsetsize==0){
                            for (int i = 0; i < mapArray.length(); i++) {
                                final JSONObject mapObj = (JSONObject) mapArray.get(i);

                                LocationItem locationItem = new LocationItem();

                                locationItem.setProperty_id(mapObj.getString("property_id"));
                                locationItem.setProperty_name(mapObj.getString("property_name"));
                                locationItem.setAddress(mapObj.getString("address"));
                                locationItem.setRefund_type(mapObj.getString("refund_type"));
                                locationItem.setPrice(mapObj.getString("price"));
                                locationItem.setCurrency_name(mapObj.getString("currency_name"));
                                locationItem.setImage(mapObj.getString("image"));
                                locationItem.setDistance(mapObj.getString("user_distance") + " Km");
                                locationItem.setRatings(mapObj.getString("ratings"));
                                locationItem.setLatitude(mapObj.getString("latitude"));
                                locationItem.setLongitude(mapObj.getString("longitude"));
                                locationItem.setFavorite(mapObj.getBoolean("favoured"));

                                locationItemList.add(locationItem);
                            }
                        }

                        setupViewPager(viewPager);
                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

                    }catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
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
                params.put("req_lat", latitude);
                params.put("req_lon", longitude);
                params.put("user_lat", currentLocationHistory.getKeyLatitude());
                params.put("user_lon", currentLocationHistory.getKeyLongitude());
                params.put("offset", offset);
                params.put("distance", distanceHistory.getDistance());
                params.put("user_id", userSession.getUserId());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

}
