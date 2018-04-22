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
import android.support.design.widget.BottomSheetBehavior;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import worknasi.model.LocationItem;
import worknasi.preference.Favoritepreference;
import worknasi.preference.SearchHistory;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;

/**
 * Created by user on 2/19/2018.
 */

public class Result_Map extends Fragment implements OnMapReadyCallback {

    GoogleMap mgoogleMap;
    MapView mapView;
    View mview;
    List<LocationItem> locationItemList;
    SearchHistory searchHistory;
    Favoritepreference favoritepreference;
    UserSession userSession;

    AppCompatActivity appCompatActivity;
    Context context;

    BottomSheetBehavior sheetBehavior;

    public Result_Map() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Result_Map(Context context, AppCompatActivity appCompatActivity, List<LocationItem> locationItemList) {
        // Required empty public constructor
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        this.locationItemList = locationItemList;
        this.searchHistory = new SearchHistory(context);
        this.favoritepreference = new Favoritepreference(context);
        this.userSession = new UserSession(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.result_map, container, false);
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

        for(int i=0; i<locationItemList.size(); i++) {
            LocationItem item = locationItemList.get(i);
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(item.getLatitude()),Double.parseDouble(item.getLongitude()))).title(item.getProperty_name()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_location_map))));
        }

        CameraPosition hm = CameraPosition.builder().target(new LatLng(Double.parseDouble(searchHistory.getKeyLatitude()),Double.parseDouble(searchHistory.getKeyLongitude()))).zoom(12).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(hm));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId().toString().substring(1);
                LocationItem getitem = locationItemList.get(Integer.parseInt(id));

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


}