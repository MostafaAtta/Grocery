package thegroceryshop.com.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;

import thegroceryshop.com.R;
import thegroceryshop.com.utils.LocaleHelper;


/**
 * Created by mohitd on 08-Mar-17.
 */

public class ListItemNewActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView5;
    private ImageView imageViewMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_new_layout);
        initView();
        setOnClickListener();
    }

    private void initView() {
        imageView5 = findViewById(R.id.imageView5);
        imageViewMenu = findViewById(R.id.imageViewMenu);
    }

    private void setOnClickListener(){
        imageViewMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageViewMenu:
                PopupMenu popup = new PopupMenu(this,imageViewMenu);
                popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

//                MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) .getMenu(), imageViewMenu);
//                menuHelper.setForceShowIcon(true);
//                menuHelper.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(ListItemNewActivity.this,
                                "Clicked popup menu item " + item.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });



                try {

                    Field field = popup.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper menuPopupHelper = (MenuPopupHelper) field.get(popup);
                    menuPopupHelper.setForceShowIcon(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.show();

                break;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
