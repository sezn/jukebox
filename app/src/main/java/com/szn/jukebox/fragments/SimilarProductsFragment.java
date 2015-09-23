package com.szn.jukebox.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.szn.jukebox.R;
import com.szn.jukebox.adapters.PagerProductsAdapter;
import com.szn.jukebox.model.Product;
import com.szn.jukebox.utils.Utils;

/**
 * Fragment Affichage Infos Profil
 * Created by Julien Sezn on 13/07/2015.
 *
 */
public class SimilarProductsFragment extends Fragment {


    private static final String TAG = SimilarProductsFragment.class.getSimpleName();
    private List<Product> products;


    public static SimilarProductsFragment newInstance(Bundle bdl) {
        SimilarProductsFragment fragment = new SimilarProductsFragment();
        fragment.setArguments(bdl);
        return fragment;
    }

    public SimilarProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        products = Utils.getProductsFromJSON(getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.similar_products, container, false);

        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        PagerProductsAdapter pagerAdapter = new PagerProductsAdapter(getFragmentManager(), getActivity(), products);
        pager.setAdapter(pagerAdapter);


        return v;
    }


 }
