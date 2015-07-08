package com.example.whiterabbit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InviteFragment extends Fragment {
    private String title;
    private int pageNum;

    public static InviteFragment newInstance(String title, int pageNum) {

        InviteFragment inviteFragment = new InviteFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);

        inviteFragment.setArguments(bundle);
        return inviteFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 2);
        title = getArguments().getString("pageTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_invite, viewGroup, false);

        return view;
    }
}
