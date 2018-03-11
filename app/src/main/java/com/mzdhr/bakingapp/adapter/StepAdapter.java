package com.mzdhr.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mzdhr.bakingapp.ItemDetailActivity;
import com.mzdhr.bakingapp.ItemDetailFragment;
import com.mzdhr.bakingapp.ItemListActivity;
import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammad on 12/03/2018.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder>{
    private static final String TAG = StepAdapter.class.getSimpleName();
    private Context mContext;
    final private ListItemClickListener mOnClickListener;
    private ArrayList<Step> mSteps;
    private final ItemListActivity mParentActivity;
    private final boolean mTwoPane;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

//    public StepAdapter(ArrayList<Step> steps, ListItemClickListener listener){
//        mSteps = steps;
//        mOnClickListener = listener;
//    }
//
//    public StepAdapter(ArrayList<Step> steps){
//        mSteps = steps;
//        mOnClickListener = null;
//    }

    public StepAdapter(ItemListActivity parent, ArrayList<Step> steps, boolean twoPane){
        mSteps = steps;
        mParentActivity = parent;
        mOnClickListener = parent;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
        StepViewHolder viewHolder = new StepViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       @BindView(R.id.content) TextView mStepTitleTextView;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position){
            String stepNumber = mSteps.get(position).getId();
            String stepShortDescription = mSteps.get(position).getShortDescription();
            mStepTitleTextView.setText(stepNumber + ". " + stepShortDescription);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
            //DummyContent.DummyItem item = (DummyContent.DummyItem) v.getTag();

            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ItemDetailFragment.ARG_ITEM_ID, mSteps.get(getAdapterPosition()).getId());
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = v.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, mSteps.get(getAdapterPosition()).getId());

                context.startActivity(intent);
            }
        }
    }
}
