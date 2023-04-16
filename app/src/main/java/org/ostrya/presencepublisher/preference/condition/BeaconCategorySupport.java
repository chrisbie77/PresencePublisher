package org.ostrya.presencepublisher.preference.condition;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import org.ostrya.presencepublisher.R;
import org.ostrya.presencepublisher.preference.common.AbstractConfigurationFragment;
import org.ostrya.presencepublisher.preference.common.AbstractDynamicPreferenceCategorySupport;
import org.ostrya.presencepublisher.preference.common.StringDummy;

public class BeaconCategorySupport extends AbstractDynamicPreferenceCategorySupport {
    public static final String BEACON_LIST = "beacons";
    public static final String BEACON_CONTENT_PREFIX = "beacon.";

    private static final String TAG = "BeaconCategorySupport";

    public BeaconCategorySupport(
            AbstractConfigurationFragment fragment,
            @Nullable ActivityResultLauncher<String> intentLauncher) {
        super(
                fragment,
                R.string.category_beacon_regions,
                BEACON_LIST,
                BEACON_CONTENT_PREFIX,
                (c, p, f) -> createAdderEntry(c, f, intentLauncher),
                BeaconCategorySupport::createEntry);
    }

    private static Preference createAdderEntry(
            Context context,
            Fragment fragment,
            @Nullable ActivityResultLauncher<String> intentLauncher) {
        if (intentLauncher != null) {
            return new AddBeaconChoicePreferenceDummy(context, fragment, intentLauncher);
        } else {
            return new StringDummy(context, R.string.no_bluetooth_explanation);
        }
    }

    private static Preference createEntry(
            Context context,
            String key,
            String title,
            SharedPreferences preferences,
            Fragment fragment) {
        return new BeaconPreference(context, key, title, fragment);
    }

    public void clickAdd() {
        @Nullable
        Preference adderPreference =
                getPreferenceManager()
                        .findPreference(AddBeaconChoicePreferenceDummy.class.getCanonicalName());
        if (adderPreference != null) {
            adderPreference.getOnPreferenceClickListener().onPreferenceClick(adderPreference);
        }
    }
}