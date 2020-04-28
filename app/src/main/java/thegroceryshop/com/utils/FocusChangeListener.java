package thegroceryshop.com.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.View;

/**
 * Created by jaikishang on 3/28/2017.
 */

public class FocusChangeListener implements View.OnFocusChangeListener {
    AppCompatEditText appCompatEditText;
    AppCompatActivity appCompatActivity;
    public FocusChangeListener(AppCompatEditText appCompatEditText, AppCompatActivity appCompatActivity)
    {
        this.appCompatActivity = appCompatActivity;
        this.appCompatEditText = appCompatEditText;
    }

    @Override
    public void onFocusChange(View view, boolean b)
    {
        if (appCompatEditText.getText().toString().trim().isEmpty())
        {
            appCompatEditText.setActivated(true);
            appCompatEditText.setSelected(true);
        }
    }
}
