package worknasi.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import worknasi.app.AppController;
import worknasi.helper.CircleTransform;
import worknasi.model.ItemReview;
import worknasi.worknasiapp.R;

/**
 * Created by user on 1/7/2018.
 */

public class ReviewAdapter extends BaseAdapter {
    private AppCompatActivity appCompatActivity;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    List<ItemReview> itemReviewList;
    Context context;


    public ReviewAdapter(AppCompatActivity appCompatActivity,Context context,List<ItemReview> itemReviewList) {
        this.appCompatActivity = appCompatActivity;
        this.itemReviewList = itemReviewList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return itemReviewList.size();
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
            inflater = (LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_review, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        ImageView review_profileimage = (ImageView) convertView.findViewById(R.id.icon_profile);
        TextView review_username = (TextView) convertView.findViewById(R.id.review_username);
        TextView review_date_time = (TextView) convertView.findViewById(R.id.review_date_time);
        TextView review_comment = (TextView) convertView.findViewById(R.id.review_comment);
        TextView review_rates =  (TextView) convertView.findViewById(R.id.text_ratings);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar);
        LinearLayout ratinglayout = (LinearLayout) convertView.findViewById(R.id.ratinglayout);

        //set fonts
        Typeface face = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/Helvetica.ttf");
        Typeface facebold = Typeface.createFromAsset(appCompatActivity.getAssets(), "font/HelveticaBold.ttf");
        review_username.setTypeface(facebold);
        review_date_time.setTypeface(face);
        review_comment.setTypeface(face);
        review_rates.setTypeface(face);

        ItemReview item = itemReviewList.get(position);

        review_username.setText(item.getUser_fullname());
        review_date_time.setText(item.getReview_time());
        review_comment.setText(item.getReview_content());
        review_rates.setText(item.getRatings());
        ratingBar.setRating(Float.parseFloat(item.getRatings()));
        ratingBar.setFocusableInTouchMode(false);

        Glide.with(context).load(item.getUser_profile_image())
                .placeholder(R.drawable.avarta)
                .transform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(review_profileimage);

        if(Float.parseFloat(item.getRatings())==0){
            ratinglayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

}
