package com.aluminati.inventory.fragments.languageSelect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aluminati.inventory.R;

import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LanguageSelection extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = LanguageSelection.class.getName();
    private String[] languages={"English","Polish"};
    private String[] lang = {"en","pl"};
    int flags[] = {R.drawable.flag_united_kingdom,R.drawable.flag_poland};


    public LanguageSelection(){

    }

    public static LanguageSelection newInstance(String title) {
        LanguageSelection languageSelection = new LanguageSelection();
        Bundle args = new Bundle();
        args.putString("title", title);
        languageSelection.setArguments(args);
        return languageSelection;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(getResources().getLayout(R.layout.language_select), container ,false);


        Spinner spinner = view.findViewById(R.id.language_selection_spinner);
                spinner.setOnItemSelectedListener(this);


                view.findViewById(R.id.confirm_language_selection).setOnClickListener(language -> {
                         changeLang(spinner.getSelectedItemPosition());
                });

        LanguageCustomAdapter languageCustomAdapter = new LanguageCustomAdapter(getApplicationContext(), flags, languages);
        spinner.setAdapter(languageCustomAdapter);
        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }



    public void changeLang(int index) {
        Configuration config = getActivity().getBaseContext().getResources().getConfiguration();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {

            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            ed.putString("local_lang", lang[index]);
            ed.commit();

            Locale locale = new Locale(lang[index]);
            Locale.setDefault(locale);
            Configuration conf = new Configuration(config);
            conf.locale = locale;
            getActivity().getBaseContext().getResources().updateConfiguration(conf, getActivity().getBaseContext().getResources().getDisplayMetrics());

            Intent intent = getActivity().getIntent();
            getActivity().overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        }
    }



}
