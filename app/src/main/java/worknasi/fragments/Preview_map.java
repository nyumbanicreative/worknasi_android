package worknasi.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import worknasi.preference.PropertyDetails;
import worknasi.worknasiapp.R;

/**
 * Created by user on 12/31/2017.
 */

public class Preview_map extends Fragment implements OnMapReadyCallback {
    GoogleMap mgoogleMap;
    MapView mapView;
    View mview;
    AppCompatActivity appCompatActivity;
    Context context;
    PropertyDetails propertyDetails;

    public Preview_map() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Preview_map(Context context, AppCompatActivity appCompatActivity) {
        // Required empty public constructor
        this.appCompatActivity = appCompatActivity;
        this.context = context;
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
        mview = inflater.inflate(R.layout.preview_map, container, false);
        return mview;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mview.findViewById(R.id.map);
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mgoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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

        googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(propertyDetails.getKeyLatitude()),Double.parseDouble(propertyDetails.getKeyLongitude()))).title(propertyDetails.getKeyPropertyName()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_location_map))));

        CameraPosition hm = CameraPosition.builder().target(new LatLng(Double.parseDouble(propertyDetails.getKeyLatitude()),Double.parseDouble(propertyDetails.getKeyLongitude()))).zoom(16).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(hm));
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

}
