package thegroceryshop.com.checkoutProcess.checkoutfragment;

/*
 * Created by rohitg on 4/19/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.CardListAdapter;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.AddCreditCardActivity;
import thegroceryshop.com.checkoutProcess.CheckOutProcessActivity;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.DeliveryCharges;
import thegroceryshop.com.modal.PaymentCard;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;


public class CardListFragment extends Fragment {

    private CheckOutProcessActivity checkOutProcessActivity;
    private RecyclerView recyclerView;
    private CardListAdapter cardListAdapter;
    private ArrayList<PaymentCard> list_cards = new ArrayList<>();
    private String userId;
    private String paymentToken = "";
    private String databaseId = "";
    private String cardTokenForPayment = "";
    private String mName;
    private String mTextMM;
    private String mTextYY;
    private String mTextCVV = "123";
    private String card_id = "";
    private JSONObject order;
    private boolean isVisibleToUser = false;
    private PaymentCard selectedCard;
    private boolean isCashOnDelivery, useValu = false;
    private ArrayList<DeliveryCharges> list_charges = new ArrayList<>();
    private boolean useCredits = false;
    private RelativeLayout lyt_cash_on_delivery, lytValu;
    private ImageView img_cash_on_delivery, imgValu;

    private LoaderLayout loader;
    private float credits_to_use;

    public static CardListFragment newInstance(CheckOutProcessActivity checkOutProcessActivity) {
        CardListFragment cardListFragment = new CardListFragment();
        cardListFragment.checkOutProcessActivity = checkOutProcessActivity;
        return cardListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, true);

        View view = inflater.inflate(R.layout.layout_cards_list, container, false);
        checkOutProcessActivity = (CheckOutProcessActivity) getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        TextView textViewDone = view.findViewById(R.id.card_list_txt_done);
        recyclerView = view.findViewById(R.id.card_list_recyl_cards);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(checkOutProcessActivity);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        loader = view.findViewById(R.id.cardlist_loader);
        loader.setStatuText(getString(R.string.no_saved_cards_available));
        lyt_cash_on_delivery = view.findViewById(R.id.card_footer_lyt_cash_on_delivery);
        img_cash_on_delivery = view.findViewById(R.id.card_footer_img_manage_card);
        lytValu = view.findViewById(R.id.card_footer_lyt_valu_installment);
        imgValu = view.findViewById(R.id.card_footer_img_valu);

        userId = ApiLocalStore.getInstance(checkOutProcessActivity).getUserId();

        if(isCashOnDelivery){
            img_cash_on_delivery.setImageResource(R.mipmap.check);
        }else{
            img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
        }

        lyt_cash_on_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCashOnDelivery = !isCashOnDelivery;
                if(isCashOnDelivery){
                    useValu = false;
                    imgValu.setImageResource(R.mipmap.uncheck);
                    img_cash_on_delivery.setImageResource(R.mipmap.check);
                    selectedCard = null;
                    if (list_cards != null && list_cards.size() > 0) {
                        for (int i = 0; i < list_cards.size(); i++) {
                            list_cards.get(i).setSelected(false);
                        }
                    }

                    float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                    float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                    credits_to_use = 0.0f;

                    if (total_amount <= available_credits) {
                        credits_to_use = total_amount;
                    } else {
                        credits_to_use = available_credits;
                    }

                    if (cardListAdapter != null) {
                        cardListAdapter.setCashOnDelivery(true);
                        if(total_amount == credits_to_use){
                            useCredits = false;
                            cardListAdapter.updateFooter(true, credits_to_use + "", false);
                            cardListAdapter.notifyDataSetChanged();
                        }else{
                            cardListAdapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                }
            }
        });

        lytValu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useValu = !useValu;
                if(useValu){
                    isCashOnDelivery = false;
                    img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                    imgValu.setImageResource(R.mipmap.check);
                    selectedCard = null;
                    if (list_cards != null && list_cards.size() > 0) {
                        for (int i = 0; i < list_cards.size(); i++) {
                            list_cards.get(i).setSelected(false);
                        }
                    }

                    float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                    float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                    credits_to_use = 0.0f;

                    if (total_amount <= available_credits) {
                        credits_to_use = total_amount;
                    } else {
                        credits_to_use = available_credits;
                    }

                    if (cardListAdapter != null) {
                        cardListAdapter.setCashOnDelivery(false);
                        if(total_amount == credits_to_use){
                            useCredits = false;
                            cardListAdapter.updateFooter(true, credits_to_use + "", false);
                            cardListAdapter.notifyDataSetChanged();
                        }else{
                            cardListAdapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    imgValu.setImageResource(R.mipmap.uncheck);
                }
            }
        });

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedCard != null) {

                    float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                    float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                    credits_to_use = 0.0f;

                    if (total_amount <= available_credits) {
                        credits_to_use = total_amount;
                    } else {
                        credits_to_use = available_credits;
                    }

                    checkOutProcessActivity.getOrder().setSelectedCard(selectedCard);
                    checkOutProcessActivity.getOrder().setCardId(selectedCard.getCardId());
                    checkOutProcessActivity.getOrder().setUseTGSCredits(useCredits);
                    checkOutProcessActivity.getOrder().setAmountByTgsCredits(credits_to_use);
                    if (useCredits) {
                        float amountPayByCard = checkOutProcessActivity.getOrder().getTotalAmountToPay() - credits_to_use;
                        if (amountPayByCard <= 0.0f) {
                            checkOutProcessActivity.getOrder().setPaymentMode("3");
                            checkOutProcessActivity.getOrder().setAmountByUser(0.0f);
                        } else {
                            checkOutProcessActivity.getOrder().setPaymentMode("4");
                            checkOutProcessActivity.getOrder().setAmountByUser(amountPayByCard);
                        }
                    } else {
                        checkOutProcessActivity.getOrder().setPaymentMode("2");
                        checkOutProcessActivity.getOrder().setAmountByUser(checkOutProcessActivity.getOrder().getTotalAmountToPay());
                    }
                    checkOutProcessActivity.viewpager.setCurrentItem(4);
                    OnlineMartApplication.mLocalStore.saveDefaultPaymentMethod(selectedCard.getCardId());
                } else {
                    if (isCashOnDelivery) {
                        checkOutProcessActivity.getOrder().setSelectedCard(null);
                        checkOutProcessActivity.getOrder().setCardId(AppConstants.BLANK_STRING);
                        OnlineMartApplication.mLocalStore.saveDefaultPaymentMethod("-1");
                        checkOutProcessActivity.getOrder().setUseTGSCredits(useCredits);
                        checkOutProcessActivity.getOrder().setAmountByTgsCredits(credits_to_use);
                        checkOutProcessActivity.getOrder().setAmountByUser(0.0f);

                        if (useCredits) {
                            float amountPayByCash = checkOutProcessActivity.getOrder().getTotalAmountToPay() - credits_to_use;
                            if (amountPayByCash <= 0.0f) {
                                checkOutProcessActivity.getOrder().setPaymentMode("3");
                                checkOutProcessActivity.getOrder().setAmountByUser(0.0f);
                            } else {
                                checkOutProcessActivity.getOrder().setPaymentMode("5");
                                checkOutProcessActivity.getOrder().setAmountByUser(amountPayByCash);
                            }
                        } else {
                            checkOutProcessActivity.getOrder().setPaymentMode("1");
                            checkOutProcessActivity.getOrder().setAmountByUser(checkOutProcessActivity.getOrder().getTotalAmountToPay());
                        }
                        checkOutProcessActivity.viewpager.setCurrentItem(4);


                    } else if (useCredits) {

                        checkOutProcessActivity.getOrder().setSelectedCard(null);
                        checkOutProcessActivity.getOrder().setCardId(AppConstants.BLANK_STRING);
                        checkOutProcessActivity.getOrder().setUseTGSCredits(useCredits);
                        checkOutProcessActivity.getOrder().setAmountByTgsCredits(credits_to_use);

                        float amountPayByUser = checkOutProcessActivity.getOrder().getTotalAmountToPay() - credits_to_use;
                        if (amountPayByUser > 0.0f) {
                            AppUtil.displaySingleActionAlert(checkOutProcessActivity, getString(R.string.app_name), getString(R.string.credits_not_enough), getString(R.string.ok));
                        } else {
                            checkOutProcessActivity.getOrder().setPaymentMode("3");
                            checkOutProcessActivity.getOrder().setAmountByUser(amountPayByUser);
                            checkOutProcessActivity.viewpager.setCurrentItem(4);
                        }

                    } else {
                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, getString(R.string.app_name), getString(R.string.pls_select_payment), getString(R.string.ok));
                    }

                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

        if (isVisibleToUser) {
            setUpDeliveryCharges();

            float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
            float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
            credits_to_use = 0.0f;

            if (total_amount <= available_credits) {
                credits_to_use = total_amount;
            } else {
                credits_to_use = available_credits;
            }

            if (cardListAdapter != null) {
                if (credits_to_use <= 0.0f) {
                    cardListAdapter.updateFooter(false, "0.0", false);
                } else {
                    cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                    /*if (isCashOnDelivery) {
                        cardListAdapter.updateFooter(false, "0.0", false);
                    } else {
                        cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                    }*/
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCreditsInWalltet();
        getCardListWebService();
    }

    public void setUpDeliveryCharges() {
        if (checkOutProcessActivity != null) {
            float totalWithoutDeliveryCharges = checkOutProcessActivity.getOrder().getTotalAmountToPay() - checkOutProcessActivity.getOrder().getDeliveryCharges();
            float deliveryCharge = getDeliveryCharges(totalWithoutDeliveryCharges);
            checkOutProcessActivity.getOrder().setDeliveryCharges(deliveryCharge);
            checkOutProcessActivity.getOrder().setTotalAmountToPay(totalWithoutDeliveryCharges + deliveryCharge);
        }
    }

    public float getDeliveryCharges(float subtotal) {

        if (list_charges != null) {
            if (list_charges.size() > 0) {
                for (int i = 0; i < list_charges.size(); i++) {
                    if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && checkOutProcessActivity.getOrder().getDeliveyType().equalsIgnoreCase(list_charges.get(i).getType())) {
                        return list_charges.get(i).getCharges();
                    }
                }
                return 0.0f;
            } else {
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())) {
                    try {
                        JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                        if (chargesArray != null) {
                            list_charges.clear();
                            for (int i = 0; i < chargesArray.length(); i++) {
                                JSONObject object = chargesArray.optJSONObject(i);
                                if (object != null) {

                                    DeliveryCharges deliveryCharges = new DeliveryCharges();
                                    deliveryCharges.setId(object.optString("id"));
                                    deliveryCharges.setType(object.optString("type"));
                                    deliveryCharges.setStart_amount((float) (object.optDouble("start_amt", 0.0)));
                                    deliveryCharges.setEnd_amount((float) (object.optDouble("end_amt", 0.0)));
                                    deliveryCharges.setCharges((float) (object.optDouble("d_charge", 0.0)));
                                    list_charges.add(deliveryCharges);
                                }
                            }

                            if (list_charges != null) {
                                for (int i = 0; i < list_charges.size(); i++) {
                                    if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && checkOutProcessActivity.getOrder().getDeliveyType().equalsIgnoreCase(list_charges.get(i).getType())) {
                                        return list_charges.get(i).getCharges();
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                }
            }
        } else {
            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())) {
                try {
                    JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                    if (chargesArray != null) {
                        list_charges.clear();
                        for (int i = 0; i < chargesArray.length(); i++) {
                            JSONObject object = chargesArray.optJSONObject(i);
                            if (object != null) {

                                DeliveryCharges deliveryCharges = new DeliveryCharges();
                                deliveryCharges.setId(object.optString("id"));
                                deliveryCharges.setType(object.optString("type"));
                                deliveryCharges.setStart_amount((float) (object.optDouble("start_amt", 0.0)));
                                deliveryCharges.setEnd_amount((float) (object.optDouble("end_amt", 0.0)));
                                deliveryCharges.setCharges((float) (object.optDouble("d_charge", 0.0)));
                                list_charges.add(deliveryCharges);
                            }
                        }

                        if (list_charges != null) {
                            for (int i = 0; i < list_charges.size(); i++) {
                                if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && checkOutProcessActivity.getOrder().getDeliveyType().equalsIgnoreCase(list_charges.get(i).getType())) {
                                    return list_charges.get(i).getCharges();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        }
        return 0.0f;
    }

    private void getCardListWebService() {
        if (NetworkUtil.networkStatus(checkOutProcessActivity)) {
            loader.showProgress();
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("user_id", userId);
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
                Call<ResponseBody> call = apiService.getCardList((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loader.showContent();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                list_cards.clear();
                                if (resObject.getInt("status_code") == 200) {
                                    JSONArray jsonArray = resObject.getJSONArray("data");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject cardObject = jsonArray.getJSONObject(i);
                                        PaymentCard paymentCard = new PaymentCard();
                                        paymentCard.setCardId(cardObject.getString("id"));
                                        paymentCard.setCard_token(cardObject.getString("token"));
                                        paymentCard.setCard_name(cardObject.getString("card_name"));
                                        paymentCard.setCard_number(cardObject.getString("masked_pan"));
                                        paymentCard.setMerchant_id(cardObject.getString("merchant_id"));
                                        paymentCard.setCard_subType(cardObject.getString("card_subtype"));
                                        paymentCard.setCard_month(cardObject.getString("card_month"));
                                        paymentCard.setCard_year(cardObject.getString("card_year"));
                                        list_cards.add(paymentCard);
                                        cardTokenForPayment = paymentCard.getCard_token();
                                        mTextMM = paymentCard.getCard_month();
                                        mTextYY = paymentCard.getCard_year();
                                        mName = paymentCard.getCard_name();
                                        card_id = paymentCard.getCardId();
                                    }
                                }

                                PaymentCard newPaymentCard = new PaymentCard();
                                list_cards.add(newPaymentCard);

                                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                                    if (OnlineMartApplication.mLocalStore.getDefaultPaymentMethod().equalsIgnoreCase("-1")) {
                                        isCashOnDelivery = true;
                                        selectedCard = null;
                                    } else {
                                        isCashOnDelivery = false;
                                        img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                        for (int i = 0; i < list_cards.size(); i++) {
                                            if (list_cards.get(i).getCardId() != null && list_cards.get(i).getCardId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                                                list_cards.get(i).setSelected(true);
                                                selectedCard = list_cards.get(i);
                                                break;
                                            }
                                        }
                                    }
                                }else{
                                    isCashOnDelivery = true;
                                    selectedCard = null;
                                }

                                float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                credits_to_use = 0.0f;

                                if (total_amount <= available_credits) {
                                    credits_to_use = total_amount;
                                } else {
                                    credits_to_use = available_credits;
                                }

                                if (credits_to_use <= 0.0f) {
                                    cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, false, "0.0");
                                } else {
                                    cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, true, credits_to_use + "");
                                }

                                if(isCashOnDelivery){
                                    img_cash_on_delivery.setImageResource(R.mipmap.check);
                                }else{
                                    img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                }

                                recyclerView.setAdapter(cardListAdapter);
                                cardListAdapter.setOnCardSelectListener(new CardListAdapter.OnCardSelectListener() {
                                    @Override
                                    public void onCardSelectListener(int position, boolean useCredits) {

                                        CardListFragment.this.useCredits = useCredits;
                                        if (list_cards != null && list_cards.size() - 1 > position) {
                                            PaymentCard paymentCard = list_cards.get(position);

                                            for (int i = 0; i < list_cards.size() - 1; i++) {
                                                if (paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId())) {
                                                    selectedCard = paymentCard;
                                                    isCashOnDelivery = false;
                                                    img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                                }
                                                list_cards.get(i).setSelected(paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId()));
                                            }

                                            if (cardListAdapter != null) {
                                                cardListAdapter.setCashOnDelivery(false);
                                                cardListAdapter.notifyDataSetChanged();
                                            }

                                            float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                            float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                            credits_to_use = 0.0f;

                                            if (total_amount <= available_credits) {
                                                credits_to_use = total_amount;
                                            } else {
                                                credits_to_use = available_credits;
                                            }

                                            if (credits_to_use <= 0.0f) {
                                                cardListAdapter.updateFooter(false, "0.0", false);
                                            } else {

                                                if (total_amount == credits_to_use) {
                                                    cardListAdapter.updateFooter(true, credits_to_use + "", false);
                                                } else {
                                                    cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onAddNewCard() {
                                        Intent intentMyCoupons;
                                        intentMyCoupons = new Intent(checkOutProcessActivity, AddCreditCardActivity.class);
                                        startActivity(intentMyCoupons);
                                    }

                                    @Override
                                    public void onCashOnDelivery() {

                                        if (list_cards != null && list_cards.size() > 0) {
                                            for (int i = 0; i < list_cards.size(); i++) {
                                                list_cards.get(i).setSelected(false);
                                            }
                                        }

                                        isCashOnDelivery = true;
                                        if (cardListAdapter != null) {
                                            cardListAdapter.setCashOnDelivery(true);
                                            cardListAdapter.notifyDataSetChanged();
                                        }
                                        selectedCard = null;
                                    }

                                    @Override
                                    public void onValu() {

                                        if (list_cards != null && list_cards.size() > 0) {
                                            for (int i = 0; i < list_cards.size(); i++) {
                                                list_cards.get(i).setSelected(false);
                                            }
                                        }

                                        useValu = true;
                                        if (cardListAdapter != null) {
                                            cardListAdapter.setUseValu(true);
                                            cardListAdapter.notifyDataSetChanged();
                                        }
                                        selectedCard = null;
                                    }

                                    @Override
                                    public void onUserCreditsChanged(boolean isUseCredits) {
                                        CardListFragment.this.useCredits = isUseCredits;

                                        float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                        float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                        credits_to_use = 0.0f;

                                        if (total_amount <= available_credits) {
                                            credits_to_use = total_amount;
                                            isCashOnDelivery = false;
                                            img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                            selectedCard = null;
                                            for (int i = 0; i < list_cards.size() - 1; i++) {
                                                list_cards.get(i).setSelected(false);
                                            }
                                        }

                                        if (cardListAdapter != null) {
                                            cardListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();

                                list_cards.clear();
                                PaymentCard newPaymentCard = new PaymentCard();
                                list_cards.add(newPaymentCard);

                                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                                    if (OnlineMartApplication.mLocalStore.getDefaultPaymentMethod().equalsIgnoreCase("-1")) {
                                        isCashOnDelivery = true;
                                        selectedCard = null;
                                    } else {
                                        isCashOnDelivery = false;
                                        img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                        for (int i = 0; i < list_cards.size(); i++) {
                                            if (list_cards.get(i).getCardId() != null && list_cards.get(i).getCardId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                                                list_cards.get(i).setSelected(true);
                                                selectedCard = list_cards.get(i);
                                                break;
                                            }
                                        }
                                    }
                                }


                                float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                credits_to_use = 0.0f;

                                if (total_amount <= available_credits) {
                                    credits_to_use = total_amount;
                                } else {
                                    credits_to_use = available_credits;
                                }

                                if (credits_to_use <= 0.0f) {
                                    cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, false, "0.0");
                                } else {
                                    if (isCashOnDelivery) {
                                        cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, false, "0.0");
                                    } else {
                                        cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, true, credits_to_use + "");
                                    }
                                }

                                recyclerView.setAdapter(cardListAdapter);
                                cardListAdapter.setOnCardSelectListener(new CardListAdapter.OnCardSelectListener() {
                                    @Override
                                    public void onCardSelectListener(int position, boolean useCredits) {

                                        CardListFragment.this.useCredits = useCredits;
                                        if (list_cards != null && list_cards.size() - 1 > position) {
                                            PaymentCard paymentCard = list_cards.get(position);

                                            for (int i = 0; i < list_cards.size() - 1; i++) {
                                                if (paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId())) {
                                                    selectedCard = paymentCard;
                                                    isCashOnDelivery = false;
                                                    img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                                }
                                                list_cards.get(i).setSelected(paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId()));
                                            }

                                            if (cardListAdapter != null) {
                                                cardListAdapter.setCashOnDelivery(false);
                                                cardListAdapter.notifyDataSetChanged();
                                            }

                                            float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                            float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                            credits_to_use = 0.0f;

                                            if (total_amount <= available_credits) {
                                                credits_to_use = total_amount;
                                            } else {
                                                credits_to_use = available_credits;
                                            }

                                            if (credits_to_use <= 0.0f) {
                                                cardListAdapter.updateFooter(false, "0.0", false);
                                            } else {

                                                if (total_amount == credits_to_use) {
                                                    cardListAdapter.updateFooter(true, credits_to_use + "", false);
                                                } else {
                                                    cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onAddNewCard() {
                                        Intent intentMyCoupons;
                                        intentMyCoupons = new Intent(checkOutProcessActivity, AddCreditCardActivity.class);
                                        startActivity(intentMyCoupons);
                                    }

                                    @Override
                                    public void onCashOnDelivery() {

                                        if (list_cards != null && list_cards.size() > 0) {
                                            for (int i = 0; i < list_cards.size(); i++) {
                                                list_cards.get(i).setSelected(false);
                                            }
                                        }

                                        isCashOnDelivery = true;
                                        if (cardListAdapter != null) {
                                            cardListAdapter.setCashOnDelivery(true);
                                            cardListAdapter.notifyDataSetChanged();
                                        }
                                        selectedCard = null;
                                    }

                                    @Override
                                    public void onValu() {

                                        if (list_cards != null && list_cards.size() > 0) {
                                            for (int i = 0; i < list_cards.size(); i++) {
                                                list_cards.get(i).setSelected(false);
                                            }
                                        }

                                        useValu = true;
                                        if (cardListAdapter != null) {
                                            cardListAdapter.setUseValu(true);
                                            cardListAdapter.notifyDataSetChanged();
                                        }
                                        selectedCard = null;
                                    }

                                    @Override
                                    public void onUserCreditsChanged(boolean isUseCredits) {
                                        CardListFragment.this.useCredits = isUseCredits;

                                        float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                        float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                        credits_to_use = 0.0f;

                                        if (total_amount <= available_credits) {
                                            credits_to_use = total_amount;
                                            isCashOnDelivery = false;
                                            img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                            selectedCard = null;
                                            for (int i = 0; i < list_cards.size() - 1; i++) {
                                                list_cards.get(i).setSelected(false);
                                            }
                                        }

                                        if (cardListAdapter != null) {
                                            cardListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //loader.showStatusText();

                        list_cards.clear();
                        PaymentCard newPaymentCard = new PaymentCard();
                        list_cards.add(newPaymentCard);

                        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                            if (OnlineMartApplication.mLocalStore.getDefaultPaymentMethod().equalsIgnoreCase("-1")) {
                                isCashOnDelivery = true;
                                selectedCard = null;
                            } else {
                                isCashOnDelivery = false;
                                img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                for (int i = 0; i < list_cards.size(); i++) {
                                    if (list_cards.get(i).getCardId() != null && list_cards.get(i).getCardId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                                        list_cards.get(i).setSelected(true);
                                        selectedCard = list_cards.get(i);
                                        break;
                                    }
                                }
                            }
                        }

                        float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                        float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                        credits_to_use = 0.0f;

                        if (total_amount <= available_credits) {
                            credits_to_use = total_amount;
                        } else {
                            credits_to_use = available_credits;
                        }

                        if (credits_to_use <= 0.0f) {
                            cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, false, "0.0");
                        } else {
                            if (isCashOnDelivery) {
                                cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, false, "0.0");
                            } else {
                                cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, true, credits_to_use + "");
                            }
                        }

                        recyclerView.setAdapter(cardListAdapter);
                        cardListAdapter.setOnCardSelectListener(new CardListAdapter.OnCardSelectListener() {
                            @Override
                            public void onCardSelectListener(int position, boolean useCredits) {

                                CardListFragment.this.useCredits = useCredits;
                                if (list_cards != null && list_cards.size() - 1 > position) {
                                    PaymentCard paymentCard = list_cards.get(position);

                                    for (int i = 0; i < list_cards.size() - 1; i++) {
                                        if (paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId())) {
                                            selectedCard = paymentCard;
                                            isCashOnDelivery = false;
                                            img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                        }
                                        list_cards.get(i).setSelected(paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId()));
                                    }

                                    if (cardListAdapter != null) {
                                        cardListAdapter.setCashOnDelivery(false);
                                        cardListAdapter.notifyDataSetChanged();
                                    }

                                    float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                    float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                    credits_to_use = 0.0f;

                                    if (total_amount <= available_credits) {
                                        credits_to_use = total_amount;
                                    } else {
                                        credits_to_use = available_credits;
                                    }

                                    if (credits_to_use <= 0.0f) {
                                        cardListAdapter.updateFooter(false, "0.0", false);
                                    } else {

                                        if (total_amount == credits_to_use) {
                                            cardListAdapter.updateFooter(true, credits_to_use + "", false);
                                        } else {
                                            cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onAddNewCard() {
                                Intent intentMyCoupons;
                                intentMyCoupons = new Intent(checkOutProcessActivity, AddCreditCardActivity.class);
                                startActivity(intentMyCoupons);
                            }

                            @Override
                            public void onCashOnDelivery() {

                                if (list_cards != null && list_cards.size() > 0) {
                                    for (int i = 0; i < list_cards.size(); i++) {
                                        list_cards.get(i).setSelected(false);
                                    }
                                }

                                isCashOnDelivery = true;
                                if (cardListAdapter != null) {
                                    cardListAdapter.setCashOnDelivery(true);
                                    cardListAdapter.notifyDataSetChanged();
                                }
                                selectedCard = null;
                            }

                            @Override
                            public void onValu() {

                                if (list_cards != null && list_cards.size() > 0) {
                                    for (int i = 0; i < list_cards.size(); i++) {
                                        list_cards.get(i).setSelected(false);
                                    }
                                }

                                useValu = true;
                                if (cardListAdapter != null) {
                                    cardListAdapter.setUseValu(true);
                                    cardListAdapter.notifyDataSetChanged();
                                }
                                selectedCard = null;
                            }

                            @Override
                            public void onUserCreditsChanged(boolean isUseCredits) {
                                CardListFragment.this.useCredits = isUseCredits;

                                float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                credits_to_use = 0.0f;

                                if (total_amount <= available_credits) {
                                    credits_to_use = total_amount;
                                    isCashOnDelivery = false;
                                    img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                    selectedCard = null;
                                    for (int i = 0; i < list_cards.size() - 1; i++) {
                                        list_cards.get(i).setSelected(false);
                                    }
                                }

                                if (cardListAdapter != null) {
                                    cardListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                //loader.showStatusText();

                list_cards.clear();
                PaymentCard newPaymentCard = new PaymentCard();
                list_cards.add(newPaymentCard);

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                    if (OnlineMartApplication.mLocalStore.getDefaultPaymentMethod().equalsIgnoreCase("-1")) {
                        isCashOnDelivery = true;
                        selectedCard = null;
                    } else {
                        isCashOnDelivery = false;
                        img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                        for (int i = 0; i < list_cards.size(); i++) {
                            if (list_cards.get(i).getCardId() != null && list_cards.get(i).getCardId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getDefaultPaymentMethod())) {
                                list_cards.get(i).setSelected(true);
                                selectedCard = list_cards.get(i);
                                break;
                            }
                        }
                    }
                }

                float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                credits_to_use = 0.0f;

                if (total_amount <= available_credits) {
                    credits_to_use = total_amount;
                } else {
                    credits_to_use = available_credits;
                }

                if (credits_to_use <= 0.0f) {
                    cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, false, "0.0");
                } else {
                    if (isCashOnDelivery) {
                        cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, false, "0.0");
                    } else {
                        cardListAdapter = new CardListAdapter(checkOutProcessActivity, list_cards, isCashOnDelivery, useValu, true, credits_to_use + "");
                    }
                }

                recyclerView.setAdapter(cardListAdapter);
                cardListAdapter.setOnCardSelectListener(new CardListAdapter.OnCardSelectListener() {
                    @Override
                    public void onCardSelectListener(int position, boolean useCredits) {

                        CardListFragment.this.useCredits = useCredits;
                        if (list_cards != null && list_cards.size() - 1 > position) {
                            PaymentCard paymentCard = list_cards.get(position);

                            for (int i = 0; i < list_cards.size() - 1; i++) {
                                if (paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId())) {
                                    selectedCard = paymentCard;
                                    isCashOnDelivery = false;
                                    img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                                }
                                list_cards.get(i).setSelected(paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId()));
                            }

                            if (cardListAdapter != null) {
                                cardListAdapter.setCashOnDelivery(false);
                                cardListAdapter.notifyDataSetChanged();
                            }

                            float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                            float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                            credits_to_use = 0.0f;

                            if (total_amount <= available_credits) {
                                credits_to_use = total_amount;
                            } else {
                                credits_to_use = available_credits;
                            }

                            if (credits_to_use <= 0.0f) {
                                cardListAdapter.updateFooter(false, "0.0", false);
                            } else {
                                if (total_amount == credits_to_use) {
                                    cardListAdapter.updateFooter(true, credits_to_use + "", false);
                                } else {
                                    cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                                }
                                /*if (isCashOnDelivery) {
                                    cardListAdapter.updateFooter(false, "0.0", false);
                                } else {
                                    if (total_amount == credits_to_use) {
                                        cardListAdapter.updateFooter(true, credits_to_use + "", false);
                                    } else {
                                        cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                                    }
                                }*/
                            }
                        }
                    }

                    @Override
                    public void onAddNewCard() {
                        Intent intentMyCoupons;
                        intentMyCoupons = new Intent(checkOutProcessActivity, AddCreditCardActivity.class);
                        startActivity(intentMyCoupons);
                    }

                    @Override
                    public void onCashOnDelivery() {

                        if (list_cards != null && list_cards.size() > 0) {
                            for (int i = 0; i < list_cards.size(); i++) {
                                list_cards.get(i).setSelected(false);
                            }
                        }

                        isCashOnDelivery = true;
                        if (cardListAdapter != null) {
                            cardListAdapter.setCashOnDelivery(true);
                            cardListAdapter.notifyDataSetChanged();
                        }
                        selectedCard = null;

                    }

                    @Override
                    public void onValu() {

                        if (list_cards != null && list_cards.size() > 0) {
                            for (int i = 0; i < list_cards.size(); i++) {
                                list_cards.get(i).setSelected(false);
                            }
                        }

                        useValu = true;
                        if (cardListAdapter != null) {
                            cardListAdapter.setUseValu(true);
                            cardListAdapter.notifyDataSetChanged();
                        }
                        selectedCard = null;
                    }

                    @Override
                    public void onUserCreditsChanged(boolean isUseCredits) {
                        CardListFragment.this.useCredits = isUseCredits;

                        float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                        float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                        credits_to_use = 0.0f;

                        if (total_amount <= available_credits) {
                            credits_to_use = total_amount;
                            isCashOnDelivery = false;
                            img_cash_on_delivery.setImageResource(R.mipmap.uncheck);
                            selectedCard = null;
                            for (int i = 0; i < list_cards.size() - 1; i++) {
                                list_cards.get(i).setSelected(false);
                            }
                        }

                        if (cardListAdapter != null) {
                            cardListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    private void loadCreditsInWalltet() {

        try {

            JSONObject request = new JSONObject();
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

            ApiInterface apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
            Call<ResponseBody> call = apiService.getCredits((new ConvertJsonToMap().jsonToMap(request)));
            APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                //call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            JSONObject resObj = obj.optJSONObject("response");
                            if (resObj.length() > 0) {

                                int statusCode = resObj.optInt("status_code", 0);
                                String status = resObj.optString("status");
                                String errorMsg = resObj.optString("error_message");
                                if (statusCode == 200 && status != null && status.equalsIgnoreCase("OK")) {

                                    JSONObject dataObj = resObj.optJSONObject("data");
                                    if (dataObj != null) {
                                        String amount = dataObj.optString("user_credit_points");
                                        OnlineMartApplication.mLocalStore.saveUserCredit(Float.parseFloat(amount));

                                        float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                                        float total_amount = checkOutProcessActivity.getOrder().getTotalAmountToPay();
                                        credits_to_use = 0.0f;

                                        if (total_amount <= available_credits) {
                                            credits_to_use = total_amount;
                                        } else {
                                            credits_to_use = available_credits;
                                        }

                                        if (cardListAdapter != null) {
                                            if (credits_to_use <= 0.0f) {
                                                cardListAdapter.updateFooter(false, "0.0", false);
                                            } else {
                                                cardListAdapter.updateFooter(true, credits_to_use + "", CardListFragment.this.useCredits);
                                            }
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppUtil.hideProgress();
                }
            });

        } catch (JSONException e) {
            AppUtil.hideProgress();
        }
    }
}
