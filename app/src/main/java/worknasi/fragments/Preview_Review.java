package worknasi.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.android.volley.toolbox.StringRequest;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import worknasi.adapter.ReviewAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.ItemReview;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;

/**
 * Created by user on 1/4/2018.
 */

public class Preview_Review extends Fragment {
    private static String TAG = Preview_Review.class.getSimpleName();
    private static String URL_REVIEW = AppConfig.LINK_REVIEW;
    private static String URL_SENDREVIEW = AppConfig.LINK_SENDREVIEW;

    private ListView listView;
    private ReviewAdapter reviewAdapter;
    private List<ItemReview> itemReviewList;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;
    private static int offsetsize = 0;

    AppCompatActivity appCompatActivity;
    Context context;

    UserSession userSession;
    PropertyDetails propertyDetails;

    ImageView sendreview;
    EditText editreview;

    public Preview_Review() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Preview_Review(Context context, AppCompatActivity appCompatActivity) {
        // Required empty public constructor
        this.appCompatActivity = appCompatActivity;
        this.context = context;
        this.userSession = new UserSession(context);
        this.propertyDetails = new PropertyDetails(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.preview_review, container, false);

        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = true;
            mRefreshLayout.autoRefresh();
        }

        itemReviewList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listfeed);
        sendreview = (ImageView) view.findViewById(R.id.sendreview);
        editreview = (EditText) view.findViewById(R.id.editreview);
        reviewAdapter = new ReviewAdapter(appCompatActivity,context,itemReviewList);
        listView.setAdapter(reviewAdapter);

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

        sendreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSession.isLoggedIn()) {
                    addReview(editreview.getText().toString());
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("goto", "review");

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    LoginDialogFragment newFragment = LoginDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "login");
                }
            }
        });

        return view;

    }

    private void LoadFeed(final int offsetsize) {
        final String offset = Integer.toString(offsetsize);

        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_REVIEW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    mRefreshLayout.finishRefresh();
                    parseJsonFeed(response,offsetsize);

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
                params.put("offset", offset);
                params.put("property_id", propertyDetails.getKeyPropertyId());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(String response,int offset) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray feedArray = jsonObject.getJSONArray("reviews");

            if(offset<1) {
                itemReviewList.clear();
            }

            for (int i = 0; i < feedArray.length(); i++) {

                JSONObject feedObj = (JSONObject) feedArray.get(i);

                ItemReview item = new ItemReview();
                item.setReview_id(feedObj.getString("review_id"));
                item.setUser_id(feedObj.getString("user_id"));
                item.setUser_fullname(feedObj.getString("user_full_name"));
                item.setUser_profile_image(feedObj.getString("user_profile_image"));
                item.setReview_content(feedObj.getString("review_content"));
                item.setReview_time(feedObj.getString("review_time"));
                item.setRatings(feedObj.getString("ratings"));
                itemReviewList.add(item);

            }

            // notify data changes to list adapater
            reviewAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void Sendreview(final String content, final String rate) {
        final ProgressDialog progressDialog = new ProgressDialog(appCompatActivity);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_SENDREVIEW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                    editreview.getText().clear();
                    ItemReview item = new ItemReview();
                    item.setUser_id(userSession.getUserId());
                    item.setUser_fullname(userSession.getFirstName()+" "+userSession.getLastName());
                    item.setUser_profile_image(userSession.getUserPicture());
                    item.setReview_content(content);
                    item.setReview_time(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date()));
                    item.setRatings(rate);
                    itemReviewList.add(item);
                    reviewAdapter.notifyDataSetChanged();
                    listView.setSelection(reviewAdapter.getCount()-1);
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
                params.put("content", content);
                params.put("rating_value", rate);
                params.put("property_id", propertyDetails.getKeyPropertyId());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


    public void addReview(final String content){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(appCompatActivity);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = (appCompatActivity).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_addrate, null);
        dialogBuilder.setView(dialogView);

        final float[] rate_val = {0};
        RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        final TextView title = (TextView) dialogView.findViewById(R.id.title);

        Typeface face = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/wregular.ttf");
        title.setTypeface(face);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rate_val[0] = v;
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
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //SEND
                alertDialog.hide();
                Sendreview(content,Float.toString(rate_val[0]));
            }
        });
        alertDialog.show();

    }

}