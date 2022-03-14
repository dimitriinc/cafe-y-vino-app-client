package com.cafeyvinowinebar.Cafe_y_Vino.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.cafeyvinowinebar.Cafe_y_Vino.R;

import org.jetbrains.annotations.NotNull;

/**
 * Sets text to each page
 */

public class SliderAdapter extends PagerAdapter {

    private final Context context;
    private final String[] slide_headings;
    private final String[] slide_descs;

    public SliderAdapter(@NonNull Context context) {
        this.context = context;
        slide_headings = context.getResources().getStringArray(R.array.pager_headings);
        slide_descs = context.getResources().getStringArray(R.array.pager_descriptions);
    }

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_pager, container, false);

        TextView slideHeading = view.findViewById(R.id.txtSliderTitle);
        TextView slideBody = view.findViewById(R.id.txtSliderBody);

        slideHeading.setText(slide_headings[position]);
        slideBody.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
