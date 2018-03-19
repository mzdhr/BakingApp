package com.mzdhr.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.helper.Constant;
import com.mzdhr.bakingapp.model.Recipe;
import com.mzdhr.bakingapp.ui.activity.IngredientAndStepActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammad on 26/02/2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    // Objects
    private static final String TAG = RecipeAdapter.class.getSimpleName();
    private ArrayList<Recipe> mRecipes;
    private Context mContext;

    public RecipeAdapter(ArrayList<Recipe> recipes, Context context) {
        mRecipes = recipes;
        mContext = context;
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

    class RecipeViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        @BindView(R.id.recipe_name_textView)
        TextView mRecipeName;
        @BindView(R.id.recipe_servings_textView)
        TextView mRecipeServings;
        @BindView(R.id.recipe_imageView)
        ImageView mRecipeImageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            mRecipeName.setText(mRecipes.get(position).getName());
            mRecipeServings.setText(mContext.getString(R.string.text_servings) + mRecipes.get(position).getServings().toString());

            try {
                if (!mRecipes.get(position).getImage().equals("")) {
                    Picasso.with(mContext)
                            .load(mRecipes.get(position).getImage())
                            .placeholder(R.drawable.ic_stove)
                            .error(R.drawable.ic_stove)
                            .into(mRecipeImageView);
                }
            } catch (Exception e) {
                Log.d(TAG, "bind: " + e.toString());
            }

        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, IngredientAndStepActivity.class);
            Bundle bundle = new Bundle();
            Parcelable recipeParcelable = Parcels.wrap(mRecipes.get(getAdapterPosition()));
            bundle.putParcelable(Constant.RECIPE_ARRAY_KEY, recipeParcelable);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

}
