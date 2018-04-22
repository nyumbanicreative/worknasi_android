package worknasi.worknasiapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import worknasi.fragments.Home;
import worknasi.fragments.LoginDialogFragment;
import worknasi.fragments.Nearme;
import worknasi.fragments.Profile;
import worknasi.preference.UserSession;

public class MainActivity extends AppCompatActivity {

    UserSession userSession;

    private ActionBar toolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new Home(MainActivity.this,getApplicationContext());
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_dashboard:
                    fragment = new Nearme(MainActivity.this,getApplicationContext());
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notifications:
                    if(userSession.isLoggedIn()) {
                        fragment = new Profile(MainActivity.this, getApplicationContext());
                        loadFragment(fragment);
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putString("goto", "profileuser");

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        LoginDialogFragment newFragment = LoginDialogFragment.newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "login");
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Worknasi");

        userSession =  new UserSession(getApplicationContext());
        loadFragment(new Home(MainActivity.this,getApplicationContext()));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
