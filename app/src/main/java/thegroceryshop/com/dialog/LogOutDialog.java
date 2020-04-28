package thegroceryshop.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import thegroceryshop.com.R;
import thegroceryshop.com.custom.RippleButton;

/*
 * Created by umeshk on 4/13/2017.
 */

public class LogOutDialog extends Dialog implements View.OnClickListener {

    // View components
    private RippleButton buttonCancel, buttonLogout;

    // for operations
    private Context context;
    private View mView;
    private OnLogOutAlertListener onLogOutAlertListener;

    /**
     * Constructor to initialize SingleActionAlertDialog
     *
     * @param context context of the component
     */
    public LogOutDialog(final Context context) {
        super(context);
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_logout);
        Window mWindow = getWindow();
        if (mWindow != null) {
            mView = mWindow.getDecorView();
            mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mWindow.setGravity(Gravity.CENTER);
        }
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        mView.setBackgroundResource(android.R.color.transparent);

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonCancel.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (view == buttonCancel) {
            onLogOutAlertListener.onCancelClick(view);
        } else if (view == buttonLogout) {
            onLogOutAlertListener.onLogoutClick(view);
        }
    }

    public void setOnLogOutAlertListener(OnLogOutAlertListener mWebServiceListener) {
        this.onLogOutAlertListener = mWebServiceListener;
    }

    public void onCancelClick(View view) {
        if (onLogOutAlertListener != null) {
            onLogOutAlertListener.onCancelClick(view);
        }
    }

    public void onLogoutClick(View view) {
        if (onLogOutAlertListener != null) {
            onLogOutAlertListener.onLogoutClick(view);
        }
    }

    public interface OnLogOutAlertListener {
        void onCancelClick(View view);

        void onLogoutClick(View view);
    }
}
