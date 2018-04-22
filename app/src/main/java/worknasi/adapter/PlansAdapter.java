package worknasi.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.PlanItem;
import worknasi.preference.CreatePlan;
import worknasi.preference.CreateRent;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;
import worknasi.worknasiapp.RentSpace;

/**
 * Created by user on 1/25/2018.
 */

public class PlansAdapter extends BaseAdapter {
    private String TAG = PlansAdapter.class.getSimpleName();
    private static String URL_RENTSPACE = AppConfig.LINK_RENT;

    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    List<PlanItem> planItemList;
    Context context;
    Activity activity;

    CreateRent createRent;
    CreatePlan createPlan;
    PropertyDetails propertyDetails;
    UserSession userSession;

    ProgressDialog progressDialog;

    public PlansAdapter(Activity activity,Context context,List<PlanItem> planItemList) {
        this.planItemList = planItemList;
        this.activity = activity;
        this.context = context;
        this.createRent = new CreateRent(context);
        this.createPlan = new CreatePlan(context);
        this.propertyDetails = new PropertyDetails(context);
        this.progressDialog = new ProgressDialog(activity);
        this.userSession = new UserSession(context);
    }

    @Override
    public int getCount() {
        return planItemList.size();
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
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_plans, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        Button button_book = (Button) convertView.findViewById(R.id.button_book);
        TextView description = (TextView) convertView.findViewById(R.id.text_plans);

        //set fonts
        Typeface face = Typeface.createFromAsset(activity.getAssets(), "font/wregular.ttf");
        Typeface facebold = Typeface.createFromAsset(activity.getAssets(), "font/wbold.ttf");
        description.setTypeface(face);

        final PlanItem planItem = planItemList.get(position);
        description.setText(planItem.getPlan_description());

        button_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRent.setKeyPropertyId(propertyDetails.getKeyPropertyId());
                createRent.setKeyPropertyName(propertyDetails.getKeyPropertyName());
                createRent.setKeyPlanId(Integer.toString(planItem.getPlan_id()));
                createRent.setKeyPlanDescription(planItem.getPlan_description());
                Rent(Integer.toString(planItem.getPlan_id()));
            }
        });

        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }


    private void Rent(final String plan_id) {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        progressDialog.show();

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_RENTSPACE, new Response.Listener<String>() {

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
                        JSONObject  statusObj = jsonObject.getJSONObject("status");

                        if(statusObj.getBoolean("error")){
                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
                        }else {
                            JSONObject jsonObject_plan = jsonObject.getJSONObject("plan");

                            //save plan
                            createPlan.setKeyPeoplePerDuration(jsonObject_plan.getString("people_per_duration"));
                            createPlan.setKeyAmount(jsonObject_plan.getString("amount"));
                            createPlan.setKeyDurationType(jsonObject_plan.getString("duration_type"));
                            createPlan.setKeySpaceCount(jsonObject_plan.getString("space_count"));
                            createPlan.setKeyMinPeople(jsonObject_plan.getString("min_people"));
                            createPlan.setKeyPropertyName(jsonObject_plan.getString("property_name"));
                            createPlan.setKeyPlanTypeName(jsonObject_plan.getString("plan_type_name"));
                            createPlan.setKeyCurrencyName(jsonObject_plan.getString("currency_name"));
                            createPlan.setKeyPlanDescription(jsonObject_plan.getString("plan_description"));

                            Intent intent = new Intent(context, RentSpace.class);
                            activity.startActivity(intent);
                        }

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
                params.put("user_id", userSession.getUserId());
                params.put("key", userSession.getUserKey());
                params.put("plan_id", plan_id);

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

}