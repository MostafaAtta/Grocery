package thegroceryshop.com.slideMenu;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.activity.BaseActivity;
import thegroceryshop.com.activity.ProductListActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.modal.sildemenu_model.CategoryItem;


/**
 * Created by jaikishang on 3/30/2017.
 */

public class SideMenuListAdapter extends BaseAdapter {

    BaseActivity appCompatActivity;
    LayoutInflater layoutInflater;
    View dividerView;
    ArrayList<SideMenuHolder> list_holders;

    public SideMenuListAdapter(BaseActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        layoutInflater = (LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list_holders = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return OnlineMartApplication.nestedCategory.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final SideMenuHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.promotion_item_view, null);
            holder = new SideMenuHolder();
            holder.lyt_row = convertView.findViewById(R.id.lyt_row);
            holder.promotion_name_txt = convertView.findViewById(R.id.promotion_name_txt);
            holder.img_promotion = convertView.findViewById(R.id.promotion_img);
            holder.categoryItme_view = convertView.findViewById(R.id.categoryItme_view);
            convertView.setTag(holder);
            if(position < list_holders.size()){
                list_holders.set(position, holder);
            }else{
                list_holders.add(holder);
            }
        } else {
            holder = (SideMenuHolder) convertView.getTag();
        }

        holder.promotion_name_txt.setText(OnlineMartApplication.nestedCategory.get(position).getName());
        holder.promotion_name_txt.setTag(position);
        holder.lyt_row.setTag(position);
        holder.img_promotion.setImageResource(R.drawable.accent_btn_bg);

        switch (OnlineMartApplication.nestedCategory.get(position).getApp_ican_id()) {

            case 2:
                holder.img_promotion.setImageResource(R.drawable.img_2);
                break;
            case 3:
                holder.img_promotion.setImageResource(R.drawable.img_3);
                break;
            case 4:
                holder.img_promotion.setImageResource(R.drawable.img_4);
                break;
            case 5:
                holder.img_promotion.setImageResource(R.drawable.img_5);
                break;
            case 6:
                holder.img_promotion.setImageResource(R.drawable.img_6);
                break;
            case 7:
                holder.img_promotion.setImageResource(R.drawable.img_7);
                break;
            case 8:
                holder.img_promotion.setImageResource(R.drawable.img_8);
                break;
            case 9:
                holder.img_promotion.setImageResource(R.drawable.img_9);
                break;
            case 10:
                holder.img_promotion.setImageResource(R.drawable.img_10);
                break;
            case 11:
                holder.img_promotion.setImageResource(R.drawable.img_11);
                break;
            case 12:
                holder.img_promotion.setImageResource(R.drawable.img_12);
                break;
            case 13:
                holder.img_promotion.setImageResource(R.drawable.img_13);
                break;
            case 14:
                holder.img_promotion.setImageResource(R.drawable.img_14);
                break;
            case 15:
                holder.img_promotion.setImageResource(R.drawable.img_15);
                break;
            case 16:
                holder.img_promotion.setImageResource(R.drawable.img_16);
                break;
            case 17:
                holder.img_promotion.setImageResource(R.drawable.img_17);
                break;
            case 18:
                holder.img_promotion.setImageResource(R.drawable.img_18);
                break;
            case 19:
                holder.img_promotion.setImageResource(R.drawable.img_19);
                break;
            case 20:
                holder.img_promotion.setImageResource(R.drawable.img_20);
                break;
            case 21:
                holder.img_promotion.setImageResource(R.drawable.img_21);
                break;
            case 22:
                holder.img_promotion.setImageResource(R.drawable.img_22);
                break;
            case 23:
                holder.img_promotion.setImageResource(R.drawable.img_23);
                break;
            case 24:
                holder.img_promotion.setImageResource(R.drawable.img_24);
                break;
            default:
                holder.img_promotion.setImageResource(R.drawable.img_default);
        }

