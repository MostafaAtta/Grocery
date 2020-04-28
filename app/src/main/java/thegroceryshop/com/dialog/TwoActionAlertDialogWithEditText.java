package thegroceryshop.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import thegroceryshop.com.R;
import thegroceryshop.com.custom.RippleButton;


/**
 * Created by rohitg on 12/8/2016.
 */

public class TwoActionAlertDialogWithEditText extends Dialog implements View.OnClickListener {

    // View components
    private TextView txtTitle;
    private TextView txtDescription;
    private RippleButton txtLeftAction, txtRightAction;
    public EditText edt_input;

    // for operations
    private String description;
    private String title;
    private String leftActionLabel, rightActionLabel;
    private Context context;
    private View mView;
    private static final String REGEX_EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private OnTwoActionAlertListener onTwoActionAlertListener;

    /**
     * Constructor to initialize SingleActionAlertDialog
     *
     * @param context          context of the component
     * @param title            title of the mBottomSheetDialog
     * @param description      description of the alert
     * @param leftActionLabel  left button label for the alert
     * @param rightActionLabel right button label for the alert
     */
    public TwoActionAlertDialogWithEditText(Context context, String title, String description, String leftActionLabel, String rightActionLabel) {
        super(context);
        this.context = context;
        this.title = title;
        this.description = description;
        this.leftActionLabel = leftActionLabel;
        this.rightActionLabel = rightActionLabel;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lyt_dialog_two_action_with_edittext);

        Window mWindow = getWindow();
        mView = mWindow.getDecorView();
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // mWindow.setGravity(Gravity.CENTER);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       /* Drawable d = mView.getResources().getDrawable(R.drawable.dialog_error_two_option);
        mWindow.setBackgroundDrawable(d);*/
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        mView.setBackgroundResource(android.R.color.transparent);

        txtTitle = findViewById(R.id.dialog_twoaction_txt_title);
        txtDescription = findViewById(R.id.dialog_twoaction_txt_desc);
        edt_input = findViewById(R.id.dialog_twoaction_edt_input);
        txtLeftAction = findViewById(R.id.dialog_left_btn);
        txtRightAction = findViewById(R.id.dialog_right_btn);

        edt_input.setLongClickable(false);

        txtTitle.setText(title);
        txtDescription.setText(description);
        txtLeftAction.setText(leftActionLabel);
        txtRightAction.setText(rightActionLabel);

        txtLeftAction.setOnClickListener(this);
        txtRightAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == txtLeftAction) {
            //dismiss();
            onTwoActionAlertListener.onLeftActionClick(view, edt_input.getText().toString());
        } else if (view == txtRightAction) {
            if (edt_input.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), context.getString(R.string.error_invaid_cvv), Toast.LENGTH_SHORT).show();
            } else if (edt_input.getText().toString().trim().length() != 3) {
                Toast.makeText(getContext(), context.getString(R.string.error_invaid_cvv1), Toast.LENGTH_SHORT).show();
            } else {
               /* if (!isValidEmails(context, edt_input)) {
                    Toast.makeText(getContext(), context.getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
                } else {*/
                    //dismiss();
                    onTwoActionAlertListener.onRightActionClick(view, edt_input.getText().toString());
                //}

            }
        }
    }

    private boolean isValidEmails(Context context, EditText edt_email) {
        boolean validation = true;

        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(REGEX_EMAIL);
        // EditText is for email

        if (edt_email.getText().toString().trim().length() == 0) {
            validation = false;
            edt_email.setSelected(true);
        } else if (!EMAIL_ADDRESS_PATTERN.matcher(edt_email.getText().toString().trim()).matches()) {
            validation = false;
            edt_email.setSelected(true);
        } else {
            edt_email.setSelected(false);
        }
        return validation;
    }

    public void setOnTwoActionsListener(OnTwoActionAlertListener onTwoActionAlertListener) {
        this.onTwoActionAlertListener = onTwoActionAlertListener;
    }

    public interface OnTwoActionAlertListener {
        void onLeftActionClick(View view, String input);

        void onRightActionClick(View view, String input);
    }
}
