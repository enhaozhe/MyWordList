package com.eazy.mywordlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CardAdapter extends PagerAdapter {

    private List<Word> mList;
    private Context context;
    private LayoutInflater layoutInflater;

    public CardAdapter(List<Word> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.word_card, container, false);

        TextView  word = v.findViewById(R.id.word_title);
        TextView def = v.findViewById(R.id.card_def);

        word.setText(mList.get(position).getWord());
        def.setText(mList.get(position).getDef());

        container.addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    public List<Word> getmList() {
        return mList;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return mList.indexOf(object);
    }
}
