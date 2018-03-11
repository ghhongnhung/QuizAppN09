package com.example.nguye.quizappn09;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String GUESSES = "settings_numberOfGuesses";
    public static final String ANIMAL_TYPES = "settings_animalTypes";
    public static final String QUIZ_BACKGROUND_COLOR = "settings_quiz_background_color";
    public static final String QUIZ_FONT = "settings_quiz_font";

    private boolean isSettingsChanged = false;

    static Typeface UVNBanhMi;
    static Typeface UVNSachVo_R;
    static Typeface windsorb;

    MainActivityFragment animalQuizFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UVNBanhMi = Typeface.createFromAsset(getAssets(), "fonts/UVNBanhMi.TTF");
        UVNSachVo_R = Typeface.createFromAsset(getAssets(), "fonts/UVNSachVo_R.TTF");
        windsorb = Typeface.createFromAsset(getAssets(), "fonts/windsorb.ttf");

        PreferenceManager.setDefaultValues(MainActivity.this, R.xml.quiz_preferences, false);

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).registerOnSharedPreferenceChangeListener(settingsChangedListener);

        animalQuizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.animalQuizFragment);
        animalQuizFragment.editSoHangDoanTenDongVat(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        animalQuizFragment.suaLoaiDongVatTrongQuiz(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        animalQuizFragment.thayDoiFontChu(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        animalQuizFragment.editBackgroundColor(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        animalQuizFragment.resetQuiz();
        isSettingsChanged = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener settingsChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            isSettingsChanged = true;

            if (key.equals(GUESSES)) {

                animalQuizFragment.editSoHangDoanTenDongVat(sharedPreferences);
                animalQuizFragment.resetQuiz();
            } else if (key.equals(ANIMAL_TYPES)) {

                Set<String> animalTypes = sharedPreferences.getStringSet(ANIMAL_TYPES, null);

                if (animalTypes != null && animalTypes.size() > 0) {

                    animalQuizFragment.suaLoaiDongVatTrongQuiz(sharedPreferences);
                    animalQuizFragment.resetQuiz();
                } else {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    animalTypes.add(getString(R.string.default_animal_type));
                    editor.putStringSet(ANIMAL_TYPES, animalTypes);
                    editor.apply();

                    Toast.makeText(MainActivity.this, R.string.toast_message, Toast.LENGTH_SHORT).show();
                }
            } else if (key.equals(QUIZ_FONT)) {

                animalQuizFragment.thayDoiFontChu(sharedPreferences);
                animalQuizFragment.resetQuiz();
            } else if (key.equals(QUIZ_BACKGROUND_COLOR)) {

                animalQuizFragment.editBackgroundColor(sharedPreferences);
                animalQuizFragment.resetQuiz();
            }

            Toast.makeText(MainActivity.this, R.string.change_message, Toast.LENGTH_SHORT).show();

        }
    };
}
