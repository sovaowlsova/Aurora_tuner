package com.sovaowlsova.auroratuner.core.util;

import androidx.fragment.app.Fragment;

public class FragmentFactory {
    public static Fragment create(Class<? extends Fragment> fragmentClass) {
        try {
            return fragmentClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create fragment: " + fragmentClass.getSimpleName() + " because: " + e.getMessage());
        }
    }
}
