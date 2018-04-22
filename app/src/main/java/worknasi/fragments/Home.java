package worknasi.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import worknasi.adapter.PlacesAutoCompleteAdapter;
import worknasi.app.AppController;
import worknasi.config.AppConfig;
import worknasi.helper.MyLocation;
import worknasi.model.LocationItem;
import worknasi.model.PlaceSingleTon;
import worknasi.preference.CurrentLocationHistory;
import worknasi.preference.DistanceHistory;
import worknasi.preference.Favoritepreference;
import worknasi.preference.PropertyDetails;
import worknasi.preference.SearchHistory;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;
import worknasi.worknasiapp.SearchResults;

/**
 * Created by user on 2/15/2018.
 */

@SuppressLint("ValidFragment")
public class Home extends Fragment implements OnMapReadyCallback {
    private static String TAG = "CALLBACKTAG";
    String Point_url = AppConfig.LINK_MAP;
    private static final String API_KEY_QUERY_PLACES = AppConfig.QUERY_KEY_AUTOCOMPLETE_PLACES;
    private static final String LINK_KEY_QUERY_PLACES = AppConfig.LINK_KEY_AUTOCOMPLETE_PLACES;
    List<LocationItem> locationItemList;

    LocationManager locationManager;
    DistanceHistory distanceHistory;
    boolean coordinate_state = false;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_NETWORK = 100;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNALSTORAGE = 101;
    public static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 102;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    UserSession userSession;
    CurrentLocationHistory currentLocationHistory;
    SearchHistory searchHistory;
    Favoritepreference favoritepreference;
    PropertyDetails propertyDetails;

    ProgressDialog progressDialog;
    private static boolean isFirstLoadBottomSheet = true;

    AppCompatActivity appCompatActivity;
    Context context;

    GoogleMap mgoogleMap;
    MapView mapView;
    View mview;

