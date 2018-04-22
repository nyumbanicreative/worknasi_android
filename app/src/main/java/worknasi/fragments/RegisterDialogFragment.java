package worknasi.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.preference.FcmDeviceKey;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;

/**
 * Created by user on 2/22/2018.
 */

public class RegisterDialogFragment extends DialogFragment {
    private String TAG = LoginDialogFragment.class.getSimpleName();
    private String REGISTER_URL = AppConfig.LINK_REGISTER;

    UserSession userSession;
    FcmDeviceKey fcmDeviceKey;

    EditText register_edittext_username;
    EditText register_edittext_email;
    EditText register_edittext_password;
    EditText register_edittext_confirmpassword;

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
        register_edittext_username = (EditText) view.findViewById(R.id.login_edittext_username);
        register_edittext_email = (EditText) view.findViewById(R.id.login_edittext_email);
        register_edittext_password = (EditText) view.findViewById(R.id.login_edittext_password);
        register_edittext_confirmpassword = (EditText) view.findViewById(R.id.login_edittext_confirmpassword);
        RelativeLayout login_layout_submit = (RelativeLayout) view.findViewById(R.id.login_layout_submit);

        //events
        login_layout_submit.setOnClickListener(click_login);

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
            Register();
        }
    };

    //login user
    private void Register() {
        progressDialog.setTitle("Login");
        progressDialog.show();
        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,REGISTER_URL, new Response.Listener<String>() {

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
                                register_edittext_username.setError(form_error.getString("user_name"));
                            }
                            if(form_error.has("user_email")) {
                                register_edittext_username.setError(form_error.getString("user_email"));
                            }
                            if(form_error.has("user_password")) {
                                register_edittext_password.setError(form_error.getString("user_password"));
                            }
                            if(form_error.has("user_confirmpassword")) {
                                register_edittext_password.setError(form_error.getString("user_confirmpassword"));
                            }


                        }else {
                            //successful login
                            //create session
                            dialogFragment.dismiss();
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
                params.put("user_name",register_edittext_username.getText().toString());
                params.put("user_email",register_edittext_email.getText().toString());
                params.put("user_password",register_edittext_password.getText().toString());
                params.put("user_confirmpassword",register_edittext_confirmpassword.getText().toString());

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

}