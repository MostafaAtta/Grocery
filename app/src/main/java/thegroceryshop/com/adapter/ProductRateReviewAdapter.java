package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 20-03-2018.
 */

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.modal.ProductRateReviewBean;

public class ProductRateReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ProductRateReviewBean> productRateReviewBeanList;
    private Context mContext;

    public ProductRateReviewAdapter(Context mContext, ArrayList<ProductRateReviewBean> productRateReviewBeanList) {
        this.mContext = mContext;
        this.productRateReviewBeanList = productRateReviewBeanList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_product_comment_list, parent, false);
        return new ProductRateReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProductRateReviewHolder) {
            ProductRateReviewHolder productRateReviewHolder = (ProductRateReviewHolder) holder;
            ProductRateReviewBean productRateReviewBean = productRateReviewBeanList.get(position);
            productRateReviewHolder.textViewName.setText(productRateReviewBean.getUserName());
            productRateReviewHolder.finalRating.setRating(Float.parseFloat(productRateReviewBean.getRating()));
            productRateReviewHolder.textViewComment.setText(productRateReviewBean.getComment());
            productRateReviewHolder.textViewTime.setText(productRateReviewBean.getDateTime());

        }
    }

    @Override
    public int getItemCount() {
        return productRateReviewBeanList.size();
    }


    private static class ProductRateReviewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private SimpleRatingBar finalRating;
        private TextView textViewComment;
        private TextView textViewTime;

        ProductRateReviewHolder(View v) {
            super(v);
            textViewName = v.findViewById(R.id.textViewName);
            finalRating = v.findViewById(R.id.finalRating);
            textViewComment = v.findViewById(R.id.textViewComment);
            textViewTime = v.findViewById(R.id.textViewTime);
        }
    }
}
