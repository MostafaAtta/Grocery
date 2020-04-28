package thegroceryshop.com.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thegroceryshop.com.R;
import thegroceryshop.com.adapter.RegionAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Region;

public class RegionPickerFragment extends BottomSheetDialogFragment {

    private View mView;
    private RecyclerView recyl_regions;
    private ImageView img_close_region_picker;
    private RegionAdapter regionAdapter;
    private BottomSheetBehavior region_picker_behavior;
    private OnRegionSelectedListener onRegionSelectedListener;
    private boolean isCancelable;
    private Activity activity;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set the custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.lyt_region_picker, null);

        Window mWindow = dialog.getWindow();
        dialog.setContentView(view);

        if(mWindow != null){
            mWindow.setBackgroundDrawable(new ColorDrawable(0));
        }

        mView = mWindow.getDecorView();
        if(mView != null){
            mView.setBackgroundResource(android.R.color.transparent);
        }

        isCancelable = getArguments().getBoolean("isCancelable", false);

        img_close_region_picker = dialog.findViewById(R.id.region_picker_img_close);
        recyl_regions = dialog.findViewById(R.id.region_picker_recyl);

        if(!isCancelable){
            img_close_region_picker.setVisibility(View.GONE);
        }

        recyl_regions.setHasFixedSize(true);
        recyl_regions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if(OnlineMartApplication.getRegions() != null && OnlineMartApplication.getRegions().size() > 0){
            regionAdapter = new RegionAdapter(getContext(), OnlineMartApplication.getRegions());
        }else{
            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getRegionList())) {
                try {
                    JSONArray regionList = new JSONArray(OnlineMartApplication.mLocalStore.getRegionList());
                    if (regionList != null) {
                        OnlineMartApplication.getRegions().clear();

                        for (int i = 0; i < regionList.length(); i++) {
                            JSONObject regionObj = regionList.optJSONObject(i);
                            if (regionObj != null) {

                                Region region = new Region();
                                region.setRegionId(regionObj.optString("id"));
                                region.setRegionNameEnglish(regionObj.optString("name"));
                                region.setRegionNameArabic(regionObj.optString("name_ar"));

                                if(OnlineMartApplication.mLocalStore.getSelectedRegionId() != null && region.getRegionId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getSelectedRegionId())){
                                    if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getSelectedRegionId())){
                                        OnlineMartApplication.mLocalStore.setSelectedRegion(region);
                                        region.setSelected(true);
                                    }
                                }

                                OnlineMartApplication.getRegions().add(region);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        if(regionAdapter == null){
            regionAdapter = new RegionAdapter(getContext(), OnlineMartApplication.getRegions());
        }
        recyl_regions.setAdapter(regionAdapter);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        region_picker_behavior = (BottomSheetBehavior) params.getBehavior();
        region_picker_behavior.setHideable(isCancelable);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        region_picker_behavior.setPeekHeight(height/2);
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        region_picker_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        View bottomSheetView = getDialog().getWindow().getDecorView().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from((bottomSheetView)).setHideable(isCancelable);

        region_picker_behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        regionAdapter.setOnRegionChangedListener(new RegionAdapter.OnRegionChangedListener() {
            @Override
            public void onRegionChanged(Region selected_region) {

                for(int i=0; i<OnlineMartApplication.getRegions().size(); i++){
                    OnlineMartApplication.getRegions().get(i).setSelected(selected_region.getRegionId().equalsIgnoreCase(OnlineMartApplication.getRegions().get(i).getRegionId()));
                }

                //regionAdapter.notifyDataSetChanged();

                if(onRegionSelectedListener != null){
                    onRegionSelectedListener.onRegionSelected(selected_region, activity);
                }

                dismiss();
            }
        });

        img_close_region_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setOnRegionSelectedListener(OnRegionSelectedListener onRegionSelectedListener){
        this.onRegionSelectedListener = onRegionSelectedListener;
    }

    public interface OnRegionSelectedListener{
        public void onRegionSelected(Region selected_region, Activity activity);
    }

    public void updateRegionAdapter(){
        if (regionAdapter != null){
            regionAdapter.notifyDataSetChanged();
        }
    }
}