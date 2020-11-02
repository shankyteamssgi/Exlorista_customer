package e.a.exlorista_customer.ProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import e.a.exlorista_customer.R;

public class progressDialog {

    Activity activity;
    AlertDialog dialog;

    public progressDialog(Activity mActivity) {
        activity = mActivity;
    }

    public void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog_layout, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public void stopLoading() {
        dialog.dismiss();
    }

}
