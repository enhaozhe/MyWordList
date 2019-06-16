package com.eazy.mywordlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<Word> wordList;
    private Context mContext;

    public RecyclerViewAdapter(List<Word> wordList, Context mContext) {
        this.wordList = wordList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.word_item, viewGroup, false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.word.setText(wordList.get(i).getWord());
        myViewHolder.def.setText(wordList.get(i).getDef());
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView word;
        private TextView def;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.word_tv);
            def = itemView.findViewById(R.id.def_tv);

        }
    }
}
