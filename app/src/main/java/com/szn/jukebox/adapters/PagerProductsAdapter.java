package com.szn.jukebox.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.google.android.gms.analytics.ecommerce.Product;

import com.szn.jukebox.model.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Julien Sezn on 11/09/2015.
 *
 */
public class PagerProductsAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private List<Product> products = new ArrayList<>();


    public PagerProductsAdapter(FragmentManager fm) {
        super(fm);
    }

    public PagerProductsAdapter(FragmentManager fragmentManager, Activity activity, List<Product> products) {
        super(fragmentManager);
        this.context = activity;
        this.products = products;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putParcelable(Constants.PRODUCT, products.get(position));

        fragment = PagerItemFragment.newInstance(args);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        if(products == null) return 0;
        return products.size();
    }

    @Override
    public float getPageWidth(int position) {
        return 0.4f;
    }

}
