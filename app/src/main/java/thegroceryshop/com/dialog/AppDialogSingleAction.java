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

public class AppDialogSingleAction extends Dialog implements View.OnClickListener {

    // View components
    private TextView txtTitle;
    private TextView txtDescription;
    private RippleButton txtAction;
    private ScrollView scrollView;

    // for operations
    private String description;
    private String title;
    private String actionLabel;
    private Context context;
    private OnSingleActionListener mSingleActionListener;
    private View mView;
    private boolean isStartAligned;

    /**
     * Constructor to initialize SingleActionAlertDialog
     *
     * @param context     context of the component
     * @param title       title of the mBottomSheetDialog
     * @param description description of the alert
     * @param actionLabel button label for the alert
     */
    public AppDialogSingleAction(final Context context, String title, String description, String actionLabel) {
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
        scrollView = findViewById(R.id.scrollView);

//        if(ValidationUtil.isNullOrBlank(title)){
//            txtTitle.setVisibility(View.GONE);
//        }else{
//            txtTitle.setVisibility(View.VISIBLE);
//        }

        //txtTitle.setText(title);
        txtDescription.setText(description);
        txtAction.setText(actionLabel);
        txtDescription.post(new Runnable() {
            @Override
            public void run() {
                if(txtDescription.getLineCount() > 3) {
                    scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AppUtil.convertDpToPixel(150, context)));
                }
            }
        });

        txtAction.setOnClickListener(this);
    }

    public void setStartAligned(boolean isStartAligned){
        this.isStartAligned = isStartAligned;
        txtDescription.setGravity(Gravity.START);
    }

    @Override
    public void onClick(View view) {
        if (view == txtAction) {
            mSingleActionListener.onActionClick(view, this);
        }
    }

    public void setOnSingleActionListener(OnSingleActionListener mSingleActionListener) {
        this.mSingleActionListener = mSingleActionListener;
    }

    public void onActionClick(View view) {
        if (mSingleActionListener != null) {
            mSingleActionListener.onActionClick(view, this);
        }
    }

    public void setTag(String tag) {
        this.txtDescription.setTag(tag);
    }

    public String getTag() {
        return this.txtDescription.getTag().toString();
    }
}
