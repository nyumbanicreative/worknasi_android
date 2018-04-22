package worknasi.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.Fruit;
import worknasi.model.FruitCategory;
import worknasi.preference.ArrayProperties;
import worknasi.preference.PreviewData;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.Chat;
import worknasi.worknasiapp.R;

/**
 * Created by user on 12/31/2017.
 */

public class Preview_details extends Fragment {
    private String TAG = Preview_details.class.getSimpleName();
    private static final String favorite_endpoint = AppConfig.LINK_FAVORITE;

    String[] parents = new String[]{"Amenities","Renting Charges", "Reasons","Rules"};
    ArrayProperties arrayProperties;
    PreviewData previewData;
    PropertyDetails propertyDetails;
    UserSession userSession;

    AppCompatActivity appCompatActivity;
    Context context;

    TextView text_property_name;
    TextView text_address;
    TextView user_name;
    TextView price;
    TextView text_distance;
    TextView from;
    TextView listname;
    TextView listnamechild;
    RatingBar ratingBar;
    CheckBox favorite;
    ImageView inbox;
    TextView description;

    ProgressDialog progressDialog;

    public Preview_details() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Preview_details(AppCompatActivity appCompatActivity, Context context) {
        // Required empty public constructor
        this.appCompatActivity = appCompatActivity;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.preview_details, container, false);

        arrayProperties = new ArrayProperties(context);
        previewData = new PreviewData(context);
        propertyDetails = new PropertyDetails(context);
        userSession = new UserSession(context);

        progressDialog = new ProgressDialog(appCompatActivity);

        text_property_name = (TextView) view.findViewById(R.id.text_property_name);
        text_address = (TextView) view.findViewById(R.id.text_address);
        user_name = (TextView) view.findViewById(R.id.user_name);
        price = (TextView) view.findViewById(R.id.price);
        text_distance = (TextView) view.findViewById(R.id.text_distance);
        from = (TextView) view.findViewById(R.id.from);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        favorite = (CheckBox) view.findViewById(R.id.favorite);
        inbox = (ImageView) view.findViewById(R.id.inbox);
        description = (TextView) view.findViewById(R.id.description);

        //font and style
        final Typeface face = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/wregular.ttf");
        Typeface facebold = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/wbold.ttf");
        text_property_name.setTypeface(facebold);
        text_address.setTypeface(face);
        user_name.setTypeface(face);
        price.setTypeface(face);
        text_distance.setTypeface(face);
        from.setTypeface(face);
        description.setTypeface(face);

        ExpandableLayout sectionLinearLayout = (ExpandableLayout) view.findViewById(R.id.el);

        //set user information
        setUserInformation();

