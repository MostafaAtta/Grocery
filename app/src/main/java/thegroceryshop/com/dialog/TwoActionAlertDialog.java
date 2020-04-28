package thegroceryshop.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import thegroceryshop.com.R;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.RippleButton;


/**
 * Created by rohitg on 12/8/2016.
 */

public class TwoActionAlertDialog extends Dialog implements View.OnClickListener {

    // View components
    private TextView txtTitle;
    private TextView txtDescription;
    private ScrollView scrollView;
    private RippleButton txtLeftAction, txtRightAction;

    // for operations
    private String description;
    private String title;
    private String leftActionLabel, rightActionLabel;
    private Context context;
    private View mView;
    private TwoActionAlertListener mTwoActionAlertListener;

    /**
     * Constructor to initialize SingleActionAlertDialog
     *
     * @param context          context of the component
     * @param title            title of the mBottomSheetDialog
     * @param description      description of the alert
     * @param leftActionLabel  left button label for the alert
     * @param rightActionLabel right button label for the alert
     */
    public TwoActionAlertDialog(final Context context, String title, final String description, String leftActionLabel, String rightActionLabel) {
        super(context);
        this.context = context;
        this.title = title;
        this.description = description;
        this.leftActionLabel = leftActionLabel;
        this.rightActionLabel = rightActionLabel;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lyt_dialog_two_action);
        Window mWindow = getWindow();
        mView = mWindow.getDecorView();
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        mView.setBackgroundResource(android.R.color.transparent);

        txtTitle = findViewById(R.id.dialog_twoaction_title);
        txtDescription = findViewById(R.id.dialog_twoaction_desc);
        txtLeftAction = findViewById(R.id.dialog_left_btn);
        txtRightAction = findViewById(R.id.dialog_right_btn);
        scrollView  = findViewById(R.id.scrollView);

        //txtTitle.setText(title);
        txtDescription.setText(description);
        txtLeftAction.setText(leftActionLabel);
        txtRightAction.setText(rightActionLabel);

        txtDescription.post(new Runnable() {
            @Override
            public void run() {
                if(txtDescription.getLineCount() > 3) {
                    scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AppUtil.convertDpToPixel(150, context)));
                }
            }
        });

        txtLeftAction.setOnClickListener(this);
        txtRightAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (view == txtLeftAction) {
            mTwoActionAlertListener.onLeftActionClick(view);
        } else if (view == txtRightAction) {
            mTwoActionAlertListener.onRightActionClick(view);
        }
    }

    public void setOnTwoActionsListener(TwoActionAlertListener mWebServiceListener) {
        this.mTwoActionAlertListener = mWebServiceListener;
    }

    public void onLeftActionClick(View view) {
        if (mTwoActionAlertListener != null) {
            mTwoActionAlertListener.onLeftActionClick(view);
        }
    }

    public void onRightActionClick(View view) {
        if (mTwoActionAlertListener != null) {
            mTwoActionAlertListener.onRightActionClick(view);
        }
    }

    public interface OnTwoActionAlertListener {
        void setOnTwoActionAlertListener(View view);
    }
}
