package worknasi.worknasiapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.model.GetPlan;
import worknasi.preference.CreatePlan;
import worknasi.preference.CreateRent;
import worknasi.preference.UserSession;

public class RentSpace extends AppCompatActivity {

    private String TAG = RentSpace.class.getSimpleName();
    private static String URL_SUBMITRENT = AppConfig.LINK_SUBMITRENT;
    TextView booking_text_spacename;
    TextView booking_text_plan;
    TextView booking_text_date;
    TextView booking_text_time;
    TextView booking_text_duration;

    EditText booking_edittext_numberofpeople;
    EditText booking_edittext_duration;

    LinearLayout booking_layout_date;
    LinearLayout booking_layout_time;
    RelativeLayout booking_layout_submit;

    CreateRent createRent;
    CreatePlan createPlan;
    UserSession userSession;
    GetPlan getPlan;

    private Calendar calendar;
    private int year, month, day;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_space);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createRent = new CreateRent(getApplicationContext());
        createPlan = new CreatePlan(getApplicationContext());
        userSession = new UserSession(getApplicationContext());
        progressDialog = new ProgressDialog(RentSpace.this);
        getPlan = new GetPlan();

        booking_text_spacename = (TextView) findViewById(R.id.booking_text_spacename);
        booking_text_plan = (TextView) findViewById(R.id.booking_text_plan);
        booking_text_date = (TextView) findViewById(R.id.booking_text_date);
        booking_text_time = (TextView) findViewById(R.id.booking_text_time);
        booking_text_duration = (TextView) findViewById(R.id.booking_text_duration);

        booking_edittext_numberofpeople = (EditText) findViewById(R.id.booking_edittext_numberofpeople);
        booking_edittext_duration = (EditText) findViewById(R.id.booking_edittext_duration);

        booking_layout_date = (LinearLayout) findViewById(R.id.booking_layout_date);
        booking_layout_time = (LinearLayout) findViewById(R.id.booking_layout_time);
        booking_layout_submit = (RelativeLayout) findViewById(R.id.booking_layout_submit);

        //set preloaded data
        booking_text_spacename.setText(createRent.getKeyPropertyName());
        booking_text_plan.setText(createRent.getKeyPlanDescription());
        booking_text_duration.setText("DURATION ("+createPlan.getKeyDurationType()+")");
        getPlan.setPlan_id(createRent.getKeyPlanId());
        getPlan.setPlan_description(createRent.getKeyPlanDescription());
        getPlan.setProperty_id(createRent.getKeyPropertyName());
        getPlan.setProperty_name(createRent.getKeyPropertyName());



        //date & time picker
        booking_layout_date.setOnClickListener(click_pick_date);
        booking_layout_time.setOnClickListener(click_pick_time);

        //submit data
        booking_layout_submit.setOnClickListener(click_submit);

    }

    View.OnClickListener click_pick_date = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDialog(999);
        }
    };

    View.OnClickListener click_pick_time = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //TODO Auto-generated method stub
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(RentSpace.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    booking_text_time.setText( selectedHour + ":" + selectedMinute);
                    getPlan.setStart_time(selectedHour + ":" + selectedMinute);
                    booking_text_time.setTextColor(Color.BLACK);
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        }
    };


    View.OnClickListener click_submit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //set no of people & duration
            getPlan.setNumber_of_people(booking_edittext_numberofpeople.getText().toString());
            getPlan.setDuration(booking_edittext_duration.getText().toString());

            SubmitRent(createRent.getKeyPlanId(),getPlan.getStart_date(),getPlan.getStart_time(),getPlan.getDuration(),getPlan.getNumber_of_people());
        }
    };


    //date dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            DatePickerDialog pickerDialog = new DatePickerDialog(this,myDateListener, year, month, day);
            //pickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime()+180);
            pickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());
            return pickerDialog;
            //return new DatePickerDialog(this,myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        String getDate = new StringBuilder().append(day).append("/").append(month).append("/").append(year).toString();
        booking_text_date.setText(getDate);
        booking_text_date.setTextColor(getResources().getColor(R.color.colorBlack));
        getPlan.setStart_date(getDate);
    }

    private void SubmitRent(final String plan_id, final String start_date, final String start_hour, final String duration, final String no_of_people) {
        //Toast.makeText(getApplicationContext(),plan_id+"\n"+start_date+"\n"+start_hour+"\n"+duration+"\n"+no_of_people+"\n",Toast.LENGTH_LONG).show();
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
        StringRequest jsonReq = new StringRequest(Request.Method.POST,URL_SUBMITRENT, new Response.Listener<String>() {

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
                            //Invalid login
                            JSONObject  form_error = statusObj.getJSONObject("form_errors");

                            if(form_error.has("start_date")) {
                                booking_text_date.setText("Date required");
                                booking_text_date.setTextColor(Color.RED);
                            }
                            if(form_error.has("start_hour")) {
                                booking_text_time.setText("Time required");
                                booking_text_time.setTextColor(Color.RED);
                            }
                            if(form_error.has("duration")) {
                                booking_edittext_duration.setError(form_error.getString("duration"));
                            }
                            if(form_error.has("no_of_people")) {
                                booking_edittext_numberofpeople.setError(form_error.getString("no_of_people"));
                            }

                        }else {
                            JSONObject jsonObject_plan = jsonObject.getJSONObject("plan");
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
                params.put("start_date", start_date);
                params.put("start_hour", start_hour);
                params.put("duration", duration);
                params.put("no_of_people", no_of_people);

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

}
