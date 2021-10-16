package com.asappstudio.basesearchspinner.searchspinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;


import com.asappstudio.basesearchspinner.Constants;
import com.asappstudio.basesearchspinner.GeneralModel;
import com.example.basesearchspinner.R;
import com.asappstudio.basesearchspinner.searchspinner.util.SoftKeyboardUtil;
import com.asappstudio.basesearchspinner.searchspinner.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseSearchSpinner extends AppCompatSpinner implements AdapterView.OnItemSelectedListener, SearchableSpinnerDialog.OnSearchDialogEventListener, Serializable {

    //Paint objects
    private TextPaint textPaint;

    private String searchHint;
    private String searchHeaderText;

    //AttributeSet
    private boolean headerVisible;
    private boolean enableHeader;
    private boolean isAutoSelectable;
    private boolean isShowKeyboardOnStart;
    private boolean isSearchable = false;
    private boolean isEnableSearchHeader;
    private boolean enableDismissSearch = false;
    private boolean flag = false;
    private boolean isDropdownShowing = false;
    private boolean isReSelectable = false;

    private int baseColor;
    private int headerColor;
    private int searchTextColor;
    private int searchBackgroundColor;
    private int searchHeaderTextColor;
    private int searchDropdownView;

    private CharSequence placeholder;
    private CharSequence headerText;

    private Drawable searchBackgroundDrawable;

    private Typeface typeface;

    public  OnValueChangedListener onValueChangedListener;
    private SearchableSpinnerDialog searchableSpinnerDialog;
    private OnItemSelectedListener onItemSelectedListener;
    private OnSpinnerEventListener spinnerEventsListener;

    private CustomSpinnerAdapter customSpinnerAdapter;

    private ArrayList<GeneralModel> spinnerListAll;
    private ArrayList<GeneralModel> spinnerListSelected = new ArrayList<>();


    /*
     * *****************
     * CONSTRUCTORS
     * *****************
     */
    public BaseSearchSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public BaseSearchSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public BaseSearchSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /*
     * ********************
     * Spinner Background
     * ********************
     */
    public void setSpinnerBackground(int drawableResource) {
        setBackgroundResource(drawableResource);//Can set upto 3 types of bg spinner_bg_with_underline spinner_bg_with_border spinner_bg_transparent
    }

    /*
     * ************************
     * CALL BACK METHOD
     * ************************
     */
    public interface OnValueChangedListener {
        public void onValueChanged();
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return onValueChangedListener;
    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
        onValueChangedListener = listener;
    }


    /*
     * **********************************************************************************
     * INITIALISATION METHODS
     * **********************************************************************************
     */

    private void init(Context context, AttributeSet attrs) {
        setOnItemSelectedListener(this);
        removeDefaultSelector(getBackground());
        initAttributes(context, attrs);
        initPaintObjects(context);
        configSearchableDialog();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray defaultArray = context.obtainStyledAttributes(new int[]{R.attr.colorControlNormal, R.attr.colorAccent});
        int defaultBaseColor = ContextCompat.getColor(context, R.color.base_color); // defaultArray.getColor(1, 0);
        defaultArray.recycle();

        TypedArray typedArray          =     context.obtainStyledAttributes(attrs, R.styleable.BaseSearchSpinner);
        baseColor                      =     typedArray.getColor(R.styleable.BaseSearchSpinner_baseColor, defaultBaseColor);
        placeholder                    =     typedArray.getString(R.styleable.BaseSearchSpinner_placeholder);
        headerText                     =     typedArray.getString(R.styleable.BaseSearchSpinner_headerText);
        enableHeader                   =     typedArray.getBoolean(R.styleable.BaseSearchSpinner_enableHeader, true);
        headerColor                    =     typedArray.getColor(R.styleable.BaseSearchSpinner_headerColor, baseColor);
        isSearchable                   =     typedArray.getBoolean(R.styleable.BaseSearchSpinner_isSearchable, false);
        isShowKeyboardOnStart          =     typedArray.getBoolean(R.styleable.BaseSearchSpinner_showKeyboardOnStart, false);
        isAutoSelectable               =     typedArray.getBoolean(R.styleable.BaseSearchSpinner_autoSelectable, false);
        isEnableSearchHeader           =     typedArray.getBoolean(R.styleable.BaseSearchSpinner_enableSearchHeader, false);
        searchHeaderText               =     typedArray.getString(R.styleable.BaseSearchSpinner_searchHeaderText);
        searchHeaderTextColor          =     typedArray.getColor(R.styleable.BaseSearchSpinner_searchHeaderTextColor, ContextCompat.getColor(context, R.color.search_header_text_color));
        int searchHeaderDrawableResId  =     typedArray.getResourceId(R.styleable.BaseSearchSpinner_searchHeaderBackgroundColor, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && searchHeaderDrawableResId != 0) {
            setSearchHeaderBackgroundColor(AppCompatResources.getDrawable(getContext(), searchHeaderDrawableResId));
        } else {
            setSearchHeaderBackgroundColor(typedArray.getColor(R.styleable.BaseSearchSpinner_searchHeaderBackgroundColor, ContextCompat.getColor(context, R.color.search_header_background)));
        }
        searchHint                     =     typedArray.getString(R.styleable.BaseSearchSpinner_searchHint);
        searchTextColor                =     typedArray.getColor(R.styleable.BaseSearchSpinner_searchTextColor, 0);
        int searchDrawableResId        =     typedArray.getResourceId(R.styleable.BaseSearchSpinner_searchBackgroundColor, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && searchDrawableResId != 0) {
            setSearchBackgroundColor(AppCompatResources.getDrawable(getContext(), searchDrawableResId));
        } else {
            setSearchBackgroundColor(typedArray.getColor(R.styleable.BaseSearchSpinner_searchBackgroundColor, ContextCompat.getColor(context, R.color.search_background)));
        }
        searchDropdownView             =     typedArray.getResourceId(R.styleable.BaseSearchSpinner_searchDropdownView, R.layout.search_list_item_layout);
        isReSelectable                 =     typedArray.getBoolean(R.styleable.BaseSearchSpinner_isReSelectable, false);
        enableDismissSearch            =     typedArray.getBoolean(R.styleable.BaseSearchSpinner_enableDismissSearch, false);
        typedArray.recycle();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void configSearchableDialog() {
        setSearchable(isSearchable);
        setShowKeyboardOnStart(isShowKeyboardOnStart);
        setEnableSearchHeader(isEnableSearchHeader);
        setSearchHeaderText(searchHeaderText);
        setSearchHeaderTextColor(searchHeaderTextColor);
        setSearchHint(searchHint);
        setSearchTextColor(searchTextColor);
        setSearchDropdownView(searchDropdownView);
        setSearchTypeFace(typeface);
        if (searchBackgroundColor != 0)
            setSearchBackgroundColor(searchBackgroundColor);
        else if (searchBackgroundDrawable != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setSearchBackgroundColor(searchBackgroundDrawable);
        }
        enableDismissSearch(enableDismissSearch);
    }

    private void removeDefaultSelector(Drawable drawable) {
        if (drawable instanceof LayerDrawable || drawable instanceof NinePatchDrawable || (drawable instanceof StateListDrawable && drawable.getCurrent() instanceof NinePatchDrawable)) {
            setBackgroundResource(R.drawable.transparent_color);
        }
    }

    public ArrayList<GeneralModel> getData() {
        int index = Constants.UNDEFINED;
        for (int i = 0; i < spinnerListAll.size(); i++) {
            GeneralModel obj = spinnerListAll.get(i);
            if (obj.getName().equals(placeholder.toString())) {
                index = spinnerListAll.indexOf(obj);
                break;
            }
        }
        if (index != Constants.UNDEFINED) {
            spinnerListAll.remove(index);
        }
        return spinnerListAll;
    }

    public void setData(ArrayList<GeneralModel> mainlist) {
        if (placeholder == null) {
            throw new AssertionError("setPlaceHolder() must be called before setData()");
        }
        spinnerListAll = new ArrayList(mainlist);
        searchableSpinnerDialog = SearchableSpinnerDialog.newInstance(this, spinnerListAll, isSearchable,isEnableSearchHeader,searchHeaderText,searchTextColor,searchBackgroundColor);
        if (spinnerListAll.stream().filter(object -> object.getName().equals(placeholder.toString())).findFirst().orElse(null) == null) {
            spinnerListAll.add(0, new GeneralModel(placeholder.toString(), placeholder.toString()));
        }
        customSpinnerAdapter = new CustomSpinnerAdapter(getContext(), spinnerListAll, isSearchable);
        setAdapter(customSpinnerAdapter);

    }

    public void showheader() {
        headerVisible = true;
    }

    public void hideheader() {
        headerVisible = false;
    }

    private void initPaintObjects(Context mContext) {
        int labelTextSize = getResources().getDimensionPixelSize(R.dimen.label_text_size);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(labelTextSize);
    }

    /*
     * **********************************************************************************
     * UTILITY METHODS
     * **********************************************************************************
     */

    public CustomSpinnerAdapter getCustomSpinnerAdapter() {
        return customSpinnerAdapter;
    }

    public ArrayList<GeneralModel> getSelectedData() {
        int index = Constants.UNDEFINED;
        for (int i = 0; i < spinnerListSelected.size(); i++) {
            GeneralModel obj = spinnerListSelected.get(i);
            if (obj.getName().equals(placeholder.toString())) {
                index = spinnerListSelected.indexOf(obj);
                break;
            }
        }
        if (index != Constants.UNDEFINED) {
            spinnerListSelected.remove(index);
        }
        return spinnerListSelected;
    }

    public void setSelectedData(ArrayList<GeneralModel> list) {
        if (list == null) {
            setSelection(Constants.UNDEFINED, true);
            return;
        }
        if (spinnerListAll == null) {
            setSelection(0, true);
            return;
        }
        this.spinnerListSelected = new ArrayList(list);
        if (list.size() > 0 && spinnerListAll.size() > 0) {
            int position = Constants.UNDEFINED;
            for (int i = 0; i < spinnerListAll.size(); i++) {
                if (list.get(0).id.equals(spinnerListAll.get(i).id)) {
                    position = i;
                    break;
                }
            }
            if (position != Constants.UNDEFINED) {
                setSelection(position, true);
            } else {
                selectPlaceholder();
            }
        }
    }

    public void selectPlaceholder() {
        setSelection(Constants.UNDEFINED, true);
    }

    public void setCustomSpinnerAdapter(CustomSpinnerAdapter customSpinnerAdapter) {
        this.customSpinnerAdapter = customSpinnerAdapter;
    }

    public CharSequence getHeaderText() {
        return this.headerText;
    }

    public void setHeaderText(CharSequence headerText) {
        this.headerText = headerText;
        invalidate();
    }

    public void setHeaderText(int resid) {
        String headerText = getResources().getString(resid);
        setHeaderText(headerText);
    }

    public boolean isEnableHeader() {
        return enableHeader;
    }

    public void setEnableHeader(boolean enableHeader) {
        this.enableHeader = enableHeader;
        invalidate();
    }

    public CharSequence getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(CharSequence placeholder) {
        this.placeholder = placeholder;
        if (spinnerListAll != null) {
            throw new AssertionError("setData() must be called after setPlaceHolder()");
        }
        invalidate();
    }

    public void setPlaceholder(int resid) {
        CharSequence placeholder = getResources().getString(resid);
        if (spinnerListAll != null) {
            throw new AssertionError("setData() must be called after setPlaceHolder()");
        }
        setPlaceholder(placeholder);
    }

    private AppCompatActivity scanForActivity(Context context) {
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    /*
     * *******************
     * DRAWING METHODS
     * *******************
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Header Drawing
        if ((headerText != null) && enableHeader) {
            textPaint.setColor(headerColor);
            String textToDraw = headerText.toString();
            canvas.drawText(textToDraw, textPaint.getTextSize() - 1, 35, textPaint);
        }
    }

    /*
     * **********************************************************************************
     * LISTENER METHODS
     * **********************************************************************************
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            SoftKeyboardUtil.hideSoftKeyboard(getContext());
            AppCompatActivity appCompatActivity = scanForActivity(getContext());
            if (appCompatActivity != null) {
                appCompatActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                View view = appCompatActivity.getCurrentFocus();
                if (view instanceof EditText) {
                    view.clearFocus();
                    SoftKeyboardUtil.hideSoftKeyboard(getContext());
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled() && event.getAction() == MotionEvent.ACTION_UP) {
            return performClick();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        flag = true;
            AppCompatActivity appCompatActivity = scanForActivity(getContext());
            if (appCompatActivity != null) {
                FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                if (!isDropdownShowing()) {
                    isDropdownShowing = true;
                    searchableSpinnerDialog.show(fragmentManager, "TAG");
                }
                if (spinnerEventsListener != null) {
                    spinnerEventsListener.onSpinnerOpened(BaseSearchSpinner.this);
                }
                invalidate();
                return true;
            }
        isDropdownShowing = true;
        if (spinnerEventsListener != null) {
            spinnerEventsListener.onSpinnerOpened(this);
        }
        invalidate();
        return super.performClick();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (isDropdownShowing() && hasWindowFocus) {
            dismiss();
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }

    public void setOnSpinnerEventListener(OnSpinnerEventListener onSpinnerEventListener) {
        this.spinnerEventsListener = onSpinnerEventListener;
    }

    public void dismiss() {
        isDropdownShowing = false;
        if (spinnerEventsListener != null) {
            spinnerEventsListener.onSpinnerClosed(this);
        }
        invalidate();
    }

    public boolean isDropdownShowing() {
        return isDropdownShowing;
    }

    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    @Override
    public GeneralModel getSelectedItem() {
        return (GeneralModel) super.getSelectedItem();
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        if (searchableSpinnerDialog != null) {
            if (position == Constants.UNDEFINED) {
                return;
            }
            searchableSpinnerDialog.setSelectedPosition(position);
            checkReSelectable(position);
        }
    }

    private void checkReSelectable(int position) {
        onItemSelectedListener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        if (onItemSelectedListener == null) {
            this.onItemSelectedListener = listener;
            super.setOnItemSelectedListener(onItemSelectedListener);
        } else {
            this.onItemSelectedListener = listener;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isSearchable) {
            SoftKeyboardUtil.hideSoftKeyboard(getContext());
            setSearchSelectedPosition(position);
        }
        spinnerListSelected.clear();
        spinnerListSelected.add(new GeneralModel(spinnerListAll.get(position).id, spinnerListAll.get(position).name));
        if (spinnerListSelected.stream().filter(object -> object.getName().equals(placeholder.toString())).findFirst().orElse(null) != null) {
            setSelectedData(null);
            if (!isSearchable) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
            }
            onValueChangedListener.onValueChanged();
            return;
        }
        if (!isSearchable) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        }
        if (flag) {
            flag = false;
            onValueChangedListener.onValueChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onNothingSelected(parent);
        }
    }

    @Override
    public void onSearchItemSelected(GeneralModel item, int position) {
        int selectedIndex = spinnerListAll.indexOf(item);
        if (position >= 0) {
            setSelection(selectedIndex, false);
        }
    }

    @Override
    public void onSearchableSpinnerDismiss() {
        dismiss();
    }

    /*
     * *********************
     * GETTERS AND SETTERS
     * *********************
     */
    public int getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
        invalidate();
    }

    public boolean isAutoSelectable() {
        return isAutoSelectable;
    }

    public void setAutoSelectable(boolean autoSelectable) {
        this.isAutoSelectable = autoSelectable;
        invalidate();
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        if (typeface != null) {

            setSearchTypeFace(typeface);
        }
        invalidate();
    }


    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            invalidate();
        }
        super.setEnabled(enabled);
    }

    public boolean isSearchable() {
        return isSearchable;
    }

    public void setSearchable(boolean searchable) {
        this.isSearchable = searchable;
        invalidate();
    }

    public boolean isShowKeyboardOnStart() {
        return isShowKeyboardOnStart;
    }

    public void setShowKeyboardOnStart(boolean showKeyboardOnStart) {
        this.isShowKeyboardOnStart = showKeyboardOnStart;
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setShowKeyboardOnStart(showKeyboardOnStart);
        }
        invalidate();
    }

    public boolean isEnableSearchHeader() {
        return isEnableSearchHeader;
    }

    public void setEnableSearchHeader(boolean isEnableSearchHeader) {
        this.isEnableSearchHeader = isEnableSearchHeader;
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setEnableSearchHeader(isEnableSearchHeader);
        }
        invalidate();
    }

    public String getSearchHeaderText() {
        return searchHeaderText;
    }

    public void setSearchHeaderText(String searchHeaderText) {
        this.searchHeaderText = searchHeaderText;
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchHeaderText(searchHeaderText);
        }
        invalidate();
    }

    public int getSearchHeaderTextColor() {
        return searchHeaderTextColor;
    }

    public void setSearchHeaderTextColor(int color) {
        this.searchHeaderTextColor = color;
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchHeaderTextColor(color);
        }
        invalidate();
    }

    public void setSearchHeaderBackgroundColor(int color) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchHeaderBackgroundColor(color);
        }
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setSearchHeaderBackgroundColor(Drawable drawable) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchHeaderBackgroundColor(drawable);
        }
        invalidate();
    }

    public String getSearchHint() {
        return searchHint;
    }

    public void setSearchDropdownView(int viewId) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchDropdownView(viewId);
        }
        invalidate();
    }

    public void setSearchBackgroundColor(int color) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchBackgroundColor(color);
        }
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setSearchBackgroundColor(Drawable drawable) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchBackgroundColor(drawable);
        }
        invalidate();
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchHint(searchHint);
        }
        invalidate();
    }

    public void setSearchTextColor(int color) {
        this.searchTextColor = color;
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSearchTextColor(color);
        }
        invalidate();
    }

    private void setSearchSelectedPosition(int position) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setSelectedPosition(position);
        }
        invalidate();
    }

    public void setSearchDialogGravity(int gravity) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setGravity(gravity);
        }
        invalidate();
    }

    public void setSearchTypeFace(Typeface typeFace) {
        if (searchableSpinnerDialog != null) {
            searchableSpinnerDialog.setTypeface(typeFace);
        }
        invalidate();
    }

    public boolean isEnableDismissSearch() {
        return enableDismissSearch;
    }

    public void setEnableDismissSearch(boolean enableDismissSearch) {
        this.enableDismissSearch = enableDismissSearch;
        enableDismissSearch(enableDismissSearch);
        invalidate();
    }

    private void enableDismissSearch(boolean enableDismissSearch) {
        if (searchableSpinnerDialog != null)
            searchableSpinnerDialog.setEnableDismissSearch(enableDismissSearch);
    }

    public boolean isReSelectable() {
        return isReSelectable;
    }

    public void setReSelectable(boolean reSelectable) {
        isReSelectable = reSelectable;
    }

    /**
     * Listening for open/closed events.
     */
    public interface OnSpinnerEventListener {
        void onSpinnerOpened(BaseSearchSpinner spinner);
        void onSpinnerClosed(BaseSearchSpinner spinner);
    }

    public static class CustomSpinnerAdapter extends BaseAdapter implements Filterable {
        private final Context context;
        private final ArrayList<GeneralModel> values;
        boolean isSearchable;

        private List<GeneralModel> itemListFiltered;
        public CustomSpinnerAdapter(Context context, ArrayList<GeneralModel> values) {
            this.context = context;
            this.values = values;
        }

        public CustomSpinnerAdapter(Context context, ArrayList<GeneralModel> values,boolean isSearchable) {
            this.context = context;
            this.values = values;
            this.isSearchable=isSearchable;
            this.itemListFiltered = values;
        }

        public int getCount() {
            if (isSearchable) {
                return itemListFiltered != null ? itemListFiltered.size() : 0;
            }
            else {
                return values.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (isSearchable) {
                return itemListFiltered != null ? itemListFiltered.get(position) : null;
            }
            else {
                return values.get(position);
            }

        }


        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(context);
            text.setTextColor(Color.BLACK);
            text.setTextSize(18);
            if (isSearchable){
                text.setText(itemListFiltered.get(position).getName());
            }
            else {
                text.setText(values.get(position).getName());
            }
            text.setPadding(15, 5, 15, 5);
            return text;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.GRAY);
            label.setTextSize(18);
            if (isSearchable){
                label.setText(itemListFiltered.get(position).getName());
            }
            else {
                label.setText(values.get(position).getName());
            }
            label.setPadding(15, 5, 15, 5);
            return label;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence != null ? charSequence.toString() : null;
                    if (charString == null || charString.isEmpty()) {
                        itemListFiltered = values;
                    } else {
                        List<GeneralModel> filteredList = new ArrayList<>();
                        String searchText = StringUtils.removeDiacriticalMarks(charString).toLowerCase();
                        for (GeneralModel row : values) {
                            String item = StringUtils.removeDiacriticalMarks(row.getName().toString()).toLowerCase();
                            if (item.contains(searchText)) {
                                filteredList.add(row);
                            }
                        }
                        itemListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = itemListFiltered;
                    filterResults.count = itemListFiltered.size();
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    itemListFiltered = (List<GeneralModel>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}