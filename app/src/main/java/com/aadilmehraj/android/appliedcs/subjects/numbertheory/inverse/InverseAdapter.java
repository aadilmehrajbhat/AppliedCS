package com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.aadilmehraj.android.appliedcs.R;
import com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse.InverseAdapter.InverseAdapterViewHolder;
import com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse.InverseUtils.Number;
import java.util.ArrayList;

public class InverseAdapter extends Adapter<InverseAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Number> mNumbers;
    final private int EVEN_TYPE = 0;
    final private int ODD_TYPE = 1;

    public InverseAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0)
            return EVEN_TYPE;
        else
            return ODD_TYPE;
    }

    @NonNull
    @Override
    public InverseAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
            .inflate(R.layout.inverse_list_item, parent, false);

        if (viewType == ODD_TYPE)
            view.setBackgroundColor(Color.rgb(0xf8, 0xf8, 0xf8));

        return new InverseAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InverseAdapterViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return (mNumbers != null) ? mNumbers.size() : 0;
    }


    public class InverseAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView mNumber;
        private TextView mAdditiveInverse;
        private TextView mMultiplicativeInverse;

        public InverseAdapterViewHolder(View itemView) {
            super(itemView);
            mNumber = itemView.findViewById(R.id.number_text_view);
            mAdditiveInverse = itemView.findViewById(R.id.add_inverse_text_view);
            mMultiplicativeInverse = itemView.findViewById(R.id.mul_inverse_text_view);
        }

        public void bindView(int position) {

            mNumber.setText(String.valueOf(mNumbers.get(position).getNumber()));
            mAdditiveInverse
                .setText(String.valueOf(mNumbers.get(position).getAdditiveInverse()));
            long inverse = mNumbers.get(position).getMultiplicativeInverse();
            if (inverse == -1) {
                mMultiplicativeInverse
                    .setText("-");
            } else {
                mMultiplicativeInverse.setText(String.valueOf(inverse));
            }

        }
    }

    /**
     * This method set the Adapter data source.
     *
     * @param numbers the data source representing inverses.
     */
    public void setAdapterData(ArrayList<Number> numbers) {
        mNumbers = numbers;
    }
}
