package com.eazy.mywordlist;

import android.R.layout;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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
    private int currentList;
    private List<String> sList;
    private MainActivity mainActivity;
    private int flag;  //prevent itemselectListner invoked when created

    public CardAdapter(List<Word> mList, Context context, int currentList, MainActivity mainActivity) {
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

        TextView  word = v.findViewById(R.id.word_title);
        TextView def = v.findViewById(R.id.card_def);
       // spinner = v.findViewById(R.id.spinner_card);

        word.setText(mList.get(position).getWord());
        def.setText(mList.get(position).getDef());

       /* spinner.setOnItemSelectedListener(this);
        sList = new ArrayList<>();
        setSpinnerList();

        ArrayAdapter<String > arrayAdapter = new ArrayAdapter<String>(context, layout.simple_spinner_dropdown_item, sList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);*/

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

    //Todo: Add functions to spinner's drop down menu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(flag < mList.size()){
            Log.d("TAG flag = ", String.valueOf(flag));
            flag ++;
            return;
        }
        Log.d("TAG new flag = ", String.valueOf(flag));
        String selected = spinner.getSelectedItem().toString();
        Log.d("TAG Current Card", selected);
        int pos = mainActivity.getCurrentCard();
        Log.d("TAG Current Card", String.valueOf(pos));
        Log.d("TAG Current TAB", String.valueOf(mainActivity.getItab()));
        Word word = mainActivity.getList(mainActivity.getItab()).get(pos);
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
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    public void setSpinnerList(){
        switch (currentList){
            case 0:
                sList.add("Move to ALMOST KNOW list");
                sList.add("Move to KNOW list");
                return;
            case 1:
                sList.add("Move to DON'T KNOW list");
                sList.add("Move to KNOW list");
                return;
            case 2:
                sList.add("Move to DON'T KNOW list");
                sList.add("Move to ALMOST KNOW list");
                return;
            default:
                return;
        }
    }
}
