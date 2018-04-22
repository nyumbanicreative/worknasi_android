package worknasi.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

import worknasi.adapter.PlansAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.PlanItem;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;

/**
 * Created by user on 1/25/2018.
 */

public class PlansDialogFragment extends DialogFragment{
    private String TAG = PlansDialogFragment.class.getSimpleName();
    private String PLANS_URL = AppConfig.LINK_PLANS;

    private ListView listView;
    private PlansAdapter plansAdapter;
    private List<PlanItem> planItemList;
    private TextView text_loading;
    private TextView heading;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;
    private static int offsetsize = 0;

    UserSession userSession;
    PropertyDetails propertyDetails;

    private static PlansDialogFragment dialogFragment;
    public static PlansDialogFragment newInstance() {
        PlansDialogFragment f = new PlansDialogFragment();
        dialogFragment = f;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_plans, container, false);

        userSession = new UserSession(getContext());
        propertyDetails = new PropertyDetails(getContext());

        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = true;
            mRefreshLayout.autoRefresh();
        }

        planItemList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listfeed);
        text_loading = (TextView) view.findViewById(R.id.text_loading);
        heading = (TextView) view.findViewById(R.id.heading);

        //set fonts
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "font/wregular.ttf");
        Typeface facebold = Typeface.createFromAsset(getActivity().getAssets(), "font/wbold.ttf");
        heading.setTypeface(facebold);
        text_loading.setTypeface(face);

        plansAdapter = new PlansAdapter(getActivity(),getContext(),planItemList);
        listView.setAdapter(plansAdapter);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                offsetsize = 0;
                LoadFeed();
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                offsetsize = offsetsize+5;
                LoadFeed();
                mRefreshLayout.finishLoadmore();
            }
        });

        return  view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
    }

    private void LoadFeed() {
        final String offset = Integer.toString(offsetsize);

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,PLANS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    text_loading.setVisibility(View.GONE);
                    mRefreshLayout.finishRefresh();
                    Log.d(TAG, PLANS_URL);
                    try {
                        //status
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray feedArray = jsonObject.getJSONArray("plans");
                        if(offsetsize<1) {
                            planItemList.clear();
                        }

                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject feedObj = (JSONObject) feedArray.get(i);

                            PlanItem item = new PlanItem();
                            item.setPlan_id(feedObj.getInt("plan_id"));
                            item.setPlan_description(feedObj.getString("plan_description"));
                            planItemList.add(item);

                        }
                        // notify data changes to list adapater
                        plansAdapter.notifyDataSetChanged();

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
                params.put("property_id", propertyDetails.getKeyPropertyId());
                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

}
