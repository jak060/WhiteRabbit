package com.example.whiterabbit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import org.w3c.dom.Text;

public class EventFragment extends Fragment {
    private String title;
    private int pageNum;

    public static EventFragment newInstance(String title, int pageNum) {

        EventFragment eventFragment = new EventFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);

        eventFragment.setArguments(bundle);
        return eventFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 0);
        title = getArguments().getString("pageTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, viewGroup, false);

        TextView test = (TextView) view.findViewById(R.id.textView);
        test.setText("I'm logged in as " + ParseUser.getCurrentUser().getUsername());

        return view;
    }
}
