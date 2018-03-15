package com.mzdhr.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.model.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammad on 26/02/2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{
    private static final String TAG = RecipeAdapter.class.getSimpleName();
    //private static int viewHolderCount;
    final private ListItemClickListener mOnClickListener;
    private ArrayList<Recipe> mRecipes;
    private Context mContext;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public RecipeAdapter(ArrayList<Recipe> recipes, ListItemClickListener listener){
        mRecipes = recipes;
        mOnClickListener = listener;
        mContext = (Context) listener;
    }
    public RecipeAdapter(ArrayList<Recipe> recipes, Context context, ListItemClickListener listener){
        mRecipes = recipes;
        mContext = context;
        mOnClickListener = listener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_recipe, parent, false);
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }



    class RecipeViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        @BindView(R.id.recipe_name_textView) TextView mRecipeName;
        @BindView(R.id.recipe_servings_textView) TextView mRecipeServings;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            mRecipeName.setText(mRecipes.get(position).getName());
            mRecipeServings.setText(mContext.getString(R.string.text_servings) + mRecipes.get(position).getServings().toString());
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

}
