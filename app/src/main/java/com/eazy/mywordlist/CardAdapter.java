package com.eazy.mywordlist;

import android.R.layout;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends PagerAdapter implements AdapterView.OnItemSelectedListener {

    private List<Word> mList;
    private Context context;
    private LayoutInflater layoutInflater;
    private Spinner spinner;

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
        spinner = v.findViewById(R.id.spinner_card);

        word.setText(mList.get(position).getWord());
        def.setText(mList.get(position).getDef());

        spinner.setOnItemSelectedListener(this);
        List<String> sList = new ArrayList<>();
        sList.add("Move to DON'T KNOW list");
        sList.add("Move to ALMOST KNOW list");
        sList.add("Move to KNOW list");

        ArrayAdapter<String > arrayAdapter = new ArrayAdapter<String>(context, layout.simple_spinner_dropdown_item, sList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