    @SuppressLint("ValidFragment")
    public Home(AppCompatActivity appCompatActivity, Context context) {
        // Required empty public constructor
        //initialize preferences
        this.appCompatActivity = appCompatActivity;
        this.context = context;
        userSession = new UserSession(context);
        currentLocationHistory = new CurrentLocationHistory(context);
        searchHistory = new SearchHistory(context);
        distanceHistory = new DistanceHistory(context);
        progressDialog = new ProgressDialog(appCompatActivity);
        favoritepreference = new Favoritepreference(context);
        propertyDetails = new PropertyDetails(context);
        locationItemList = new ArrayList<>();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_home, container, false);
        return mview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mview.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mgoogleMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            appCompatActivity, R.raw.style_json));
            if (!success) {
            }
        } catch (Resources.NotFoundException e) {
        }

        //add permision
        checkLocationPermission();
        load_granted(googleMap);

    }

    private void load_granted(GoogleMap googleMap) {
        if (CheckGpsStatus()) {
            progressDialog.setMessage("Loading.. Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    appCompatActivity.finish();
                }
            });
            progressDialog.show();
            ViewPointsNetwork(googleMap);

        } else {
            prompt_gps_switch();
        }
    }

    private void ViewPointsOptimized(final GoogleMap googleMap){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                if (coordinate_state) {
                } else {
                    progressDialog.hide();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    coordinate_state = true;
                    Log.e("MAP", "lat" + latitude + " long" + longitude);
                    //save current location
                    currentLocationHistory.setKeyLatitudeLongitude(Double.toString(latitude), Double.toString(longitude));
                    //load coordinated
                    loadpoint(latitude, longitude, googleMap);

                }
            }
        };
        MyLocation myLocation = new MyLocation(appCompatActivity);
        myLocation.getLocation(appCompatActivity, locationResult);
    }

    private void ViewPoints(final GoogleMap googleMap) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("MAP", "gps provider");
            if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("MAP", "not added");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e("MAP", "imefikla");
                    if (coordinate_state) {
                    } else {
                        progressDialog.hide();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        coordinate_state = true;
                        Log.e("MAP", "lat" + latitude + " long" + longitude);
                        //save current location
                        currentLocationHistory.setKeyLatitudeLongitude(Double.toString(latitude), Double.toString(longitude));
                        //load coordinated
                        loadpoint(latitude, longitude, googleMap);

                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });

        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            Log.e("MAP", "Network provider");
            if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("MAP", "not added");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e("MAP", "imefikla");
                    if (coordinate_state) {
                    } else {
                        progressDialog.hide();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        coordinate_state = true;
                        Log.e("MAP", "lat" + latitude + " long" + longitude);
                        //save current location
                        currentLocationHistory.setKeyLatitudeLongitude(Double.toString(latitude), Double.toString(longitude));
                        //load coordinated
                        loadpoint(latitude, longitude, googleMap);

                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });

        }
    }


    private void ViewPointsNetwork(final GoogleMap googleMap) {
        Log.e("MAP", "Network provider");
        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MAP", "not added");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("MAP", "imefikla");
                if (coordinate_state) {
                } else {
                    progressDialog.hide();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    coordinate_state = true;
                    Log.e("MAP", "lat" + latitude + " long" + longitude);
                    //save current location
                    currentLocationHistory.setKeyLatitudeLongitude(Double.toString(latitude), Double.toString(longitude));
                    //load coordinated
                    loadpoint(latitude, longitude, googleMap);
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }


    public boolean CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? true : false;
    }

    public void prompt_gps_switch() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(appCompatActivity);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = (appCompatActivity).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_gps, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.hide();
                appCompatActivity.finish();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                appCompatActivity.finish();
            }
        });
        alertDialog.show();
    }

    private void loadpoint(final double lat, final double longt, final GoogleMap gm) {

        final String latitude = new Double(lat).toString().trim();
        final String longitude = new Double(longt).toString().trim();
        final double[] last_lat = new double[1];
        final double[] last_lon = new double[1];

        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //gm.setMyLocationEnabled(true);
        UiSettings uiSettings = gm.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);


        //start a new request
        StringRequest jsonReq = new StringRequest(Request.Method.POST,Point_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response != null)
                {
                    try {

                        //save location
                        currentLocationHistory.setKeyLatitudeLongitude(latitude,longitude);

                        //status
                        JSONObject jsonObject = new JSONObject(response);

                        //check status
                        JSONObject  statusObj = jsonObject.getJSONObject("status");
                        if(statusObj.getBoolean("error")){
                            Toast.makeText(context,statusObj.getString("error_msg"),Toast.LENGTH_LONG).show();
                        }else {
                        }

                        JSONArray feedArray = jsonObject.getJSONArray("properties");

                        for (int i = 0; i < feedArray.length(); i++) {
                            final JSONObject markObj = (JSONObject) feedArray.get(i);

                            LocationItem locationItem = new LocationItem();
                            locationItem.setProperty_id(markObj.getString("property_id"));
                            locationItem.setProperty_name(markObj.getString("property_name"));
                            locationItem.setDescription(markObj.getString("description"));
                            locationItem.setLatitude(markObj.getString("latitude"));
                            locationItem.setLongitude(markObj.getString("longitude"));
                            locationItem.setAddress(markObj.getString("address"));
                            locationItem.setCountry_name(markObj.getString("country_name"));
                            locationItem.setCity_name(markObj.getString("city_name"));
                            locationItem.setState_name(markObj.getString("state_name"));
                            locationItem.setRefund_type(markObj.getString("refund_type"));
                            locationItem.setPrice(markObj.getString("price"));
                            locationItem.setCurrency_name(markObj.getString("currency_name"));
                            locationItem.setImage(markObj.getString("image"));
                            locationItem.setDistance(markObj.getString("distance") + " Km");
                            locationItem.setRatings(markObj.getString("ratings"));
                            locationItem.setFavorite(markObj.getBoolean("favoured"));
                            locationItemList.add(locationItem);

                            last_lat[0] = new Double(markObj.getString("latitude").trim()).doubleValue();
                            last_lon[0] = new Double(markObj.getString("longitude").trim()).doubleValue();

                            gm.addMarker(new MarkerOptions().position(new LatLng(new Double(markObj.getString("latitude").trim()).doubleValue(),new Double(markObj.getString("longitude").trim()).doubleValue())).title(locationItem.getProperty_name()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_location_map))));
                        }
                        //gm.addMarker(new MarkerOptions().position(new LatLng(lat,longt)).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context,R.drawable.ic_my_location_black_24dp))));

                        CameraPosition hm = CameraPosition.builder().target(new LatLng(lat,longt)).zoom(12).build();
                        gm.moveCamera(CameraUpdateFactory.newCameraPosition(hm));

                        gm.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                String id = marker.getId().toString().substring(1);
                                LocationItem getitem = locationItemList.get(Integer.parseInt(id));

                                //assign property details
                                propertyDetails.setKeyPropertyId(getitem.getProperty_id());
                                propertyDetails.setKeyPropertyName(getitem.getProperty_name());
                                propertyDetails.setKeyDescription(getitem.getDescription());
                                propertyDetails.setKeyPropertyType("");
                                propertyDetails.setKeyOfficeType("");
                                propertyDetails.setKeyLatitude(getitem.getLatitude());
                                propertyDetails.setKeyLongitude(getitem.getLongitude());
                                propertyDetails.setKeyAddress(getitem.getAddress());
                                propertyDetails.setKeyCountryName(getitem.getCountry_name());
                                propertyDetails.setKeyStateName(getitem.getState_name());
                                propertyDetails.setKeyCityName(getitem.getCity_name());
                                propertyDetails.setKeyCurrencyName(getitem.getCurrency_name());
                                propertyDetails.setKeyRefundType(getitem.getRefund_type());
                                propertyDetails.setKeySpaceAccomodates(getitem.getSpace_accomodates_count());
                                propertyDetails.setKeyPrice(getitem.getPrice());
                                propertyDetails.setKeyImage(getitem.getImage());
                                propertyDetails.setKeyDistance(getitem.getDistance());
                                propertyDetails.setKeyRatings(getitem.getRatings());
                                propertyDetails.setKeyFavoured(getitem.isFavorite());

                                //check if item exist
                                for(int i=0; i<favoritepreference.getKeyitemSize(); i++){

                                    if(userSession.getUserId().equals(favoritepreference.getKeyUserId(i)) && getitem.getProperty_id().equals(favoritepreference.getKeyPropertyId(i)) ){
                                        showBottomSheetDialogFragment(
                                                getitem.getImage(),
                                                getitem.getProperty_id(),
                                                getitem.getProperty_name(),
                                                getitem.getAddress(),
                                                getitem.getPrice(),
                                                getitem.getCurrency_name(),
                                                getitem.getRefund_type(),
                                                getitem.getRatings(),
                                                getitem.getDistance(),
                                                Boolean.valueOf(favoritepreference.getKeyFavorite(i)));
                                        break;
                                    }else {
                                        showBottomSheetDialogFragment(
                                                getitem.getImage(),
                                                getitem.getProperty_id(),
                                                getitem.getProperty_name(),
                                                getitem.getAddress(),
                                                getitem.getPrice(),
                                                getitem.getCurrency_name(),
                                                getitem.getRefund_type(),
                                                getitem.getRatings(),
                                                getitem.getDistance(),
                                                getitem.isFavorite());
                                    }

                                }

                                return false;
                            }
                        });


                    }catch (JSONException e) {e.printStackTrace();}

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                /*if (error instanceof NetworkError) {
                    Toast.makeText(context,"Oops. Network error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context,"Oops. Server error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context,"Oops. Auth error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context,"Oops. Parse error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(context,"Oops. Connection error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context,"Oops. Timeout error!",Toast.LENGTH_LONG).show();
                }*/
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("req_lat", latitude);
                params.put("req_lon", longitude);
                params.put("distance", distanceHistory.getDistance());
                params.put("user_id", userSession.getUserId());
                params.put("near_me_type", "map");
                params.put("offset", "0");

                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(appCompatActivity);
            // ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = (appCompatActivity).getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.item_search, null);
            dialogBuilder.setView(dialogView);
            final AutoCompleteTextView autoCompView = (AutoCompleteTextView) dialogView.findViewById(R.id.autoCompleteTextView);
            autoCompView.setAdapter(new PlacesAutoCompleteAdapter(appCompatActivity, R.layout.item_googleplaces));
            autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                    String str = (String) parent.getItemAtPosition(i);
                    getGeometry(PlaceSingleTon.getInstance().getPlacesId(i));
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

            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getGeometry(String place_id) {
        progressDialog.setTitle("Searching");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                LINK_KEY_QUERY_PLACES+"?placeid="+place_id+"&key="+API_KEY_QUERY_PLACES
                , null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    progressDialog.dismiss();
                    try {
                        JSONObject result = new JSONObject(response.toString());
                        JSONObject  resultObj = result.getJSONObject("result");
                        JSONObject  geometry = resultObj.getJSONObject("geometry");
                        JSONObject  location = geometry.getJSONObject("location");
                        searchHistory.setKeyLatitudeLongitude(location.getString("lat"),location.getString("lng"));
                        Intent intent = new Intent(context,SearchResults.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                /*if (error instanceof NetworkError) {
                    Toast.makeText(context,"Oops. Network error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context,"Oops. Server error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context,"Oops. Auth error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context,"Oops. Parse error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(context,"Oops. Connection error!",Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context,"Oops. Timeout error!",Toast.LENGTH_LONG).show();
                }*/
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
        View customMarkerView = ((LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_custom_marker, null);
        TextView textView = (TextView) customMarkerView.findViewById(R.id.text_space_counter);
        textView.setText("");
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void showBottomSheetDialogFragment(String image,
                                              String property_id,
                                              String property_name,
                                              String address,
                                              String price,
                                              String currency,
                                              String refund_type,
                                              String rating,
                                              String distance,
                                              boolean favorite) {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(context,appCompatActivity,image,property_id,property_name,address,price,currency,refund_type,rating,distance,favorite);
        bottomSheetFragment.show(appCompatActivity.getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    //check permissions
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(appCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(appCompatActivity)
                        .setTitle("Location Permission")
                        .setMessage("Allow worknasi to access your location")
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.e("UTATA","IMEITWA HAPA4");
                                //Prompt the user once explanation has been shown
                               requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                Log.e("UTATA","IMEITWA HAPA3");
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                Log.e("UTATA","FK");
            }
            return false;
        } else {
            return true;
        }
    }


    public boolean checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(appCompatActivity,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity,
                    Manifest.permission.INTERNET)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(appCompatActivity)
                        .setTitle("Location Permission")
                        .setMessage("Allow worknasi to access Internet")
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(appCompatActivity,
                                        new String[]{Manifest.permission.INTERNET},
                                        MY_PERMISSIONS_REQUEST_NETWORK);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_NETWORK);
            }
            return false;
        } else {
            return true;
        }
    }


    public boolean checkExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(appCompatActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(appCompatActivity)
                        .setTitle("Location Permission")
                        .setMessage("Allow worknasi to access External Storage")
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(appCompatActivity,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_EXTERNALSTORAGE);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNALSTORAGE);
            }
            return false;
        } else {
            return true;
        }
    }


    public boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(appCompatActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(appCompatActivity)
                        .setTitle("Location Permission")
                        .setMessage("Allow worknasi to access Coarse Location")
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(appCompatActivity,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults) {
        Log.e("UTATA","IMEITWA HAPA1");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Log.e("UTATA","IMEITWA HAPA2");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    load_granted(mgoogleMap);Log.e("UTATA","MARA NYINGINE");
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(appCompatActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }else if(ContextCompat.checkSelfPermission(appCompatActivity,
                            Manifest.permission.INTERNET)
                            == PackageManager.PERMISSION_GRANTED){

                    }else if(ContextCompat.checkSelfPermission(appCompatActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){

                    }else if(ContextCompat.checkSelfPermission(appCompatActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED){

                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    //----------------------------------------------------------------------------------------------------------
    /**
     * Google api callback methods
     */

}