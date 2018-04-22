package worknasi.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import de.hdodenhof.circleimageview.CircleImageView;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.BookedSpace;
import worknasi.worknasiapp.Inbox;
import worknasi.worknasiapp.MyFavorite;
import worknasi.worknasiapp.R;
import worknasi.worknasiapp.ReservedSpaces;

/**
 * Created by user on 2/15/2018.
 */

@SuppressLint("ValidFragment")
public class Profile extends Fragment {
    private static String TAG = Profile.class.getSimpleName();

    CardView mybooking;
    CardView myreserved;
    CardView inbox;
    CardView myfavorite;
    CardView settings;
    CardView logout;
    CircleImageView image_dp;

    UserSession userSession;
    TextView textusername;

    AppCompatActivity appCompatActivity;
    Context context;

    public Profile(AppCompatActivity appCompatActivity,Context context) {
        // Required empty public constructor
        this.appCompatActivity = appCompatActivity;
        this.context = context;
        userSession = new UserSession(context);
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userSession = new UserSession(context);

        mybooking = (CardView) view.findViewById(R.id.card_booking);
        myreserved = (CardView) view.findViewById(R.id.card_reserved);
        inbox = (CardView) view.findViewById(R.id.card_inbox);
        myfavorite = (CardView) view.findViewById(R.id.card_favorites);
        settings = (CardView) view.findViewById(R.id.card_settings);
        logout = (CardView) view.findViewById(R.id.card_logout);
        image_dp = (CircleImageView) view.findViewById(R.id.image_dp);
        textusername = (TextView) view.findViewById(R.id.textusername);

        Glide.with(context).load(userSession.getUserPicture())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.avarta)
                .dontAnimate()
                .into(image_dp);

        textusername.setText(userSession.getFirstName()+" "+userSession.getLastName());

        //swwitch users
        switch (userSession.getKeyUsertype()){
            case "1":
                //admim
                myreserved.setVisibility(View.GONE);
                break;
            case "2":
                //customer
                mybooking.setVisibility(View.GONE);
                break;
        }

        //events
        mybooking.setOnClickListener(click_booking);
        myreserved.setOnClickListener(click_reserved);
        inbox.setOnClickListener(click_inbox);
        myfavorite.setOnClickListener(click_favorite);
        settings.setOnClickListener(click_setting);
        logout.setOnClickListener(click_logout);


        return view;

    }

    View.OnClickListener click_reserved = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,ReservedSpaces.class);
            startActivity(intent);
        }
    };


    View.OnClickListener click_booking = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,BookedSpace.class);
            startActivity(intent);
        }
    };


    View.OnClickListener click_inbox = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,Inbox.class);
            startActivity(intent);
        }
    };

    View.OnClickListener click_favorite = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,MyFavorite.class);
            startActivity(intent);
        }
    };

    View.OnClickListener click_setting = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    View.OnClickListener click_logout = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            userSession.clearSession();
            appCompatActivity.finish();
            Toast.makeText(context,"Your session is cleared successfuly",Toast.LENGTH_LONG).show();
        }
    };

}