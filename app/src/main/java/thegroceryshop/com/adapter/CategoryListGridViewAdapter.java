package thegroceryshop.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import thegroceryshop.com.R;
import thegroceryshop.com.activity.ProductMoreDetailsActivity;

/**
 * Created by mohitd on 27-Feb-17.
 */

public class CategoryListGridViewAdapter extends BaseAdapter implements View.OnClickListener{
    private Context context;
  //  private final String[] mThumbIds;

  /*  public ProductListGridViewAdapter(Context context, String[] mThumbIds) {
        this.context = context;
        this.mThumbIds = mThumbIds;
    }*/

    public CategoryListGridViewAdapter(Context context) {
        this.context = context;

    }
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.layout_category_listing_item, null);

            // set image based on selected text
            ImageView imageView = gridView
                    .findViewById(R.id.imageViewProduct);

            LinearLayout linearLayoutProductView = gridView.findViewById(R.id.linearLayoutProductView);

            linearLayoutProductView.setOnClickListener(this);


           // imageView.setImageResource(mThumbIds[position]);


        } else {
            gridView = convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return 9 ;// mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.linearLayoutProductView:
                Intent intentProductDetails;
                intentProductDetails = new Intent(context, ProductMoreDetailsActivity.class);
                intentProductDetails.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intentProductDetails);
                break;
        }
    }
}

