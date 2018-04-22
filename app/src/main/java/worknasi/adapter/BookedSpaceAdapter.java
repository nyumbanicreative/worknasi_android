package worknasi.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import worknasi.app.AppController;
import worknasi.model.ItemReservedSpace;
import worknasi.preference.PropertyDetails;
import worknasi.preference.UserSession;
import worknasi.worknasiapp.R;

/**
 * Created by user on 2/4/2018.
 */

public class BookedSpaceAdapter extends BaseAdapter {
    private String TAG = BookedSpaceAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    List<ItemReservedSpace> reservedSpaceList;
    Context context;
    Activity activity;

    PropertyDetails propertyDetails;
    UserSession userSession;

    ProgressDialog progressDialog;

    public BookedSpaceAdapter(Activity activity,Context context,List<ItemReservedSpace> reservedSpaceList) {
        this.reservedSpaceList = reservedSpaceList;
        this.activity = activity;
        this.context = context;
        this.propertyDetails = new PropertyDetails(context);
        this.progressDialog = new ProgressDialog(activity);
        this.userSession = new UserSession(context);
    }

    @Override
    public int getCount() {
        return reservedSpaceList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_reservedspace, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView property_name = (TextView) convertView.findViewById(R.id.property_name);

        TextView uibooking_date = (TextView) convertView.findViewById(R.id.uibooking_date);
        TextView booking_date = (TextView) convertView.findViewById(R.id.booking_date);

        TextView uicheck_in_date = (TextView) convertView.findViewById(R.id.uicheck_in_date);
        TextView check_in_date = (TextView) convertView.findViewById(R.id.check_in_date);

        TextView uicheck_out_date = (TextView) convertView.findViewById(R.id.uicheck_out_date);
        TextView check_out_date = (TextView) convertView.findViewById(R.id.check_out_date);

        TextView uinumber_of_people = (TextView) convertView.findViewById(R.id.uino_of_people);
        TextView number_of_people = (TextView) convertView.findViewById(R.id.no_of_people);

        TextView uieffective_amount = (TextView) convertView.findViewById(R.id.uieffective_amount);
        TextView effective_amount = (TextView) convertView.findViewById(R.id.effective_amount);

        TextView uistatus = (TextView) convertView.findViewById(R.id.uistatus);
        TextView status = (TextView) convertView.findViewById(R.id.status);

        CardView pay = (CardView) convertView.findViewById(R.id.pay);

        TextView uiadmin_name = (TextView) convertView.findViewById(R.id.uiadmin_name) ;
        TextView admin_name = (TextView) convertView.findViewById(R.id.admin_name) ;

        CircleImageView admin_profile_image = (CircleImageView) convertView.findViewById(R.id.admin_profile_image);
        ImageView admin_chat = (ImageView) convertView.findViewById(R.id.admin_chat);
        ImageView admin_space_preview = (ImageView) convertView.findViewById(R.id.admin_space_preview);

        TextView uiadmin_email = (TextView) convertView.findViewById(R.id.uiadmin_email);
        TextView admin_email = (TextView) convertView.findViewById(R.id.admin_email);

        TextView uicustomer_name = (TextView) convertView.findViewById(R.id.uicustomer_name);
        TextView customer_name = (TextView) convertView.findViewById(R.id.customer_name);

        TextView uicustomer_email = (TextView) convertView.findViewById(R.id.uicustomer_email);
        TextView customer_email = (TextView) convertView.findViewById(R.id.customer_email);

        //set fonts
        Typeface face = Typeface.createFromAsset(activity.getAssets(), "font/wregular.ttf");
        Typeface facebold = Typeface.createFromAsset(activity.getAssets(), "font/wbold.ttf");

        property_name.setTypeface(facebold);
        check_in_date.setTypeface(face);
        check_out_date.setTypeface(face);
        number_of_people.setTypeface(face);
        effective_amount.setTypeface(face);
        status.setTypeface(face);
        admin_name.setTypeface(facebold);
        admin_email.setTypeface(facebold);
        customer_name.setTypeface(facebold);
        customer_email.setTypeface(facebold);

        //--------------------------------------  UI ------------------------------------------
        uibooking_date.setTypeface(face);
        uicheck_in_date.setTypeface(face);
        uicheck_out_date.setTypeface(face);
        uinumber_of_people.setTypeface(face);
        uieffective_amount.setTypeface(face);
        uistatus.setTypeface(face);
        uiadmin_name.setTypeface(face);
        uiadmin_email.setTypeface(face);
        uicustomer_name.setTypeface(face);
        uicustomer_email.setTypeface(face);

        //manage ui : for admin
        admin_chat.setVisibility(View.GONE);

        final ItemReservedSpace item = reservedSpaceList.get(position);

        property_name.setText(item.getProperty_name());
        booking_date.setText(item.getBooking_date());
        check_in_date.setText(item.getCheck_in_date());
        check_out_date.setText(item.getCheck_out_date());
        number_of_people.setText(item.getNumber_of_people());
        effective_amount.setText(item.getEffective_amount()+" "+item.getCurrency_name());

        if(item.getStatus().equals("0")){
            status.setText("Not paid");
            pay.setVisibility(View.GONE);
        }else {
            status.setText("PAID");
            pay.setVisibility(View.GONE);
        }

        admin_email.setText(item.getAdmin_email());
        customer_name.setText(item.getUser_first_name()+" "+item.getUser_last_name());
        customer_email.setText(item.getUser_email());

        Glide.with(context).load(item.getAdmin_profile_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.avarta)
                .dontAnimate()
                .into(admin_profile_image);

        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

}
