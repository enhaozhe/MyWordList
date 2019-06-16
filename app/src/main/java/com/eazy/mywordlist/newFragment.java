package com.eazy.mywordlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class newFragment extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    private List<Word> mList;

    public newFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.new_fragment, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mList, getContext());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mList = new ArrayList<>();

        mList.add(new Word("Word", "a single distinct meaningful element of speech or writing, used with others (or sometimes alone) to form a sentence and typically shown with a space on either side when written or printed."));
        mList.add(new Word("aws", "asf"));
        mList.add(new Word("awa", "asf"));
        mList.add(new Word("awd", "asf"));
        mList.add(new Word("awb", "asf"));
        mList.add(new Word("awc", "asf"));

    }

}
