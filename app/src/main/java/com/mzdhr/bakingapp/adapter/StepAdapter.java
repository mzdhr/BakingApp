package com.mzdhr.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mzdhr.bakingapp.StepDetailActivity;
import com.mzdhr.bakingapp.StepDetailFragment;
import com.mzdhr.bakingapp.StepsActivity;
import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.helper.Constant;
import com.mzdhr.bakingapp.model.Recipe;
import com.mzdhr.bakingapp.model.Step;

import org.parceler.Parcels;

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
    private Recipe mRecipe;
    private ArrayList<Step> mSteps;
    private final StepsActivity mParentActivity;
    private final boolean mTwoPane;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }



    public StepAdapter(StepsActivity parent, Recipe recipe, boolean twoPane){
        mRecipe = recipe;
        mSteps = mRecipe.getSteps();
        mParentActivity = parent;
        mOnClickListener = parent;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_step, parent, false);
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
                arguments.putString(StepDetailFragment.ARG_ITEM_ID, mSteps.get(getAdapterPosition()).getId());
                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();

                // Passing data to Tablet fragment details
                arguments.putString(Constant.STEP_VIDEO_URL_KEY, mRecipe.getSteps().get(getAdapterPosition()).getVideoURL());
                arguments.putString(Constant.STEP_DESCRIPTION_KEY, mRecipe.getSteps().get(getAdapterPosition()).getDescription());
                arguments.putParcelable(Constant.STEP_LIST_KEY, Parcels.wrap(mRecipe.getSteps()));
                //arguments.putString(Constant.STEP_THUMBNAIL_URL_KEY, mRecipe.getSteps().get(getAdapterPosition()).getThumbnailURL());

            } else {
                Context context = v.getContext();
                Intent intent = new Intent(context, StepDetailActivity.class);
                //intent.putExtra(StepDetailFragment.ARG_ITEM_ID, mRecipe.get(getAdapterPosition()).getId());
                // FIXME: 16/03/2018 if getVidewURL == "" then use getThumnail!!
                intent.putExtra(Constant.STEP_VIDEO_URL_KEY, mRecipe.getSteps().get(getAdapterPosition()).getVideoURL());
                intent.putExtra(Constant.STEP_DESCRIPTION_KEY, mRecipe.getSteps().get(getAdapterPosition()).getDescription());
                Log.d(TAG, "onClick: getAdapterPosition: " + getAdapterPosition());
                context.startActivity(intent);
            }
        }
    }
}
