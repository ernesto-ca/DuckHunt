package com.proyecto.duckhunt.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proyecto.duckhunt.R;
import com.proyecto.duckhunt.models.User;
import java.util.List;


public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private Context context;

    public MyUserRecyclerViewAdapter(List<User> items, Context ctx) {
        mValues = items;
        context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        int pos = position + 1;
        holder.tvPosition.setText(pos+"°");
        holder.tvDucks.setText(String.valueOf(mValues.get(position).getDucks()));
        holder.tvNick.setText(mValues.get(position).getNick());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvPosition;
        public final TextView tvDucks;
        public final TextView tvNick;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvPosition = view.findViewById(R.id.tvPosition);
            tvDucks =  view.findViewById(R.id.tvDucks);
            tvNick  = view.findViewById(R.id.tvNick);

            //Cambiar tipo de fuente
            Typeface typeface =  Typeface.createFromAsset(context.getAssets(),"pixel.ttf");
            tvNick.setTypeface(typeface);
            tvDucks.setTypeface(typeface);
            tvPosition.setTypeface(typeface);
        }

        @Override
        public String toString() {
            return super.toString() + " '"+tvNick.getText()+"'";
        }
    }
}