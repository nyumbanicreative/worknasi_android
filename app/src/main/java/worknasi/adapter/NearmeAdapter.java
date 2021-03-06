package worknasi.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.fragments.LoginDialogFragment;
import worknasi.model.NearmeItem;
import worknasi.preference.ArrayProperties;
import worknasi.preference.CurrentLocationHistory;
import worknasi.preference.PreviewData;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.Preview;
import worknasi.worknasiapp.R;


/**
 * Created by user on 1/10/2018.
 */

public class NearmeAdapter extends BaseAdapter {
    private String TAG = NearmeAdapter.class.getSimpleName();
    private static final String endpoint = AppConfig.LINK_FETCH_PREVIEW;
    private static final String favorite_endpoint = AppConfig.LINK_FAVORITE;

    private AppCompatActivity appCompatActivity;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    List<NearmeItem> nearmeItemList;
    Context context;

    ProgressDialog progressDialog;
    PropertyDetails propertyDetails;
    ArrayProperties arrayProperties;
    PreviewData previewData;
    UserSession userSession;
    CurrentLocationHistory currentLocationHistory;

    CheckBox btn_add_fav;

    String actionFrom;

    public NearmeAdapter(AppCompatActivity appCompatActivity,Context context,List<NearmeItem> nearmeItemList,String actionFrom) {
        this.appCompatActivity = appCompatActivity;
        this.nearmeItemList = nearmeItemList;
        this.context = context;
        this.progressDialog = new ProgressDialog(appCompatActivity);
        this.propertyDetails = new PropertyDetails(context);
        this.arrayProperties = new ArrayProperties(context);
        this.previewData = new PreviewData(context);
        this.userSession = new UserSession(context);
        this.currentLocationHistory = new CurrentLocationHistory(context);
        this.actionFrom = actionFrom;
    }

    @Override
    public int getCount() {
        return nearmeItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_feedconstr, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        ImageView file_image = (ImageView) convertView.findViewById(R.id.feedimage);
        TextView text_property_name = (TextView) convertView.findViewById(R.id.text_property_name);
        TextView text_address = (TextView) convertView.findViewById(R.id.text_address);
        TextView text_distance = (TextView) convertView.findViewById(R.id.text_distance);
        TextView text_price = (TextView) convertView.findViewById(R.id.text_price);
        TextView text_ratings = (TextView) convertView.findViewById(R.id.text_ratings);
        RatingBar text_ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        ConstraintLayout constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.container);
        btn_add_fav = (CheckBox) convertView.findViewById(R.id.btn_add_fav);

        //set fonts
        Typeface face = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/wregular.ttf");
        Typeface facebold = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/wbold.ttf");
        text_property_name.setTypeface(facebold);
        text_distance.setTypeface(face);
        text_address.setTypeface(face);
        text_price.setTypeface(face);
        text_ratings.setTypeface(face);

        final NearmeItem item = nearmeItemList.get(position);

