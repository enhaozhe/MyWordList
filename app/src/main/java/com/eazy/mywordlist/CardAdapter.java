package com.eazy.mywordlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;

public class CardAdapter extends PagerAdapter implements AdapterView.OnItemSelectedListener {

    private List<Word> mList;
    private Context context;
    private LayoutInflater layoutInflater;
    private Spinner spinner;
    private int currentList;
    private List<String> sList;
    private MainActivity mainActivity;
    private int flag;  //prevent itemselectListner invoked when created

    CardAdapter(List<Word> mList, Context context, int currentList, MainActivity mainActivity) {
        this.mList = mList;
        this.context = context;
        this.currentList = currentList;
        this.mainActivity = mainActivity;
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

        final TextView word = v.findViewById(R.id.word_title);
        final TextView def = v.findViewById(R.id.card_def);
        ImageButton button = v.findViewById(R.id.card_edit_bt);

        word.setText(mList.get(position).getWord());
        def.setText(mList.get(position).getDef());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.editMode(new Word(word.getText().toString(), def.getText().toString()));
            }
        });
        container.addView(v);
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
        if(flag < mList.size()){
            flag ++;
            return;
        }
        String selected = spinner.getSelectedItem().toString();
        int pos = mainActivity.getCurrentCard();
        Word word = mainActivity.getList(mainActivity.getCurrentCard()).get(pos);
        switch (selected){
            case "Move to ALMOST KNOW list":
                mainActivity.moveWord(word, 1);
                break;
            case "Move to KNOW list":
                mainActivity.moveWord(word, 2);
                break;
            case "Move to DON'T KNOW list":
                mainActivity.moveWord(word, 0);
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
