package com.swarmnyc.pup.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.MainActivity;

public class MyChatsFragment extends Fragment {

    public MyChatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.getInstance().showToolbar();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_chats, container, false);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