        Glide.with(context).load(item.getFeedimage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .dontAnimate()
                .into(file_image);

        text_property_name.setText(item.getText_property_name());
        text_distance.setText(item.getText_distance());
        text_address.setText(item.getText_address());
        text_price.setText("From "+item.getText_price()+" "+item.getText_currency());
        text_ratingBar.setRating(Float.parseFloat(item.getRatingBar()));
        text_ratings.setText(item.getRatingBar());
        text_ratingBar.setFocusableInTouchMode(false);
        btn_add_fav.setChecked(item.isFavorite());

        //hide item
        if(Float.parseFloat(item.getRatingBar())==0){
            text_ratingBar.setVisibility(View.GONE);
            text_ratings.setVisibility(View.GONE);
        }


        constraintLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadFeed(item.getProperty_id());
            }
        });

        btn_add_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSession.isLoggedIn()) {
                    if (((CheckBox) view).isChecked()) {
                        Addfavorite(item.getProperty_id(), userSession.getUserId(), "1", userSession.getUserKey());
                        item.setFavorite(true);
                    } else {
                        Addfavorite(item.getProperty_id(), userSession.getUserId(), "0", userSession.getUserKey());
                        item.setFavorite(false);
                    }
                }else {

                    switch (actionFrom){
                        case "nearme":
                            Bundle bundle = new Bundle();
                            bundle.putString("goto", "nearme");

                            FragmentTransaction ft = appCompatActivity.getSupportFragmentManager().beginTransaction();
                            LoginDialogFragment newFragment = LoginDialogFragment.newInstance();
                            newFragment.setArguments(bundle);
                            newFragment.show(ft, "login");
                            break;
                        case "myfavorite":
                            break;
                        case "searchresults":
                            break;
                        case "filter":
                            break;
                    }
                }
            }
        });

        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    private void LoadFeed(final String property_id) {
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,endpoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {

                    progressDialog.hide();
                    try {
                        JSONObject jObject  = new JSONObject(response.toString());
                        JSONObject  propertyDetailsObj = jObject.getJSONObject("property_details");
                        JSONObject  userObj = propertyDetailsObj.getJSONObject("user");

                        //user
                        previewData.setKeyUserId(userObj.getString("user_id"));
                        previewData.setKeyFirstname(userObj.getString("first_name"));
                        previewData.setKeyLastname(userObj.getString("last_name"));
                        previewData.setKeyEmail(userObj.getString("email"));
                        previewData.setKeyAbout(userObj.getString("about"));
                        previewData.setKeyDp("");

                        //general information
                        propertyDetails.setKeyPropertyId(propertyDetailsObj.getString("property_id"));
                        propertyDetails.setKeyPropertyName(propertyDetailsObj.getString("property_name"));
                        propertyDetails.setKeyDescription(propertyDetailsObj.getString("description"));
                        propertyDetails.setKeyPropertyType(propertyDetailsObj.getString("property_type"));
                        propertyDetails.setKeyOfficeType(propertyDetailsObj.getString("office_type"));
                        propertyDetails.setKeyLatitude(propertyDetailsObj.getString("latitude"));
                        propertyDetails.setKeyLongitude(propertyDetailsObj.getString("longitude"));
                        propertyDetails.setKeyAddress(propertyDetailsObj.getString("address"));
                        propertyDetails.setKeyCountryName(propertyDetailsObj.getString("country_name"));
                        propertyDetails.setKeyStateName(propertyDetailsObj.getString("state_name"));
                        propertyDetails.setKeyCityName(propertyDetailsObj.getString("city_name"));
                        propertyDetails.setKeyCurrencyName(propertyDetailsObj.getString("currency_name"));
                        propertyDetails.setKeyRefundType(propertyDetailsObj.getString("refund_type"));
                        propertyDetails.setKeySpaceAccomodates(propertyDetailsObj.getString("space_accomodates_count"));
                        propertyDetails.setKeyPrice(propertyDetailsObj.getString("price"));
                        propertyDetails.setKeyImage(propertyDetailsObj.getString("image"));
                        propertyDetails.setKeyDistance(propertyDetailsObj.getString("distance"));
                        propertyDetails.setKeyRatings(propertyDetailsObj.getString("ratings"));
                        propertyDetails.setKeyFavoured(propertyDetailsObj.getBoolean("favoured"));

                        //photo
                        String  photolist = propertyDetailsObj.getString("photos");
                        arrayProperties.setKeyPhotoUrl(photolist);

                        //preview details
                        //--- amenities------
                        String  amenitieslist = propertyDetailsObj.getString("amenities");
                        arrayProperties.setKeyAmenitiesList(amenitieslist);

                        //--- charges------
                        String  chargeslist = propertyDetailsObj.getString("rening_charges");
                        arrayProperties.setKeyChargesList(chargeslist);

                        //--- reasons------
                        String  reasonlist = propertyDetailsObj.getString("reasons");
                        arrayProperties.setKeyReasonList(reasonlist);

                        //--- rules------
                        String  ruleslist = propertyDetailsObj.getString("rules");
                        arrayProperties.setKeyRulesList(ruleslist);


                        //next
                        Intent intent = new Intent(context, Preview.class);
                        appCompatActivity.startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
                params.put("property_id",property_id);
                params.put("user_id",userSession.getUserId());
                params.put("user_lat",currentLocationHistory.getKeyLatitude());
                params.put("user_lon",currentLocationHistory.getKeyLongitude());
                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }



    private void Addfavorite(final String property_id, final String user_id, final String state, final String key) {
        switch (state){
            case "0":
                progressDialog.setTitle("Removing favorite");
                break;
            case "1":
                progressDialog.setTitle("Adding favorite");
                break;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,favorite_endpoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    progressDialog.hide();

                    try {
                        JSONObject jObject  = new JSONObject(response.toString());
                        JSONObject  statusObj = jObject.getJSONObject("status");
                        JSONObject  stateObj = jObject.getJSONObject("state");

                        if(stateObj.getString("").equals("1")){
                            btn_add_fav.setChecked(true);
                        }else {
                            btn_add_fav.setChecked(false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getMessage());
                VolleyLog.d(TAG, "json: " + error.getMessage());
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("property_id",property_id);
                params.put("user_id",user_id);
                params.put("state",state);
                params.put("key",key);
                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


}
