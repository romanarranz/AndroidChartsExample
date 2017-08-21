package com.github.romanarranz.androiddynamicchartsexample;

import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.github.romanarranz.androiddynamicchartsexample.adapters.CategoryAdapter;
import com.github.romanarranz.androiddynamicchartsexample.fragments.DynamicPlotFragment;
import com.github.romanarranz.androiddynamicchartsexample.fragments.StaticPlotFragment;

public class MainActivity extends AppCompatActivity {

    private DynamicPlotFragment mDynamicPlot;
    private StaticPlotFragment mStaticPlot;

    private CategoryAdapter mAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        if (savedInstanceState != null) {
            mDynamicPlot = (DynamicPlotFragment) getSupportFragmentManager().findFragmentByTag(DynamicPlotFragment.DPF_URI);
            mStaticPlot = (StaticPlotFragment) getSupportFragmentManager().findFragmentByTag(StaticPlotFragment.SPF_URI);
        } else {
            mDynamicPlot = new DynamicPlotFragment();
            mStaticPlot = new StaticPlotFragment();
        }

        setupViewPager();
    }

    /**
     * Construir el ViewPager con las tabs de distintos Fragments
     */
    private void setupViewPager(){
        mAdapter = new CategoryAdapter(this, getSupportFragmentManager());
        mAdapter.addFragment(mStaticPlot, R.string.category_static_plot, StaticPlotFragment.SPF_URI);
        mAdapter.addFragment(mDynamicPlot, R.string.category_dynamic_plot, DynamicPlotFragment.DPF_URI);

        mViewPager.setAdapter(mAdapter);

        // after you set the adapter checking if view is laid out
        if (ViewCompat.isLaidOut(mTabLayout)) {
            setViewPagerListener();
        }
        else {
            mTabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    setViewPagerListener();
                    mTabLayout.removeOnLayoutChangeListener(this);
                }
            });
        }

        final ViewTreeObserver vto = mViewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeLayoutListenerPost16(mViewPager.getViewTreeObserver(), this);
            }
        });
    }

    /**
     * Eventos para cambiar de tab
     */
    private void setViewPagerListener() {
        mTabLayout.setupWithViewPager(mViewPager);
        // use class TabLayout.ViewPagerOnTabSelectedListener
        // note that it's a class not an interface as OnTabSelectedListener, so you can't implement it in your activity/fragment
        // methods are optional, so if you don't use them, you can not override them (e.g. onTabUnselected)
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }
        });
    }

    @RequiresApi(16)
    private void removeLayoutListenerPost16(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener){
        observer.removeOnGlobalLayoutListener(listener);
    }
}
