# BaseSearchSpinner_Android
A simple and easy to use, highly customizable Searchable Spinner for Android Developers, written in Java. This spinner is using Dialog.

### Include the spinner object in XML
```xml
<com.example.basesearchspinner.searchspinner.BaseSearchSpinner
    android:id="@+id/spinner_country"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center_vertical"
    android:layout_marginStart="10dp"
    android:layout_marginTop="10dp"
    android:layout_marginEnd="10dp"
    android:layout_marginBottom="10dp"
    app:baseColor="@color/black"
    app:enableHeader="true"
    app:headerColor="#000000"
    app:headerText="Select your country"
    app:placeholder="Select"/>
```

### Declare Properties
```android
    ArrayList<GeneralModel> listCountry;
    BaseSearchSpinner spinnerCountry;
```

### Load the spinner object in Java class
```android
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
                //Please select something Toast
                Toast.makeText(getApplicationContext(), "Please select Country", Toast.LENGTH_SHORT).show();
            }
            if(spinnerCountry.getSelectedData().size() > 0) {
                GeneralModel generalModel= spinnerCountry.getSelectedData().get(0);
                Toast.makeText(getApplicationContext(), generalModel.getName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onValueChanged: ");
            }
        }
    });
```
*listCountry is the meta data to show on spinnerCountry*
*spinnerCountry.setSpinnerBackground(R.drawable.spinner_bg_transparent); We have 3 kinds of background options*
*spinner_bg_with_underline.xml / spinner_bg_with_border.xml / spinner_bg_transparent.xml*

### Validations very simple
```android
    btnSubmit.setOnClickListener(view -> {
        if (spinnerCountry.getSelectedData().size() <= 0) {
            showSingleToast("Please select a country");
        } else {
            exit();
        }
    });
```

### Unselect
```android
    btnSubmit.selectPlaceholder();
```
*This will also invoke onValueChanged method*

### Image
#### Simple Spinner With Dialog
![Simple Spinner With Dialog](https://github.com/xeieshan/BaseSearchSpinner_Android/blob/main/Spinner%20with%20Dialog.jpeg)
#### Search Spinner With Dialog
![Search Spinner With Dialog](https://github.com/xeieshan/BaseSearchSpinner_Android/blob/main/Search%20Spinner%20With%20Dialog.jpeg)
