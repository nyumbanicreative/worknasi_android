package worknasi.worknasiapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import worknasi.fragments.LoginDialogFragment;
import worknasi.fragments.PlansDialogFragment;
import worknasi.fragments.Preview_Review;
import worknasi.fragments.Preview_details;
import worknasi.fragments.Preview_map;
import worknasi.fragments.Preview_photo;
import worknasi.helper.CustomTabLayout;
import worknasi.model.Image;
import worknasi.preference.ArrayProperties;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;

public class Preview extends AppCompatActivity {

    private CustomTabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Image> images;

    PropertyDetails propertyDetails;
    ArrayProperties arrayProperties;
    UserSession userSession;

    RelativeLayout booking_layout_proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        booking_layout_proceed = (RelativeLayout) findViewById(R.id.booking_layout_proceed);

        images = new ArrayList<>();
        propertyDetails = new PropertyDetails(getApplicationContext());
        arrayProperties = new ArrayProperties(getApplicationContext());
        userSession = new UserSession(getApplicationContext());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //events
        booking_layout_proceed.setOnClickListener(click_proceed_rent);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Preview_details(Preview.this,getApplicationContext()), "DETAILS");
        adapter.addFragment(new Preview_photo(getApplicationContext(),Preview.this), "PHOTOS");
        adapter.addFragment(new Preview_Review(getApplicationContext(),Preview.this), "REVIEW");
        adapter.addFragment(new Preview_map(getApplicationContext(),Preview.this), "MAP");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //event
    View.OnClickListener click_proceed_rent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(userSession.isLoggedIn()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                PlansDialogFragment newFragment = PlansDialogFragment.newInstance();
                newFragment.show(ft, "booking");
            }else {
                Bundle bundle = new Bundle();
                bundle.putString("goto", "booking");

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                LoginDialogFragment newFragment = LoginDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "login");
            }
        }
    };

}
