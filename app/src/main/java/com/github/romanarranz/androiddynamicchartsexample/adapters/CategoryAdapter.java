package com.github.romanarranz.androiddynamicchartsexample.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by romanarranzguerrero on 22/8/17.
 */

public class CategoryAdapter extends PagerAdapter {

    private Context mContext;

    private List<Fragment> mFragmentList;
    private List<Integer> mFragmentTitles;
    private List<String> mFragmentTag;

    private FragmentManager mFragmentManager;

    /**
     * Create a new {@link CategoryAdapter} object.
     *
     * @param context is the context of the app
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public CategoryAdapter(Context context, FragmentManager fm) {
        mContext = context;
        mFragmentList = new ArrayList<>();
        mFragmentTitles = new ArrayList<>();
        mFragmentTag = new ArrayList<>();
        mFragmentManager = fm;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     *
     * @param position
     * @return
     */
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * Return the total number of pages.
     *
     * @return
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * Return true if the selected view is set up with the fragment else the blank screen will be displayed
     * https://stackoverflow.com/questions/30995446/what-is-the-role-of-isviewfromobject-view-view-object-object-in-fragmentst
     * https://stackoverflow.com/questions/7277892/instantiateitem-in-pageradapter-and-addview-in-viewpager-confusion/16772250#16772250
     *
     * @param view
     * @param fragment
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object fragment) {
        return ((Fragment) fragment).getView() == view;
    }

    /**
     * Return the {@link CharSequence} header of the selected page.
     *
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(mFragmentTitles.get(position)).toUpperCase(Locale.getDefault());
    }

    /**
     * Return {@link Fragment} the fragment which is instantiated inside the view of the screen using a fragment transaction
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Fragment instantiateItem(ViewGroup container, int position){
        Fragment fragment = getItem(position);
        String tag = mFragmentTag.get(position);

        if(mFragmentManager.findFragmentByTag(tag) == null) {
            mFragmentManager
                    .beginTransaction()
                    .add(container.getId(), fragment, tag)
                    .commit();
        }

        return fragment;
    }

    /**
     * Add a new fragment to this adapter with its title
     *
     * @param fragment
     * @param title
     */
    public void addFragment(Fragment fragment, int title, String tag){
        if(!mFragmentTag.contains(tag)) {
            mFragmentList.add(fragment);
            mFragmentTitles.add(title);
            mFragmentTag.add(tag);
        }
    }

    /**
     * Method overrided from PageAdapter which delete a fragment in function its position
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        mFragmentManager
                .beginTransaction()
                .remove(mFragmentList.get(position))
                .commit();

        mFragmentList.set(position, null);
        mFragmentTitles.set(position, null);
        mFragmentTag.set(position, null);
    }
}
