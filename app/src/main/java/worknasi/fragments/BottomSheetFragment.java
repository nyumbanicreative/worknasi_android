package worknasi.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.preference.ArrayProperties;
import worknasi.preference.CurrentLocationHistory;
import worknasi.preference.Favoritepreference;
import worknasi.preference.PreviewData;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.Preview;
import worknasi.worknasiapp.R;

/**
 * Created by user on 2/19/2018.
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private String TAG = BottomSheetFragment.class.getSimpleName();
    private static final String endpoint = AppConfig.LINK_FETCH_PREVIEW;
    private static final String favorite_endpoint = AppConfig.LINK_FAVORITE;

    ProgressDialog progressDialog;

    PropertyDetails propertyDetails;
    UserSession userSession;
    PreviewData previewData;
    Favoritepreference favoritepreference;
    CurrentLocationHistory currentLocationHistory;
    ArrayProperties arrayProperties;

    Context context;
    String image;
    String property_id;
    String property_name;
    String address;
    String price;
    String currency;
    String refund_type;
    String rating;
    String distance;
    boolean bottom_favorite;

    CheckBox favorite;

    FragmentActivity fragmentActivity;

    //String data collector
    StringBuilder builder_userid      = new StringBuilder();
    StringBuilder builder_propertyid  = new StringBuilder();
    StringBuilder builder_favorite    = new StringBuilder();

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public BottomSheetFragment(Context context,
                               FragmentActivity fragmentActivity,
                               String image,
                               String property_id,
                               String property_name,
                               String address,
                               String price,
                               String currency,
                               String refund_type,
                               String rating,
                               String distance,
                               boolean favorite) {
        //Required empty public constructor
        this.context = context;
        this.fragmentActivity = fragmentActivity;
        this.image = image;
        this.property_id = property_id;
        this.property_name = property_name;
        this.address = address;
        this.price = price;
        this.currency = currency;
        this.refund_type = refund_type;
        this.rating = rating;
        this.distance = distance;
        this.bottom_favorite = favorite;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        userSession = new UserSession(getContext());
        propertyDetails = new PropertyDetails(getContext());
        progressDialog = new ProgressDialog(getActivity());
        previewData = new PreviewData(getContext());
        favoritepreference = new Favoritepreference(getContext());
        currentLocationHistory = new CurrentLocationHistory(getContext());
        arrayProperties = new ArrayProperties(getContext());

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.item_marker_info_cnstr, container, false);
        ImageView file_image = (ImageView) view.findViewById(R.id.image);
        TextView text_property_name = (TextView) view.findViewById(R.id.text_property_name);
        TextView text_address = (TextView) view.findViewById(R.id.text_address);
        TextView text_price = (TextView) view.findViewById(R.id.price);
        TextView text_refund_type = (TextView) view.findViewById(R.id.refund_type);
        RatingBar text_ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        TextView text_distance = (TextView) view.findViewById(R.id.text_distance);
        TextView text_ratings = (TextView) view.findViewById(R.id.text_ratings);
        favorite = (CheckBox) view.findViewById(R.id.btn_add_fav);

        //set fonts
        Typeface face = Typeface.createFromAsset(fragmentActivity.getAssets(), "font/Helvetica.ttf");
        Typeface facebold = Typeface.createFromAsset(fragmentActivity.getAssets(), "font/HelveticaBold.ttf");
        text_property_name.setTypeface(facebold);
        text_address.setTypeface(face);
        text_price.setTypeface(face);
        text_refund_type.setTypeface(face);
        text_ratings.setTypeface(face);
        text_distance.setTypeface(face);

        Glide.with(context).load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .dontAnimate()
                .into(file_image);
        text_property_name.setText(property_name);
        text_address.setText(address);
        text_price.setText(price+" "+currency);
        text_refund_type.setText("From");
        text_ratingBar.setRating(Float.parseFloat(rating));
        text_ratings.setText(rating);
        text_distance.setText(distance);
        favorite.setChecked(bottom_favorite);

        //hide items (offset)
        if(Float.parseFloat(rating)==0){
            text_ratingBar.setVisibility(View.GONE);
            text_ratings.setVisibility(View.GONE);
        }

        file_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadFeed(property_id);
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userSession.isLoggedIn()){
                    if(((CheckBox) view).isChecked()) {
                        Addfavorite(property_id, userSession.getUserId(), userSession.getUserKey(), "1");
                    }else {
                        Addfavorite(property_id, userSession.getUserId(), userSession.getUserKey(), "0");
                    }
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString("goto", "bottomsheet");

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    LoginDialogFragment newFragment = LoginDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "login");
                }

            }
        });

        return view;
    }

    private void Addfavorite(final String property_id, final String user_id, final String key, final String state) {
        switch (state){
            case "0":
                progressDialog.setTitle("Removing favorite");
                //delete sheet favorite
                builder_userid.append(user_id).append("-");
                builder_propertyid.append(property_id).append("-");
                builder_favorite.append("false").append("-");
                favoritepreference.addFavoritePreference(builder_userid.toString(),builder_propertyid.toString(),builder_favorite.toString());
                break;
            case "1":
                progressDialog.setTitle("Adding favorite");
                builder_userid.append(user_id).append("-");
                builder_propertyid.append(property_id).append("-");
                builder_favorite.append("true").append("-");
                favoritepreference.addFavoritePreference(builder_userid.toString(),builder_propertyid.toString(),builder_favorite.toString());
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
                            favorite.setChecked(true);
                            //add sheet favorite
                        }else {
                            favorite.setChecked(false);
                        }

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
                params.put("user_id",user_id);
                params.put("state",state);
                params.put("key",key);
                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
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
                        getActivity().startActivity(intent);

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


        //Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


}