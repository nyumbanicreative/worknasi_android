package worknasi.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import worknasi.worknasiapp.R;


/**
 * Created by user on 1/26/2018.
 */

public class PopDialog {

    public static void showDialog(final Activity activity, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle( "Info" )
                .setIcon(R.drawable.ic_error_outline_black_24dp)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                        activity.finish();
                    }
                }).show();
    }
}
