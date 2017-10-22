package in.co.victor.chatbubblesdemo.widgets;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import in.co.victor.chatbubblesdemo.R;

/**
 * Created by Victor on 20/10/2017.
 */

public class DialogService {

    public ProgressDialog mProgressDialog;

    private Context context;

    public DialogService(Context context) {
        this.context = context;
    }

    public void showToast(String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(text);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
