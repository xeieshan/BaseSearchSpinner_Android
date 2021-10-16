package com.asappstudio.basesearchspinner.searchspinner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;

import com.asappstudio.basesearchspinner.GeneralModel;
import com.example.basesearchspinner.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchableSpinnerDialog extends DialogFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String INSTANCE_LIST_ITEMS = "ListItems";
    private static final String INSTANCE_LISTENER_KEY = "OnSearchDialogEventListener";
    private static final String INSTANCE_SPINNER_KEY = "SmartMaterialSpinner";
    private static final String SEARCHABLE_KEY = "searchable";
    private static final String isEnableSearchHeader_KEY = "isEnableSearchHeader";
    private static final String searchHeaderText_KEY = "searchHeaderText";
    private static final String searchTextColor_KEY = "searchTextColor";
    private static final String searchBackgroundColor_KEY = "searchBackgroundColor";

    private boolean isShowKeyboardOnStart;
    private boolean isEnableSearchHeader;
    private boolean isDismissOnSelected = true;
    private boolean isSearchable;
    private boolean enableDismissSearch;

    private int searchDialogGravity = Gravity.CENTER;
    private int searchHeaderTextColor;
    private int selectedPosition = -1;
    private int headerBackgroundColor;
    private int searchDropdownView;
    private int searchBackgroundColor;
    private int searchTextColor;

    private Drawable headerBackgroundDrawable;
    private Drawable searchBackgroundDrawable;

    private GeneralModel selectedItem;

    private String searchHeaderText;
    private String searchHint;
    private String dismissSearchText;

    private Typeface typeface;

    public BaseSearchSpinner.CustomSpinnerAdapter searchArrayAdapter;
    public Button btnDismiss;

    private ViewGroup searchHeaderView;
    private AppCompatTextView tvSearchHeader;
    private SearchView searchView;
    private TextView tvSearch;
    private ListView searchListView;
    private LinearLayout itemListContainer;

    private OnSearchDialogEventListener onSearchDialogEventListener;
    private OnSearchTextChanged onSearchTextChanged;
    private BaseSearchSpinner baseSearchSpinner;


    public SearchableSpinnerDialog() {
    }

    public static SearchableSpinnerDialog newInstance(BaseSearchSpinner baseSearchSpinner, List<GeneralModel> items, boolean isSearchable, boolean isEnableSearchHeader, String searchHeaderText, int searchTextColor, int searchBackgroundColor) {
        SearchableSpinnerDialog searchableSpinnerDialog = new SearchableSpinnerDialog();
        Bundle args = new Bundle();
        args.putSerializable(INSTANCE_LIST_ITEMS, (Serializable) items);
        args.putSerializable(INSTANCE_SPINNER_KEY, baseSearchSpinner);
        args.putSerializable(SEARCHABLE_KEY, isSearchable);
        args.putSerializable(isEnableSearchHeader_KEY, isEnableSearchHeader);
        args.putSerializable(searchHeaderText_KEY, searchHeaderText);
        args.putSerializable(searchTextColor_KEY, searchTextColor);
        args.putSerializable(searchBackgroundColor_KEY, searchBackgroundColor);

        searchableSpinnerDialog.setArguments(args);
        return searchableSpinnerDialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState = setSavedInstanceState(outState);
        outState.putSerializable(INSTANCE_LISTENER_KEY, outState.getSerializable(INSTANCE_LISTENER_KEY));
        outState.putSerializable(INSTANCE_SPINNER_KEY, outState.getSerializable(INSTANCE_SPINNER_KEY));
        outState.putSerializable(INSTANCE_LIST_ITEMS, outState.getSerializable(INSTANCE_LIST_ITEMS));
        outState.putSerializable(SEARCHABLE_KEY, outState.getSerializable(SEARCHABLE_KEY));
        outState.putSerializable(isEnableSearchHeader_KEY, outState.getSerializable(isEnableSearchHeader_KEY));
        outState.putSerializable(searchHeaderText_KEY, outState.getSerializable(searchHeaderText_KEY));
        outState.putSerializable(searchTextColor_KEY, outState.getSerializable(searchTextColor_KEY));
        outState.putSerializable(searchBackgroundColor_KEY, outState.getSerializable(searchBackgroundColor_KEY));
        super.onSaveInstanceState(outState);
    }

    private Bundle setSavedInstanceState(Bundle savedInstanceState) {
        Bundle dialogInstanceState = this.getArguments();
        if (savedInstanceState == null || savedInstanceState.isEmpty() && dialogInstanceState != null) {
            savedInstanceState = dialogInstanceState;
        }
        return savedInstanceState;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        savedInstanceState = setSavedInstanceState(savedInstanceState);
        this.baseSearchSpinner = (BaseSearchSpinner) savedInstanceState.get(INSTANCE_SPINNER_KEY);
        this.isSearchable = (boolean) savedInstanceState.get(SEARCHABLE_KEY);
        this.isEnableSearchHeader = (boolean) savedInstanceState.get(isEnableSearchHeader_KEY);
        this.searchHeaderTextColor = (int) savedInstanceState.get(searchTextColor_KEY);
        this.searchHeaderText = (String) savedInstanceState.get(searchHeaderText_KEY);
        this.headerBackgroundColor = (int) savedInstanceState.get(searchBackgroundColor_KEY);
        this.onSearchDialogEventListener = baseSearchSpinner;
        savedInstanceState.putSerializable(INSTANCE_LISTENER_KEY, onSearchDialogEventListener);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        savedInstanceState = setSavedInstanceState(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (savedInstanceState != null) {
            onSearchDialogEventListener = (BaseSearchSpinner) savedInstanceState.getSerializable(INSTANCE_LISTENER_KEY);
        }
        View searchLayout = inflater.inflate(R.layout.searchable_dialog_layout, null);
        initSearchDialog(searchLayout, savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(searchLayout);

        AlertDialog dialog = builder.create();
        setGravity(dialog);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        savedInstanceState = setSavedInstanceState(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initSearchDialog(View rootView, Bundle savedInstanceState) {
        searchHeaderView = rootView.findViewById(R.id.search_header_layout);
        tvSearchHeader = rootView.findViewById(R.id.tv_search_header);
        searchView = rootView.findViewById(R.id.search_view);
        tvSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchListView = rootView.findViewById(R.id.search_list_item);
        itemListContainer = rootView.findViewById(R.id.item_search_list_container);
        btnDismiss = rootView.findViewById(R.id.btn_dismiss);

        if (getActivity() != null) {
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            }
        }
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        if (isShowKeyboardOnStart) {
            searchView.requestFocus();
        } else {
            searchView.clearFocus();
        }

        ArrayList<GeneralModel> items = savedInstanceState != null ? (ArrayList<GeneralModel>) savedInstanceState.getSerializable(INSTANCE_LIST_ITEMS) : null;
        if (items != null) {
            searchArrayAdapter = new BaseSearchSpinner.CustomSpinnerAdapter(getActivity(), items, isSearchable);

        }
        searchListView.setAdapter(searchArrayAdapter);
        searchListView.setTextFilterEnabled(true);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onSearchDialogEventListener != null) {
                    selectedItem = (GeneralModel) searchArrayAdapter.getItem(position);
                    onSearchDialogEventListener.onSearchItemSelected((GeneralModel) searchArrayAdapter.getItem(position), position);

                }
                getDialog().dismiss();
            }
        });

        searchListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    scrollToSelectedItem();
                } else if (bottom > oldBottom) {
                    scrollToSelectedItem();
                }
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        initSearchHeader();
        initSearchBody();
        initSearchFooter();
    }

    private void initSearchHeader() {
        if (isEnableSearchHeader) {
            searchHeaderView.setVisibility(View.VISIBLE);
        } else {
            searchHeaderView.setVisibility(View.GONE);
        }
        if (isSearchable) {
            searchView.setVisibility(View.VISIBLE);
        } else {
            searchView.setVisibility(View.GONE);
        }

        if (searchHeaderText != null) {
            tvSearchHeader.setText(searchHeaderText);
            tvSearchHeader.setTypeface(typeface);
        }

        if (searchHeaderTextColor != 0) {
            tvSearchHeader.setTextColor(searchHeaderTextColor);
        }

        if (headerBackgroundColor != 0) {
            searchHeaderView.setBackgroundColor(headerBackgroundColor);
        } else if (headerBackgroundDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                searchHeaderView.setBackground(headerBackgroundDrawable);
            }
        }
    }

    private void initSearchBody() {
        if (searchHint != null) {
            searchView.setQueryHint(searchHint);
        }
        if (searchBackgroundColor != 0) {
            searchView.setBackgroundColor(searchBackgroundColor);
        } else if (searchBackgroundDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                searchView.setBackground(searchBackgroundDrawable);
            }
        }
        if (tvSearch != null) {
            tvSearch.setTypeface(typeface);
            if (searchTextColor != 0) {
                tvSearch.setTextColor(searchTextColor);
            }
        }
    }

    private void initSearchFooter() {
        if (enableDismissSearch)
            btnDismiss.setVisibility(View.VISIBLE);
        if (dismissSearchText != null)
            btnDismiss.setText(dismissSearchText);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (onSearchDialogEventListener != null) {
            onSearchDialogEventListener.onSearchableSpinnerDismiss();
        }
        super.onDismiss(dialog);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            ((BaseSearchSpinner.CustomSpinnerAdapter) searchListView.getAdapter()).getFilter().filter(null);
        } else {
            ((BaseSearchSpinner.CustomSpinnerAdapter) searchListView.getAdapter()).getFilter().filter(s);
        }
        if (onSearchTextChanged != null) {
            onSearchTextChanged.onSearchTextChanged(s);
        }
        return true;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    public interface OnSearchDialogEventListener extends Serializable {
        void onSearchItemSelected(GeneralModel item, int position);

        void onSearchableSpinnerDismiss();
    }

    public interface OnSearchTextChanged {
        void onSearchTextChanged(String strText);
    }

    public void setOnSearchDialogEventListener(OnSearchDialogEventListener onSearchDialogEventListener) {
        this.onSearchDialogEventListener = onSearchDialogEventListener;
    }

    public void setOnSearchTextChangedListener(OnSearchTextChanged onSearchTextChanged) {
        this.onSearchTextChanged = onSearchTextChanged;
    }

    public void setEnableSearchHeader(boolean enableSearchHeader) {
        isEnableSearchHeader = enableSearchHeader;
    }

    public void setShowKeyboardOnStart(boolean showKeyboardOnStart) {
        isShowKeyboardOnStart = showKeyboardOnStart;
    }

    public void setSearchHeaderText(String header) {
        searchHeaderText = header;
    }

    public void setSearchHeaderTextColor(int color) {
        this.searchHeaderTextColor = color;
    }

    public void setSearchHeaderBackgroundColor(int color) {
        headerBackgroundColor = color;
        headerBackgroundDrawable = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setSearchHeaderBackgroundColor(Drawable drawable) {
        headerBackgroundDrawable = drawable;
        headerBackgroundColor = 0;
    }

    public int getSearchDropdownView() {
        return searchDropdownView;
    }

    public void setSearchDropdownView(int searchDropdownView) {
        this.searchDropdownView = searchDropdownView;
    }

    public void setSearchBackgroundColor(int color) {
        searchBackgroundColor = color;
        searchBackgroundDrawable = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setSearchBackgroundColor(Drawable drawable) {
        searchBackgroundDrawable = drawable;
        searchBackgroundColor = 0;
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
    }

    public void setSearchTextColor(int color) {
        searchTextColor = color;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public void setGravity(int gravity) {
        this.searchDialogGravity = gravity;
    }

    private void setGravity(Dialog dialog) {
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setGravity(searchDialogGravity);
        }
    }

    private void scrollToSelectedItem() {
        if (selectedPosition >= 0 && searchListView.isSmoothScrollbarEnabled()) {
            searchListView.smoothScrollToPositionFromTop(selectedPosition, 0, 10);
        }
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public boolean isEnableDismissSearch() {
        return enableDismissSearch;
    }

    public void setEnableDismissSearch(boolean enableDismissSearch) {
        this.enableDismissSearch = enableDismissSearch;
    }

    public String getDismissSearchText() {
        return dismissSearchText;
    }

    public void setDismissSearchText(String dismissSearchText) {
        this.dismissSearchText = dismissSearchText;
    }

}
