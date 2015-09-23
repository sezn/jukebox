package com.szn.jukebox.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import com.szn.jukebox.JukeApplication;
import com.szn.jukebox.R;
import com.szn.jukebox.interfaces.AdapterListener;
import com.szn.jukebox.model.Product;

/**
 * Created by Julien Sezn on 11/09/2015.
 *
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private static final String TAG = "EventsAdapter";
    private Context context;
    private List<Product> products = new ArrayList<>();
    private AdapterListener listener;
    private int resId;
    private ImageLoader imgLoader;
    private ProgressDialog pd;


    public ProductAdapter(Context con, int res, List<Product> list){
        this.context = con;
        this.products = list;
        this.resId = res;
        imgLoader = JukeApplication.getInstance().getImageLoader();
        try {
            listener = (AdapterListener) con;
        } catch (ClassCastException e) {
            throw new ClassCastException(con.toString() + " must implement AdapterListener");
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);

        ViewHolder vh = new ViewHolder(v, new ViewHolder.OnViewHolderClick() {
            @Override
            public void onItemClick(View v, int pos) {
                listener.onSelected(v, products.get(pos) );
            }
        });

        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.name.setTag(position);

        if(product != null) {

            if(product.getName() != null && !product.getName().trim().isEmpty())
                holder.name.setText(product.getName());

            if(product.getFilters().getBRAND().getValue() != null && !product.getFilters().getBRAND().getValue().trim().isEmpty())
                holder.brand.setText(product.getFilters().getBRAND().getValue());


            if(product.getPicture() != null && !product.getPicture().trim().isEmpty()) {

                imgLoader.get(product.getPicture(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.mImage.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "onErrorResponse");
                    }
                });

            } else
                holder.mImage.setImageResource(R.drawable.flower_icon);
        }

        setAnimation(holder.container, position);
    }


    @Override
    public int getItemCount() {
        if(products == null) return 0;
        return products.size();
    }


    public void addItem(Product lens){
        products.add(lens);
//        notifyDataSetChanged();
        notifyItemInserted(products.size() - 1);
    }

    public void addItem(Product lens, int position){
        checkIfNull();
        products.add(position, lens);
        notifyItemInserted(position);
    }

    public void addItems(List<Product> lenses){
        checkIfNull();
//        lenses = lenses.subList(0, 4);
        products.addAll(lenses);
        notifyDataSetChanged();
    }

    public void remove(Product item) {
        int position = products.indexOf(item);
        products.remove(position);
        notifyItemRemoved(position);
    }

    void checkIfNull(){
        if(products == null)
            products = new ArrayList<>();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public View container;
        public TextView name, brand, detail;
        public ImageView mImage;
        OnViewHolderClick holderListener;

        public ViewHolder(View v, OnViewHolderClick hListener) {
            super(v);
            holderListener = hListener;
            container = v;
            name      = (TextView) v.findViewById(R.id.name);
            brand     = (TextView) v.findViewById(R.id.brand);
            detail    = (TextView) v.findViewById(R.id.details);
            mImage    = (ImageView)v.findViewById(R.id.img);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos= (int) view.findViewById(R.id.name).getTag();
            holderListener.onItemClick(view, pos);
        }

        public interface OnViewHolderClick{
            void onItemClick(View v, int pos);
        }
    }


    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
