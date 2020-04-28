
package thegroceryshop.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.modal.PaymentCard;



/**
 * Created by JUNED on 6/10/2016.
 */
public class CardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ImageView imageView;
    private ArrayList<PaymentCard> list_cards;
    private final int TYPE_CARD = 1;
    private final int TYPE_FOOTER = 2;
    private Context mContext;
    private OnCardSelectListener onCardSelectListener;
    private boolean isCashOnDelivery, useValu;
    private String creditsToUse;
    private boolean isCreditsAvailable;
    private boolean useTGSCredits;

    public CardListAdapter(Context mContext, ArrayList<PaymentCard> list_cards, boolean isCashOnDelivery, 
                           boolean useValu, boolean isCreditsAvailable, String creditsToUse) {
        this.mContext = mContext;
        this.list_cards = list_cards;
        this.isCashOnDelivery = isCashOnDelivery;
        this.useValu = useValu;
        this.creditsToUse = creditsToUse;
        this.isCreditsAvailable = isCreditsAvailable;
        this.isCreditsAvailable = false;
        this.useTGSCredits = false;
    }

    public void setCashOnDelivery(boolean isCashOnDelivery){
        this.isCashOnDelivery = isCashOnDelivery;
    }

    public void setUseValu(boolean useValu){
        this.useValu = useValu;
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

        RelativeLayout lyt_cash_on_delivery, lyt_tgs_credits, lytValu;
        ImageView img_cash_on_delivery, img_use_credits, imgValu;
        TextView txt_available_credits, txt_use_credits;
        LinearLayout lyt_add_card;

        FooterHolder(View v) {
            super(v);
            lyt_cash_on_delivery = v.findViewById(R.id.card_footer_lyt_cash_on_delivery);
            img_cash_on_delivery = v.findViewById(R.id.card_footer_img_manage_card);
            lytValu = v.findViewById(R.id.card_footer_lyt_valu_installment);
            imgValu = v.findViewById(R.id.card_footer_img_valu);
            lyt_add_card = v.findViewById(R.id.card_footer_lyt_add_card);
            lyt_tgs_credits = v.findViewById(R.id.card_footer_lyt_tgs_credits);
            img_use_credits = v.findViewById(R.id.card_footer_img_tgs_credits);
            txt_available_credits = v.findViewById(R.id.card_footer_txt_available_credits);
            txt_use_credits = v.findViewById(R.id.card_footer_txt_use_x_credits);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == TYPE_CARD) {
            view = LayoutInflater.from(mContext).inflate(R.layout.lyt_card_item, parent, false);
            return new PaymentCardHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_card_footer, parent, false);
            return new FooterHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        PaymentCard paymentCard = list_cards.get(position);
        if (holder instanceof PaymentCardHolder) {

            ((PaymentCardHolder) holder).txt_card_no.setText(paymentCard.getCardNumber());
            ((PaymentCardHolder) holder).lyt_payment_card.setTag(position);

            ((PaymentCardHolder) holder).lyt_payment_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCardSelectListener != null) {
                        isCashOnDelivery = false;
                        useValu = false;
                        onCardSelectListener.onCardSelectListener((int) view.getTag(), useTGSCredits);
                    }
                }
            });

            if (paymentCard.isSelected()) {
                ((PaymentCardHolder) holder).txt_select.setSelected(true);
                ((PaymentCardHolder) holder).img_card.setImageResource(R.mipmap.check);
            } else {
                ((PaymentCardHolder) holder).txt_select.setSelected(false);
                ((PaymentCardHolder) holder).img_card.setImageResource(R.mipmap.uncheck);
            }

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

            if(isCashOnDelivery){
                ((FooterHolder) holder).img_cash_on_delivery.setImageResource(R.mipmap.check);
            }else{
                ((FooterHolder) holder).img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
            }

            if(useValu){
                ((FooterHolder) holder).imgValu.setImageResource(R.mipmap.check);
            }else{
                ((FooterHolder) holder).imgValu.setImageResource(R.mipmap.uncheck);
            }
            ((FooterHolder) holder).lyt_cash_on_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCardSelectListener != null) {
                        isCashOnDelivery = !isCashOnDelivery;
                        if(isCashOnDelivery){
                            ((FooterHolder) holder).img_cash_on_delivery.setImageResource(R.mipmap.check);
                        }else{
                            ((FooterHolder) holder).img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                        }
                        onCardSelectListener.onCashOnDelivery();
                    }
                }
            });

            ((FooterHolder) holder).lytValu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCardSelectListener != null) {
                        useValu = !useValu;
                        if(useValu){
                            ((FooterHolder) holder).imgValu.setImageResource(R.mipmap.check);
                        }else{
                            ((FooterHolder) holder).imgValu.setImageResource(R.mipmap.uncheck);
                        }
                        onCardSelectListener.onValu();
                    }
                }
            });

            if(isCreditsAvailable){
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    ((FooterHolder) holder).txt_available_credits.setText(mContext.getString(R.string.tgs_credit) + " : " + mContext.getString(R.string.egp) + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getUserCredit());
                    ((FooterHolder) holder).txt_use_credits.setText(String.format(mContext.getString(R.string.use_x_credit), mContext.getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + creditsToUse)));
                }else{
                    ((FooterHolder) holder).txt_available_credits.setText(mContext.getString(R.string.tgs_credit) + " : " + OnlineMartApplication.mLocalStore.getUserCredit() + AppConstants.SPACE + mContext.getString(R.string.egp));
                    ((FooterHolder) holder).txt_use_credits.setText(String.format(mContext.getString(R.string.use_x_credit), AppUtil.mSetRoundUpPrice("" + creditsToUse) + AppConstants.SPACE + mContext.getString(R.string.egp)));
                }

                if(useTGSCredits){
                    ((FooterHolder) holder).img_use_credits.setImageResource(R.mipmap.check);
                }else{
                    ((FooterHolder) holder).img_use_credits.setImageResource(R.mipmap.uncheck);
                }

                ((FooterHolder) holder).txt_available_credits.setVisibility(View.VISIBLE);
                ((FooterHolder) holder).lyt_tgs_credits.setVisibility(View.VISIBLE);
            }else{

                ((FooterHolder) holder).txt_available_credits.setVisibility(View.GONE);
                ((FooterHolder) holder).lyt_tgs_credits.setVisibility(View.GONE);
                ((FooterHolder) holder).img_use_credits.setImageResource(R.mipmap.uncheck);

            }

            ((FooterHolder) holder).lyt_tgs_credits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCardSelectListener != null) {
                        useTGSCredits = !useTGSCredits;
                        if(useTGSCredits){
                            ((FooterHolder) holder).img_use_credits.setImageResource(R.mipmap.check);
                        }else{
                            ((FooterHolder) holder).img_use_credits.setImageResource(R.mipmap.uncheck);
                        }
                        onCardSelectListener.onUserCreditsChanged(useTGSCredits);
                    }
                }
            });
        }
    }

    public void updateFooter(boolean isCreditsAvailable, String creditsToUse, boolean useTGSCredits){

        this.isCreditsAvailable = isCreditsAvailable;
        this.isCreditsAvailable = false;
        this.creditsToUse = creditsToUse;
        this.useTGSCredits = useTGSCredits;
        this.isCreditsAvailable = false;
        this.useValu = false;
        this.useTGSCredits = false;
        notifyItemChanged(list_cards.size()-1);

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
        void onCardSelectListener(int position, boolean useCredits);
        void onAddNewCard();
        void onCashOnDelivery();
        //update value layout click
        void onValu();
        void onUserCreditsChanged(boolean isUseCredits);
    }
}
