package com.asappstudio.basesearchspinner;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.asappstudio.basesearchspinner.searchspinner.BaseSearchSpinner;
import com.example.basesearchspinner.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<GeneralModel> listCountry;
    BaseSearchSpinner spinnerCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCountry =  findViewById(R.id.spinner_country);

        listCountry = new ArrayList<GeneralModel>();
        listCountry.add(new GeneralModel("1231132","United States of America"));
        listCountry.add(new GeneralModel("4451132","United Kingdom"));
        listCountry.add(new GeneralModel("4452132","Canada"));
        listCountry.add(new GeneralModel("445242","Pakistan"));


        spinnerCountry.setPlaceholder(R.string.spinner_placeholder);
        spinnerCountry.setSearchHeaderText(getResources().getString(R.string.select_your_country));
        spinnerCountry.setSearchable(true);
        spinnerCountry.setEnableSearchHeader(true);
        spinnerCountry.setData(listCountry);
        spinnerCountry.setSpinnerBackground(R.drawable.spinner_bg_transparent);
        spinnerCountry.setOnValueChangedListener(new BaseSearchSpinner.OnValueChangedListener() {
            @Override
            public void onValueChanged() {
                if(spinnerCountry.getSelectedData().size() == 0) {
                    //Please select something
                }
                if(spinnerCountry.getSelectedData().size() > 0) {
                    GeneralModel generalModel= spinnerCountry.getSelectedData().get(0);
                    Toast.makeText(getApplicationContext(), generalModel.getName(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onValueChanged: ");

                }
            }
        });
    }
}