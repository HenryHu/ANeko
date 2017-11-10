package org.tamanegi.aneko;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class ANekoActivity extends PreferenceActivity
{
    int DRAW_OVER_OTHER_APPS = 1;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        addPreferencesFromResource(R.xml.pref);

        getPreferenceManager().findPreference(AnimationService.PREF_KEY_ENABLE)
                .setOnPreferenceClickListener(new OnEnableClickListener());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(String.format("package:%s", getPackageName())));
            startActivityForResult(intent, DRAW_OVER_OTHER_APPS);
        } else {
            startAnimationService();
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APPS) {
            if (Settings.canDrawOverlays(this)) {
                startAnimationService();
            }
        }
    }

    private void startAnimationService()
    {
        SharedPreferences.Editor edit =
            getPreferenceManager().getSharedPreferences().edit();
        edit.putBoolean(AnimationService.PREF_KEY_VISIBLE, true);
        edit.commit();

        startService(new Intent(this, AnimationService.class)
                     .setAction(AnimationService.ACTION_START));
    }

    private class OnEnableClickListener
        implements Preference.OnPreferenceClickListener
    {
        @Override
        public boolean onPreferenceClick(Preference pref)
        {
            startAnimationService();
            return true;
        }
    }
}
