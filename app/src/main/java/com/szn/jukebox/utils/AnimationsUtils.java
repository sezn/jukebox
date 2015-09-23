package com.szn.jukebox.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.szn.jukebox.R;

/**
 * Classe Utilitaire pour les Animations
 * Created by Julien Sezn on 16/09/2015.
 *
 */
public class AnimationsUtils {


    private static final String TAG = "AnimationsUtils";


    public static void expand(View rootView) {
        //set Visible
        View mLinearLayout = rootView.findViewById(R.id.collapsed_wrapper);

        mLinearLayout.setVisibility(View.VISIBLE);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mLinearLayout.measure(widthSpec, heightSpec);
        ValueAnimator mAnimator = slideAnimator(mLinearLayout, 0, mLinearLayout.getMeasuredHeight());

        ObjectAnimator rotate = ObjectAnimator.ofFloat(rootView.findViewById(R.id.headerArrow), "rotation", 0f, 90f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(mAnimator).with(rotate);
        animSet.start();

//        mAnimator.start();
    }

    public static void collapse(final View rootView) {
        final View mLinearLayout = rootView.findViewById(R.id.collapsed_wrapper);

        int finalHeight = mLinearLayout.getHeight();
        ValueAnimator mAnimator = slideAnimator(mLinearLayout, finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                mLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        ObjectAnimator rotate = ObjectAnimator.ofFloat(rootView.findViewById(R.id.headerArrow), "rotation", 90f, 0f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(mAnimator).with(rotate);
        animSet.start();
    }


    public static ValueAnimator slideAnimator(final View mLinearLayout, int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void hideView(final View view, int duration) {
        int x = (int) view.getX();
        int y = (int) view.getY();
        int radius = 400; //view.getRadius();
        int endRadius = 200;

        Log.w(TAG, "Hiding: " + view.getTag());
        Log.w(TAG, "X: " + x + "  ::  Y: " + y);

        radius = view.getHeight();
        endRadius = (int) view.getPivotX();


        Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y, radius, endRadius);
        anim.setDuration(duration);
        anim.setInterpolator(new BounceInterpolator());

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });

        anim.start();
    }

}
