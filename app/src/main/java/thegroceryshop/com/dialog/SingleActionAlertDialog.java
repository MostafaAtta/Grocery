package thegroceryshop.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import thegroceryshop.com.R;
import thegroceryshop.com.custom.RippleButton;


/**
 * Created by rohitg on 12/8/2016.
 */

public class SingleActionAlertDialog extends Dialog implements View.OnClickListener {

    // View components
    private TextView txtTitle;
    private TextView txtDescription;
    private RippleButton txtAction;

    // for operations
    private String description;
    private String title;
    private String actionLabel;
    private Context context;
    private SingleActionAlertListener mSingleActionAlertListener;
    private View mView;

    /**
     * Constructor to initialize SingleActionAlertDialog
     *
     * @param context     context of the component
     * @param title       title of the mBottomSheetDialog
     * @param description description of the alert
     * @param actionLabel button label for the alert
     */
    public SingleActionAlertDialog(Context context, String title, String description, String actionLabel) {
        super(context);
        this.context = context;
        this.title = title;
        this.description = description;
        this.actionLabel = actionLabel;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lyt_dialog_single_action);

        Window mWindow = getWindow();
        mView = mWindow.getDecorView();
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        mView.setBackgroundResource(android.R.color.transparent);

        txtTitle = findViewById(R.id.dialog_title);
        txtDescription = findViewById(R.id.dialog_desc);
        txtAction = findViewById(R.id.dialog_btn);

        txtTitle.setText(title);
        txtDescription.setText(description);
        txtAction.setText(actionLabel);

        txtAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == txtAction) {
            mSingleActionAlertListener.onActionClick(view, this);
        }
    }

    public void setOnSingleActionsListener(SingleActionAlertListener mSingleActionAlertListener) {
        this.mSingleActionAlertListener = mSingleActionAlertListener;
    }

    public void onActionClick(View view) {
        if (mSingleActionAlertListener != null) {
            mSingleActionAlertListener.onActionClick(view, this);
        }
    }

    public interface OnSingleActionAlertListener {
        void setOnSingleActionAlertListener(View view);
    }
}
