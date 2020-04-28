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
import thegroceryshop.com.custom.ValidationUtil;


/**
 * Created by rohitg on 12/8/2016.
 */

public class BarCodeDialog extends Dialog implements View.OnClickListener {

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
    private OnDoubleActionListener mDoubleActionListener;

    /**
     * Constructor to initialize SingleActionAlertDialog
     *
     * @param context          context of the component
     * @param title            title of the mBottomSheetDialog
     * @param description      description of the alert
     * @param leftActionLabel  left button label for the alert
     * @param rightActionLabel right button label for the alert
     */
    public BarCodeDialog(final Context context, String title, final String description, String leftActionLabel, String rightActionLabel) {
        super(context);
        this.context = context;
        this.title = title;
        this.description = description;
        this.leftActionLabel = leftActionLabel;
        this.rightActionLabel = rightActionLabel;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lyt_bar_code_dialog);
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

        if(ValidationUtil.isNullOrBlank(txtTitle)){
            txtTitle.setVisibility(View.GONE);
        }else{
            txtTitle.setVisibility(View.VISIBLE);
        }

        txtTitle.setText(title);
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
            mDoubleActionListener.onLeftActionClick(view);
        } else if (view == txtRightAction) {
            mDoubleActionListener.onRightActionClick(view);
        }
    }

    public void setOnDoubleActionsListener(OnDoubleActionListener mDoubleActionListener) {
        this.mDoubleActionListener = mDoubleActionListener;
    }

    public void onLeftActionClick(View view) {
        if (mDoubleActionListener != null) {
            mDoubleActionListener.onLeftActionClick(view);
        }
    }

    public void onRightActionClick(View view) {
        if (mDoubleActionListener != null) {
            mDoubleActionListener.onRightActionClick(view);
        }
    }
}
