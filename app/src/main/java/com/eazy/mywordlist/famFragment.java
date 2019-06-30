package com.eazy.mywordlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class famFragment extends Fragment {

    View v;
    private RecyclerView recyclerView;
    private List<Word> mList;
    private RecyclerViewAdapter mAdapter;

    public famFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fam_fragment, container, false);

        recyclerView = v.findViewById(R.id.recyclerView_fam);
        mAdapter = new RecyclerViewAdapter(mList, getContext(), (MainActivity) getContext());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mList = (List<Word>) getArguments().getSerializable("getFamList");
        }else{
            mList = new ArrayList<>();
        }

    }

    public void preDelete(Word word){
        mList.remove(word);
        mAdapter.notifyDataSetChanged();
    }

    public void addWord(Word word){
        mList.add(word);
        mAdapter.notifyDataSetChanged();
    }

    public void removeWord(Word word){
        mList.remove(word);
        mAdapter.notifyDataSetChanged();
    }

    public void notifyData(){
        mAdapter.notifyDataSetChanged();
    }
}