        if (OnlineMartApplication.nestedCategory.get(position).isSelected()) {
            holder.promotion_name_txt.setTextColor(ContextCompat.getColor(appCompatActivity, R.color.menu_selected));
            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                holder.promotion_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.montserrat_regular));
            } else {
                holder.promotion_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.ge_ss_two_medium));
            }
        } else {
            holder.promotion_name_txt.setTextColor(ContextCompat.getColor(appCompatActivity, R.color.white_90));
            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                holder.promotion_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.montserrat_light));
            } else {
                holder.promotion_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.ge_ss_two_light));
            }
        }

        if (OnlineMartApplication.nestedCategory.get(position).getLevel().equalsIgnoreCase("3")) {

            if (OnlineMartApplication.nestedCategory.get(position).getCategory() != null) {

                if (OnlineMartApplication.nestedCategory.get(position).getCategory().size() > 0) {

                    boolean isOpened = false;
                    holder.categoryItme_view.removeAllViews();
                    for (int subCatIndex = 0; subCatIndex < OnlineMartApplication.nestedCategory.get(position).getCategory().size(); subCatIndex++) {

                        CategoryItem categoryItem = OnlineMartApplication.nestedCategory.get(position).getCategory().get(subCatIndex);

                        View subCatView = null;
                        if(holder.categoryItme_view.getTag() != null && holder.categoryItme_view.getTag() instanceof View){
                            subCatView = (View)(holder.categoryItme_view.getTag());
                        }else{
                            subCatView = LayoutInflater.from(appCompatActivity).inflate(R.layout.promotion_item_view, null, false);
                        }


                        subCatView.setBackgroundColor(ContextCompat.getColor(appCompatActivity, R.color.promo_color));
                        TextView sunCat_name_txt = subCatView.findViewById(R.id.promotion_name_txt);
                        LinearLayout lyt_row1 = subCatView.findViewById(R.id.lyt_row);
                        sunCat_name_txt.setText(categoryItem.getName());

                        if (categoryItem.isSelected()) {
                            sunCat_name_txt.setTextColor(ContextCompat.getColor(appCompatActivity, R.color.menu_selected));
                            //sunCat_name_txt.setTypeface(Typeface.SERIF);
                            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                                sunCat_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.montserrat_regular));
                            } else {
                                sunCat_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.ge_ss_two_medium));
                            }
                            isOpened = true;
                        } else {
                            sunCat_name_txt.setTextColor(ContextCompat.getColor(appCompatActivity, R.color.white));
                            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                                sunCat_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.montserrat_light));
                            } else {
                                sunCat_name_txt.setTypeface(ResourcesCompat.getFont(appCompatActivity, R.font.ge_ss_two_light));
                            }
                            //sunCat_name_txt.setTypeface(Typeface.MONOSPACE);
                        }

                        dividerView = subCatView.findViewById(R.id.dividerView);
                        dividerView.setVisibility(View.VISIBLE);
                        subCatView.setTag(categoryItem);

                        subCatView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CategoryItem categoryItem1 = (CategoryItem) v.getTag();
                                Intent productListintent = new Intent(appCompatActivity, ProductListActivity.class);

                                productListintent.putExtra(ProductListActivity.CATEGORY_ID, categoryItem1.getCategoryId());
                                productListintent.putExtra(ProductListActivity.CATEGORY_NAME, categoryItem1.getName());

                                if (!categoryItem1.getLevel().equalsIgnoreCase("1")) {
                                    productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, true);
                                } else {
                                    productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);
                                }

                                if (categoryItem1.getAdminCategoryLevel().equalsIgnoreCase("3")) {
                                    productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, false);
                                } else {
                                    productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, true);
                                }

                                appCompatActivity.resetSelection();
                                categoryItem1.setSelected(true);
                                appCompatActivity.lyt_drawer.closeDrawers();
                                appCompatActivity.startActivity(productListintent);
                            }
                        });

                        holder.categoryItme_view.addView(subCatView);
                    }

                    if (isOpened) {
                        OnlineMartApplication.nestedCategory.get(position).setFlag(false);//flag = false;
                        AppUtil.expand1(holder.categoryItme_view);
                        dividerView.setVisibility(View.GONE);
                    } else {
                        OnlineMartApplication.nestedCategory.get(position).setFlag(true);//flag = true;
                        AppUtil.collapse1(holder.categoryItme_view);
                        dividerView.setVisibility(View.VISIBLE);
                    }
                }else{
                    holder.categoryItme_view.removeAllViews();
                    AppUtil.collapse1(holder.categoryItme_view);
                    OnlineMartApplication.nestedCategory.get(position).setOpened(false);
                    OnlineMartApplication.nestedCategory.get(position).setFlag(false);
                }

                if(OnlineMartApplication.nestedCategory.get(position).isOpened()){
                    OnlineMartApplication.nestedCategory.get(position).setFlag(false);//flag = false;
                    AppUtil.expand1(holder.categoryItme_view);
                    dividerView.setVisibility(View.GONE);
                    OnlineMartApplication.nestedCategory.get(position).setOpened(true);
                }else{
                    OnlineMartApplication.nestedCategory.get(position).setFlag(true);//flag = true;
                    AppUtil.collapse1(holder.categoryItme_view);
                    dividerView.setVisibility(View.VISIBLE);
                    OnlineMartApplication.nestedCategory.get(position).setOpened(false);
                }
            }else{
                holder.categoryItme_view.removeAllViews();
                AppUtil.collapse1(holder.categoryItme_view);
                OnlineMartApplication.nestedCategory.get(position).setOpened(false);
                OnlineMartApplication.nestedCategory.get(position).setFlag(false);
            }
        }else{
            holder.categoryItme_view.removeAllViews();
            AppUtil.collapse1(holder.categoryItme_view);
            OnlineMartApplication.nestedCategory.get(position).setOpened(false);
            OnlineMartApplication.nestedCategory.get(position).setFlag(false);
        }

        holder.lyt_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = (Integer) view.getTag();
                if (OnlineMartApplication.nestedCategory.get(position).getLevel().equalsIgnoreCase("1") || OnlineMartApplication.nestedCategory.get(position).getLevel().equalsIgnoreCase("2")) {

                    Intent productListintent = new Intent(appCompatActivity, ProductListActivity.class);

                    productListintent.putExtra(ProductListActivity.CATEGORY_ID, OnlineMartApplication.nestedCategory.get(position).getCategoryId());
                    productListintent.putExtra(ProductListActivity.CATEGORY_NAME, OnlineMartApplication.nestedCategory.get(position).getName());

                    if (!OnlineMartApplication.nestedCategory.get(position).getLevel().equalsIgnoreCase("1")) {
                        productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, true);
                    } else {
                        productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);
                    }

                    if (OnlineMartApplication.nestedCategory.get(position).getAdminCategoryLevel().equalsIgnoreCase("3")) {
                        productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, false);
                    } else {
                        productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, true);
                    }

                    appCompatActivity.resetSelection();
                    OnlineMartApplication.nestedCategory.get(position).setSelected(true);
                    appCompatActivity.startActivity(productListintent);

                } else if (OnlineMartApplication.nestedCategory.get(position).getLevel().equalsIgnoreCase("3")) {

                    try {
                        if (OnlineMartApplication.nestedCategory.get(position).getFlag()) {
                            OnlineMartApplication.nestedCategory.get(position).setFlag(false);//flag = false;
                            AppUtil.expand(holder.categoryItme_view);

                            dividerView.setVisibility(View.GONE);
                            OnlineMartApplication.nestedCategory.get(position).setOpened(true);

                        } else {

                            OnlineMartApplication.nestedCategory.get(position).setFlag(true);//flag = true;
                            AppUtil.collapse(holder.categoryItme_view);
                            dividerView.setVisibility(View.VISIBLE);
                            OnlineMartApplication.nestedCategory.get(position).setOpened(false);

                            if(appCompatActivity != null && appCompatActivity.listview_side != null){
                                appCompatActivity.listview_side.post(null);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    try {
                        if (OnlineMartApplication.nestedCategory.get(position).getFlag()) {
                            OnlineMartApplication.nestedCategory.get(position).setFlag(false);//flag = false;
                            AppUtil.expand(holder.categoryItme_view);
                            dividerView.setVisibility(View.GONE);
                        } else {
                            OnlineMartApplication.nestedCategory.get(position).setFlag(true);//flag = true;
                            AppUtil.collapse(holder.categoryItme_view);
                            dividerView.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return convertView;
    }

    class SideMenuHolder {

        private LinearLayout lyt_row;
        private TextView promotion_name_txt;
        private ImageView img_promotion;
        private LinearLayout categoryItme_view;
    }
}
