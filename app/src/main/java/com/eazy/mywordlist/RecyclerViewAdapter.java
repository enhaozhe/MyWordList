package com.eazy.mywordlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<Word> wordList;
    private Context mContext;
    private MainActivity mainActivity;

    RecyclerViewAdapter(List<Word> wordList, Context mContext, MainActivity mainActivity) {
        this.wordList = wordList;
        this.mContext = mContext;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.word_item, viewGroup, false);
        return new MyViewHolder(v, mainActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.word.setText(wordList.get(i).getWord());
        myViewHolder.def.setText(wordList.get(i).getDef());
        if(mainActivity.isDelete_mode_status()){
            myViewHolder.itemView.setBackgroundColor(android.R.drawable.btn_default);
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView word;
        private TextView def;
        private LinearLayout linearLayout;
        private MainActivity mainActivity;
        private ImageButton button;

        MyViewHolder(@NonNull View itemView, final MainActivity mainActivity) {
            super(itemView);

            this.mainActivity = mainActivity;
            word = itemView.findViewById(R.id.word_tv);
            def = itemView.findViewById(R.id.def_tv);
            button = itemView.findViewById(R.id.switch_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.showAlertDialog(new Word(word.getText().toString(), def.getText().toString()));
                }
            });
            linearLayout = itemView.findViewById(R.id.word_view);
            linearLayout.setOnLongClickListener(mainActivity);
            linearLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(mainActivity.getStatus()){
                mainActivity.preDelete(v, position);
            }else{
                mainActivity.viewMode(position);
            }
        }
    }
}
