package thegroceryshop.com.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.adapter.CouponAdapter;
import thegroceryshop.com.modal.WishListBean;


/*
 * Created by umeshk on 2/23/2017.
 */
@SuppressLint("ValidFragment")
public class Redeemed_Fragment extends Fragment {

    AppCompatActivity appCompatActivity;
    private RecyclerView recyclerViewCoupon;

    public Redeemed_Fragment(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_coupon, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerViewCoupon = view.findViewById(R.id.recyclerViewCoupon);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewCoupon.setLayoutManager(linearLayoutManager);
        recyclerViewCoupon.setHasFixedSize(true);

        recyclerViewCoupon.setAdapter(new CouponAdapter(getActivity(), new ArrayList<WishListBean>()));
    }
}
