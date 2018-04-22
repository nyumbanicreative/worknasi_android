package worknasi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.preference.FcmDeviceKey;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;
import worknasi.worknasiapp.Register;

/**
 * Created by user on 1/3/2018.
 */

public class LoginDialogFragment extends DialogFragment {
    private String TAG = LoginDialogFragment.class.getSimpleName();
    private String LOGIN_URL = AppConfig.LINK_LOGIN;

    UserSession userSession;
    FcmDeviceKey fcmDeviceKey;

    EditText login_edittext_username;
    EditText login_edittext_password;
    TextView register;

    ProgressDialog progressDialog;

    private static LoginDialogFragment dialogFragment;
    public static LoginDialogFragment newInstance() {
        LoginDialogFragment f = new LoginDialogFragment();
        dialogFragment = f;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_login_fragment, container, false);

        userSession =  new UserSession(getContext());
        fcmDeviceKey = new FcmDeviceKey(getContext());

        progressDialog = new ProgressDialog(getActivity());
        login_edittext_username = (EditText) view.findViewById(R.id.login_edittext_username);
        login_edittext_password = (EditText) view.findViewById(R.id.login_edittext_password);
        RelativeLayout login_layout_submit = (RelativeLayout) view.findViewById(R.id.login_layout_submit);
        register = (TextView) view.findViewById(R.id.register);

        //events
        login_layout_submit.setOnClickListener(click_login);
        register.setOnClickListener(click_register);

        return  view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
    }


    View.OnClickListener click_login = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Login();
        }
    };

    View.OnClickListener click_register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(),Register.class);
            getActivity().startActivity(intent);
        }
    };

    //login user
    private void Login() {
        progressDialog.setTitle("Login");
        progressDialog.show();
        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null)
                {
                    progressDialog.hide();
                    Log.d(TAG, response.toString());
                    try {
                        //status
                        JSONObject jObject  = new JSONObject(response);
                        JSONObject  statusObj = jObject.getJSONObject("status");

                        if(statusObj.getBoolean("error")) {

                            //Invalid login
                            JSONObject  form_error = statusObj.getJSONObject("form_errors");

                            if(form_error.has("user_name")) {
                                login_edittext_username.setError(form_error.getString("user_name"));
                            }
                            if(form_error.has("user_password")) {
                                login_edittext_password.setError(form_error.getString("user_password"));
                            }


                        }else {
                            //successful login
                            //create session
                            JSONObject jsonObject_user = jObject.getJSONObject("user");
                            userSession.setUserId(jsonObject_user.getString("id"));
                            userSession.setUserFirstName(jsonObject_user.getString("first_name"));
                            userSession.setUserLastName(jsonObject_user.getString("last_name"));
                            userSession.setUserEmail(jsonObject_user.getString("email"));
                            userSession.setUserPicture(jsonObject_user.getString("profile_image"));
                            userSession.setUserType(jsonObject_user.getString("user_type"));
                            userSession.setUserKey(jsonObject_user.getString("key"));
                            userSession.setUserAbout(jsonObject_user.getString("about"));
                            userSession.LoginUser();

                            switch (getArguments().getString("goto")){

                                case "filter":
                                    /*Intent intent_filter = new Intent(getContext(), Filter.class);
                                    getActivity().startActivity(intent_filter);*/
                                    dialogFragment.dismiss();
                                    break;

                                case "inbox":
                                    /*Intent intent_inbox = new Intent(getContext(), Inbox.class);
                                    getActivity().startActivity(intent_inbox);*/
                                    dialogFragment.dismiss();
                                    break;

                                case "mapactivity":
                                    /*Intent intent_mapactivity = new Intent(getContext(), MapsActivity.class);
                                    getActivity().startActivity(intent_mapactivity);*/
                                    dialogFragment.dismiss();
                                    break;

                                case "myfavorite":
                                    /*Intent intent_myfavorite = new Intent(getContext(), MyFavorite.class);
                                    getActivity().startActivity(intent_myfavorite);*/
                                    dialogFragment.dismiss();
                                    break;

                                case "nearme":
                                    Intent intent_nearme = new Intent(getContext(), Nearme.class);
                                    getActivity().startActivity(intent_nearme);
                                    dialogFragment.dismiss();
                                    break;

                                case "preview":
                                    dialogFragment.dismiss();
                                    break;

                                case "profileuser":
                                    /*Intent intent_profile_user = new Intent(getContext(), Profile_user.class);
                                    getActivity().startActivity(intent_profile_user);*/

                                    dialogFragment.dismiss();
                                    break;

                                case "searchresults":
                                   /* Intent intent_searchresults = new Intent(getContext(), SearchResults.class);
                                    getActivity().startActivity(intent_searchresults);*/
                                    dialogFragment.dismiss();
                                    break;

                                case "booking":
                                    /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    BookingDialogFragment newFragment = BookingDialogFragment.newInstance();
                                    newFragment.show(ft, "slideshow");
                                    dialogFragment.dismiss();*/
                                    break;

                                case "review":
                                    dialogFragment.dismiss();

                                case "bottomsheet":
                                    dialogFragment.dismiss();

                            }
                        }



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
                params.put("user_name",login_edittext_username.getText().toString());
                params.put("user_password",login_edittext_password.getText().toString());
                params.put("firebase_token",fcmDeviceKey.getKeyFcmdevicekey());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

}