        sectionLinearLayout.setRenderer(new ExpandableLayout.Renderer<FruitCategory, Fruit>() {
            @Override
            public void renderParent(View view, FruitCategory model, boolean isExpanded, int parentPosition) {
                //((TextView) view.findViewById(R.id.tvParent)).setText(model.name);
                listname = (TextView) view.findViewById(R.id.tvParent);
                listname.setTypeface(face);
                listname.setText(model.name);
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);
            }

            @Override
            public void renderChild(View view, Fruit model, int parentPosition, int childPosition) {
                //((TextView) view.findViewById(R.id.tvChild)).setText(model.name);
                listnamechild = (TextView) view.findViewById(R.id.tvChild);
                listnamechild.setTypeface(face);
                listnamechild.setText(model.name);
            }
        });

        if (getAnemitiesSize()>0){
            sectionLinearLayout.addSection(getAmenities());
        }else if(getChargesSize()>0){
            sectionLinearLayout.addSection(getCharges());
        }else  if(getReasonsSize()>0){
            sectionLinearLayout.addSection(getReasons());
        }else if(getRulesSize()>0){
            sectionLinearLayout.addSection(getRules());
        }

        //add fvryt
        favorite.setOnClickListener(click_favorite);
        //open chat
        inbox.setOnClickListener(click_inbox);

        return view;
    }

    private void setUserInformation() {

        text_property_name.setText(propertyDetails.getKeyPropertyName());
        text_address.setText(propertyDetails.getKeyCountryName()+" "+propertyDetails.getKeyStateName());
        user_name.setText(previewData.getKeyFirstname()+" "+previewData.getKeyLastname());
        price.setText(propertyDetails.getKeyPrice()+" "+propertyDetails.getKeyCurrencyName());
        text_distance.setText(propertyDetails.getKeyDistance()+ " Km");
        ratingBar.setRating(Float.parseFloat(propertyDetails.getKeyRatings()));
        favorite.setChecked(propertyDetails.getKeyFavoured());
        description.setText(propertyDetails.getKeyDescription());

    }

    View.OnClickListener click_favorite =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(userSession.isLoggedIn()){
                if(((CheckBox) view).isChecked()) {
                    Addfavorite(propertyDetails.getKeyPropertyId(), userSession.getUserId(), "1");
                }else {
                    Addfavorite(propertyDetails.getKeyPropertyId(), userSession.getUserId(), "0");
                }
            }else {
                Bundle bundle = new Bundle();
                bundle.putString("goto", "preview");

                FragmentTransaction ft = appCompatActivity.getSupportFragmentManager().beginTransaction();
                LoginDialogFragment newFragment = LoginDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "login");
            }

        }
    };


    View.OnClickListener click_inbox = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(userSession.isLoggedIn()){
                //save target id
                Intent intent = new Intent(context,Chat.class);
                intent.putExtra("target_id",previewData.getKeyUserId());
                startActivity(intent);
            }else {
                Bundle bundle = new Bundle();
                bundle.putString("goto", "preview");

                FragmentTransaction ft = appCompatActivity.getSupportFragmentManager().beginTransaction();
                LoginDialogFragment newFragment = LoginDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "login");
            }

        }
    };

    private int getAnemitiesSize(){

        String  amenitieslist = arrayProperties.getKeyAmenitiesList();
        Gson googlejsonAmenities = new Gson();
        ArrayList AmenitiesArrayListGson = googlejsonAmenities.fromJson(amenitieslist,ArrayList.class);

        ArrayList<String> AmenitiesArrayList = new ArrayList<String>();
        AmenitiesArrayList = AmenitiesArrayListGson;
        String[] array_AmenitiesArrayList = AmenitiesArrayList.toArray(new String[AmenitiesArrayList.size()]);
        return array_AmenitiesArrayList.length;
    }


    private int getChargesSize(){

        String  chargeslist = arrayProperties.getKeyChargesList();
        Gson googlejsonCharges = new Gson();
        ArrayList ChargesArrayListGson = googlejsonCharges.fromJson(chargeslist,ArrayList.class);

        ArrayList<String> ChargesArrayList = new ArrayList<String>();
        ChargesArrayList = ChargesArrayListGson;
        String[] array_ChargesArrayList = ChargesArrayList.toArray(new String[ChargesArrayList.size()]);
        return array_ChargesArrayList.length;
    }


    private int getReasonsSize(){

        String  reasonlist = arrayProperties.getKeyReasonList();
        Gson googlejsonReasons = new Gson();
        ArrayList ReasonsArrayListGson = googlejsonReasons.fromJson(reasonlist,ArrayList.class);

        ArrayList<String> ReasonsArrayList = new ArrayList<String>();
        ReasonsArrayList = ReasonsArrayListGson;
        String[] array_ReasonArrayList = ReasonsArrayList.toArray(new String[ReasonsArrayList.size()]);
        return array_ReasonArrayList.length;
    }


    private int getRulesSize(){

        String  ruleslist = arrayProperties.getKeyRulesList();
        Gson googlejsonRules = new Gson();
        ArrayList RulesArrayListGson = googlejsonRules.fromJson(ruleslist,ArrayList.class);

        ArrayList<String> RulesArrayList = new ArrayList<String>();
        RulesArrayList = RulesArrayListGson;
        String[] array_RulesArrayList = RulesArrayList.toArray(new String[RulesArrayList.size()]);
        return array_RulesArrayList.length;
    }


    public Section<FruitCategory, Fruit> getAmenities() {
        Section<FruitCategory, Fruit> section = new Section<>();
        FruitCategory fruitCategory = new FruitCategory(parents[0]);
        section.parent = fruitCategory;

        String  amenitieslist = arrayProperties.getKeyAmenitiesList();
        Gson googlejsonAmenities = new Gson();
        ArrayList AmenitiesArrayListGson = googlejsonAmenities.fromJson(amenitieslist,ArrayList.class);

        ArrayList<String> AmenitiesArrayList = new ArrayList<String>();
        AmenitiesArrayList = AmenitiesArrayListGson;
        String[] array_AmenitiesArrayList = AmenitiesArrayList.toArray(new String[AmenitiesArrayList.size()]);

        for (int i=0; i<array_AmenitiesArrayList.length; i++) {
            section.children.add(new Fruit(array_AmenitiesArrayList[i]));
        }

        section.expanded = false;
        return section;
    }

    public Section<FruitCategory, Fruit> getCharges() {
        Section<FruitCategory, Fruit> section = new Section<>();
        FruitCategory fruitCategory = new FruitCategory(parents[1]);
        section.parent = fruitCategory;

        String  chargeslist = arrayProperties.getKeyChargesList();
        Gson googlejsonCharges = new Gson();
        ArrayList ChargesArrayListGson = googlejsonCharges.fromJson(chargeslist,ArrayList.class);

        ArrayList<String> ChargesArrayList = new ArrayList<String>();
        ChargesArrayList = ChargesArrayListGson;
        String[] array_ChargesArrayList = ChargesArrayList.toArray(new String[ChargesArrayList.size()]);

        for (int i=0; i<array_ChargesArrayList.length; i++) {
            section.children.add(new Fruit(array_ChargesArrayList[i]));
        }

        section.expanded = false;
        return section;
    }

    public Section<FruitCategory, Fruit> getReasons() {
        Section<FruitCategory, Fruit> section = new Section<>();
        FruitCategory fruitCategory = new FruitCategory(parents[2]);
        section.parent = fruitCategory;

        String  reasonlist = arrayProperties.getKeyReasonList();
        Gson googlejsonReasons = new Gson();
        ArrayList ReasonsArrayListGson = googlejsonReasons.fromJson(reasonlist,ArrayList.class);

        ArrayList<String> ReasonsArrayList = new ArrayList<String>();
        ReasonsArrayList = ReasonsArrayListGson;
        String[] array_ReasonArrayList = ReasonsArrayList.toArray(new String[ReasonsArrayList.size()]);


        for (int i=0; i<array_ReasonArrayList.length; i++) {
            section.children.add(new Fruit(array_ReasonArrayList[i]));
        }

        section.expanded = false;
        return section;
    }

    public Section<FruitCategory, Fruit> getRules() {
        Section<FruitCategory, Fruit> section = new Section<>();
        FruitCategory fruitCategory = new FruitCategory(parents[3]);
        section.parent = fruitCategory;

        String  ruleslist = arrayProperties.getKeyRulesList();
        Gson googlejsonRules = new Gson();
        ArrayList RulesArrayListGson = googlejsonRules.fromJson(ruleslist,ArrayList.class);

        ArrayList<String> RulesArrayList = new ArrayList<String>();
        RulesArrayList = RulesArrayListGson;
        String[] array_RulesArrayList = RulesArrayList.toArray(new String[RulesArrayList.size()]);


        for (int i=0; i<array_RulesArrayList.length; i++) {
            section.children.add(new Fruit(array_RulesArrayList[i]));
        }

        section.expanded = false;
        return section;
    }

    private void Addfavorite(final String property_id, final String user_id, final String state) {
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
                        JSONObject jObject  = new JSONObject(response);
                        JSONObject  statusObj = jObject.getJSONObject("status");
                        JSONObject  stateObj = jObject.getJSONObject("state");

                        if(stateObj.getString("").equals("1")){
                            favorite.setChecked(true);
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
                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

}