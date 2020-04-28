package thegroceryshop.com.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.modal.PaymentCard;


/**
 * Created by JUNED on 6/10/2016.
 */
public class PaymentMethodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PaymentCard> list_cards;
    private final int TYPE_CARD = 1;
    private final int TYPE_FOOTER = 2;
    private Context mContext;
    private boolean isInPaymentMode;
    private OnCardSelectListener onCardSelectListener;

    public PaymentMethodAdapter(Context mContext, ArrayList<PaymentCard> list_cards, boolean isInPaymentMode) {
        this.mContext = mContext;
        this.list_cards = list_cards;
        this.isInPaymentMode = isInPaymentMode;
    }


    private static class PaymentCardHolder extends RecyclerView.ViewHolder {

        TextView txt_card_no, txt_select, txt_delete;
        ImageView img_card;
        LinearLayout lyt_payment_card;

        PaymentCardHolder(View v) {
            super(v);
            lyt_payment_card = v.findViewById(R.id.lyt_card_lyt_payment_card);
            txt_card_no = v.findViewById(R.id.lyt_card_txt_card_no);
            txt_select = v.findViewById(R.id.lyt_card_txt_select);
            txt_delete = v.findViewById(R.id.lyt_card_txt_delete);
            img_card = v.findViewById(R.id.card_footer_img_add_card);

        }
    }

    private static class FooterHolder extends RecyclerView.ViewHolder {

        RelativeLayout lyt_cash_on_delivery;
        LinearLayout lyt_add_card;
        FooterHolder(View v) {
            super(v);
            lyt_cash_on_delivery = v.findViewById(R.id.card_footer_lyt_cash_on_delivery);
            lyt_add_card = v.findViewById(R.id.card_footer_lyt_add_card);
            lyt_cash_on_delivery.setVisibility(View.GONE);

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == TYPE_CARD) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_card_item, parent, false);
            return new PaymentCardHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_card_footer, parent, false);
            return new FooterHolder(view);
        }
    }

    public void updateList(ArrayList<PaymentCard> list_cards) {
        this.list_cards = list_cards;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        PaymentCard paymentCard = list_cards.get(position);
        if (holder instanceof PaymentCardHolder) {

            ((PaymentCardHolder) holder).txt_card_no.setText(paymentCard.getCardNumber());

            if(isInPaymentMode){
                if (paymentCard.isSelected()) {
                    ((PaymentCardHolder) holder).txt_select.setSelected(true);
                    ((PaymentCardHolder) holder).img_card.setImageResource(R.mipmap.check);
                } else {
                    ((PaymentCardHolder) holder).txt_select.setSelected(false);
                    ((PaymentCardHolder) holder).img_card.setImageResource(R.mipmap.uncheck);
                }
            }else{
                ((PaymentCardHolder) holder).txt_select.setSelected(true);
                ((PaymentCardHolder) holder).img_card.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_trash));
            }

            ((PaymentCardHolder) holder).img_card.setTag(position);
            ((PaymentCardHolder) holder).img_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCardSelectListener != null) {
                        onCardSelectListener.onCardSelectListener((int) view.getTag());
                    }
                }
            });

        } else if(holder instanceof FooterHolder) {

            ((FooterHolder) holder).lyt_add_card.setTag(position);
            ((FooterHolder) holder).lyt_cash_on_delivery.setTag(position);

            ((FooterHolder) holder).lyt_add_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCardSelectListener != null) {
                        onCardSelectListener.onAddNewCard();
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list_cards.size();
    }

    @Override
    public int getItemViewType(int position) {

        if ((list_cards.size() - 1) == position) {
            return TYPE_FOOTER;
        } else {
            return TYPE_CARD;
        }
    }

    public void setOnCardSelectListener(OnCardSelectListener onCardSelectListener) {
        this.onCardSelectListener = onCardSelectListener;
    }

    public interface OnCardSelectListener {
        void onCardSelectListener(int position);
        void onAddNewCard();
    }
}
