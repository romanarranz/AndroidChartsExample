package com.github.romanarranz.androiddynamicchartsexample.helpers;

import android.os.Bundle;
import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.Map;

import icepick.Bundler;

/**
 * Created by romanarranzguerrero on 22/8/17.
 */

public class MapBundler implements Bundler<Map<Float, Float>> {

    @Override
    public void put(String s, Map<Float, Float> floatFloatMap, Bundle bundle) {
        Parcelable mapParcelable = Parcels.wrap(floatFloatMap);
        bundle.putParcelable(s, mapParcelable);
    }

    @Override
    public Map<Float, Float> get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
