package com.example.whiterabbit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ContactListFragment extends Fragment {
    private String title;
    private int pageNum;

    public static ContactListFragment newInstance(String title, int pageNum) {

        ContactListFragment contactListFragment = new ContactListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);

        contactListFragment.setArguments(bundle);
        return contactListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 1);
        title = getArguments().getString("pageTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact_list, viewGroup, false);

        return view;
    }
}
