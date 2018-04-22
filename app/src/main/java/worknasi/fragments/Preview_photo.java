package worknasi.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.ArrayList;

import worknasi.adapter.GalleryAdapter;
import worknasi.config.AppConfig;
import worknasi.model.Image;
import worknasi.preference.ArrayProperties;
import worknasi.preference.PropertyDetails;
import worknasi.worknasiapp.R;

/**
 * Created by user on 12/31/2017.
 */

public class Preview_photo extends Fragment {

    AppCompatActivity appCompatActivity;
    Context context;

    private String TAG = Preview_photo.class.getSimpleName();
    private static final String endpoint = AppConfig.LINK_FETCH_PREVIEW;
    private ArrayList<Image> images;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;

    ArrayProperties arrayProperties;
    PropertyDetails propertyDetails;

    public Preview_photo() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Preview_photo(Context context, AppCompatActivity appCompatActivity) {
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
        View view =  inflater.inflate(R.layout.preview_photo, container, false);

        arrayProperties = new ArrayProperties(context);
        propertyDetails = new PropertyDetails(context);

        images = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new GalleryAdapter(context, images);

        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = true;
            mRefreshLayout.autoRefresh();
        }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                fetchImages();
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                fetchImages();
                mRefreshLayout.finishLoadmore();
            }
        });

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(context, recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = appCompatActivity.getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    private void fetchImages() {
    mRefreshLayout.finishRefresh();
        images.clear();

        String  photolist = arrayProperties.getKeyPhotoUrl();
        Gson googlejsonPhotoUrl = new Gson();
        ArrayList PhotoArrayListGson = googlejsonPhotoUrl.fromJson(photolist,ArrayList.class);

        ArrayList<String> photolist_url = new ArrayList<String>();
        photolist_url = PhotoArrayListGson;
        String[] array_photo_url = photolist_url.toArray(new String[photolist_url.size()]);

        for (int i = 0; i < array_photo_url.length; i++) {
                Image image = new Image();
                image.setName(propertyDetails.getKeyPropertyName());
                image.setSmall(array_photo_url[i]);
                image.setMedium(array_photo_url[i]);
                image.setLarge(array_photo_url[i]);
                image.setTimestamp(propertyDetails.getKeyOfficeType());
                images.add(image);
        }
        mAdapter.notifyDataSetChanged();
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
