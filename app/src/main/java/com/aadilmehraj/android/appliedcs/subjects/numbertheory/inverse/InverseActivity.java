package com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aadilmehraj.android.appliedcs.R;
import com.aadilmehraj.android.appliedcs.databinding.ActivityInverseBinding;
import com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse.InverseUtils.Number;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinner.OnItemSelectedListener;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class InverseActivity extends AppCompatActivity implements LoaderCallbacks<List<Number>> {

    private static final String TAG = InverseActivity.class.getSimpleName();
    public static final int INVERSE_LOADER_ID = 12;
    private static final String MOD_KEY = "mod";
    private static final String SPINNER_KEY = "spinner-key";
    private static final String METHOD_KEY = "method-key";
    private int mMethodType;

    /**
     * Methods for calculating the inverses
     */
    private final static String[] METHOD_TYPES = new String[]{
        "Tabular Method",
        "Extended Euclid's Algorithm",
        "Galois Field of n = 3",
        "Galois Field of n = 8"
    };


    ActivityInverseBinding mInverseBinding;
    private ArrayList<Number> mNumbers;
    private long mModN;


    @BindView(R.id.inverse_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.number_text_view)
    TextView mNumberLabel;
    @BindView(R.id.mul_inverse_text_view)
    TextView mMultiplicativeInverseLabel;
    @BindView(R.id.add_inverse_text_view)
    TextView mAdditiveInverseLabel;
    @BindView(R.id.inverse_label_layout)
    FrameLayout mFrameLayout;
    @BindView(R.id.loading_progress_bar)
    ProgressBar mLoadingBar;

    InverseAdapter mAdapter;
    private String INVERSE_KEY = "inverse.key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new DebugTree());


        mInverseBinding = DataBindingUtil.setContentView(this, R.layout.activity_inverse);
        ButterKnife.bind(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(SPINNER_KEY)) {
            mMethodType = savedInstanceState.getInt(SPINNER_KEY);
            if (mMethodType == 2 || mMethodType == 3) {
                mInverseBinding.modulusEditText.setEnabled(false);
            }
        }


        if (savedInstanceState != null && savedInstanceState.containsKey(INVERSE_KEY)) {
            mNumbers = savedInstanceState.getParcelableArrayList(INVERSE_KEY);
        }

        initLabels();
        mInverseBinding.spinner.setItems(METHOD_TYPES);
        mInverseBinding.spinner
            .setOnItemSelectedListener(new OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id,
                    String item) {
                    mMethodType = position;
                    if (position == 2) {
                        mInverseBinding.modulusEditText.setText("3");
                        mInverseBinding.modulusEditText.setEnabled(false);
                    } else if (position == 3) {
                        mInverseBinding.modulusEditText.setText("8");
                        mInverseBinding.modulusEditText.setEnabled(false);
                    } else {
                        mInverseBinding.modulusEditText.setText("");
                        mInverseBinding.modulusEditText.setEnabled(true);
                    }
                }
            });
        mInverseBinding.spinner.setSelectedIndex(mMethodType);
        mFrameLayout.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new InverseAdapter(this);
        mAdapter.setAdapterData(mNumbers);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }


    /**
     * Utility method that initializes the Label of number, additive inverse and multiplicative
     * inverse.
     */
    private void initLabels() {
        mNumberLabel.setText("w");

        mNumberLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mAdditiveInverseLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mMultiplicativeInverseLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        mNumberLabel.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
        mAdditiveInverseLabel.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);
        mMultiplicativeInverseLabel.setTypeface(Typeface.SERIF, Typeface.BOLD_ITALIC);

        mNumberLabel.setTextColor(Color.WHITE);
        mAdditiveInverseLabel.setTextColor(Color.WHITE);
        mMultiplicativeInverseLabel.setTextColor(Color.WHITE);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("w-1");
        spannableStringBuilder
            .setSpan(new SuperscriptSpan(), 1, 3, 0);
        spannableStringBuilder
            .setSpan(new RelativeSizeSpan(0.6f), 1, 3, 0);
        mMultiplicativeInverseLabel.setText(spannableStringBuilder);
        mAdditiveInverseLabel.setText("-w");

        mInverseBinding.modulusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    mInverseBinding.calculateButton.setEnabled(false);
                } else {
                    mInverseBinding.calculateButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * This method handles the onClick on calculate button
     *
     * @param view on which click was performed.
     */
    public void calculateInverse(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
            Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

//        Toast.makeText(this, METHOD_TYPES[mMethodType], Toast.LENGTH_SHORT).show();

        mModN = Long.parseLong(mInverseBinding.modulusEditText.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putLong(MOD_KEY, mModN);
        bundle.putInt(METHOD_KEY, mMethodType);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Number>> loader = loaderManager.getLoader(INVERSE_LOADER_ID);

        if (loader == null) {
            loaderManager.initLoader(INVERSE_LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(INVERSE_LOADER_ID, bundle, this);
        }

    }

    /**
     * This method displays the {@link ProgressBar} and hides the {@link RecyclerView}
     * and Label.
     */
    private void hideData() {
        mLoadingBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mFrameLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * This method displays the {@link RecyclerView} and label and
     * hides the {@link ProgressBar}
     */
    private void showData() {
        mLoadingBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }


    @NonNull
    @Override
    public Loader<List<Number>> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case INVERSE_LOADER_ID:
                long mod = args.getLong(MOD_KEY);
                int methodType = args.getInt(METHOD_KEY);

                hideData();
                return new InverseLoader(getApplicationContext(), mod, methodType);

            default:
                throw new UnsupportedOperationException("Loader not implemented with id: " + id);
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Number>> loader, List<Number> data) {
        if (data != null) {
            showData();
            mNumbers = (ArrayList<Number>) data;
            mAdapter.setAdapterData((ArrayList<Number>) data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    /**
     * Loader implementation for calculating the Inverses.
     */
    public static class InverseLoader extends AsyncTaskLoader<List<Number>> {

        private long mModN;
        private int mMethodType;
        private ArrayList<Number> mNumbers;

        public InverseLoader(@NonNull Context context, long mod, int methodType) {
            super(context);
            mModN = mod;
            mMethodType = methodType;
        }

        @Override
        protected void onStartLoading() {
            if (mNumbers == null) {
                forceLoad();
            } else {
                deliverResult(mNumbers);
            }
        }

        @Nullable
        @Override
        public List<Number> loadInBackground() {

            switch (mMethodType) {
                case 0:                 /* Tabular Method */
                    return InverseUtils.getAllInverses(mModN);

                case 1:                 /* Extended Euclidean Algorithm */
                    return InverseUtils.getAllInversesUsingGCD(mModN);

                case 2:                 /* Galois Field n = 3 */
                    Log.e("TAG", "GF called");
                    return GaloisFieldUtils.getAllInverseUsingGF(3);

                case 3:                 /* Galois Field n = 8 */
                    return GaloisFieldUtils.getAllInverseUsingGF(8);

                default:
                    throw new UnsupportedOperationException(
                        "Method not defined with Type : " + mMethodType);
            }
        }

        @Override
        public void deliverResult(@Nullable List<Number> data) {
            mNumbers = (ArrayList<Number>) data;
            super.deliverResult(data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SPINNER_KEY, mMethodType);
        outState.putParcelableArrayList(INVERSE_KEY, mNumbers);
        super.onSaveInstanceState(outState);
    }
}
