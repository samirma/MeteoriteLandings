package com.antonio.samir.meteoritelandingsspots.ui.animator;

import android.animation.Animator;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.ui.fragments.MeteoriteDetailFragment;

public abstract class AnimatePhotoChanger {

    private static final int DURATION = 500;
    protected final AnimateParam animateParam;
    private MeteoriteDetailFragment newInstance;

    public AnimatePhotoChanger(AnimateParam animateParam, MeteoriteDetailFragment newInstance) {
        this.animateParam = animateParam;
        this.newInstance = newInstance;
    }

    public void start() {

        animateParam.startAnimate
                .setDuration(DURATION)
                .setInterpolator(animateParam.interpolator)
                .translationXBy(animateParam.width)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        beforeSecondAnimation();
                        animateParam.endAnimate
                                .setInterpolator(animateParam.interpolator)
                                .setDuration(DURATION)
                                .translationX(0)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        secondAnimationStart();
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    protected abstract void beforeSecondAnimation();

    protected void secondAnimationStart() {
        final FragmentTransaction fragmentTransaction = ((AppCompatActivity)animateParam.context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment, newInstance);
        fragmentTransaction.commit();
    }

    public void replace(){
        final FragmentTransaction fragmentTransaction = ((AppCompatActivity)animateParam.context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, newInstance);
        fragmentTransaction.commit();
    }

}
