package thegroceryshop.com.utils;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import thegroceryshop.com.custom.ValidationUtil;

public class AddTextWatcher implements TextWatcher
{

    private EditText view;
    private AppCompatActivity appCompatActivity;
    public AddTextWatcher(EditText view, AppCompatActivity appCompatActivity) {
        this.view = view;
        this.appCompatActivity =  appCompatActivity;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
        mErrorSetUp(view.getInputType());
    }

    public void afterTextChanged(Editable editable)
    {
    }

    private void mErrorSetUp(int code)
    {
        switch (code)
        {
            case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                if (ValidationUtil.isValidEmails(appCompatActivity,view))
                {
                    this.view.setActivated(false);
                    this.view.setSelected(false);
                }
                break;
            case InputType.TYPE_TEXT_VARIATION_PASSWORD:
                if (ValidationUtil.isValidPassword(appCompatActivity,view,false))
                {
                    this.view.setActivated(false);
                    this.view.setSelected(false);
                }
                break;
            default:
                this.view.setActivated(false);
                this.view.setSelected(false);
                break;

        }
    }
}