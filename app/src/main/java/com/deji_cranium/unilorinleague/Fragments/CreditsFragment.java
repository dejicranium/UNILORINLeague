package com.deji_cranium.unilorinleague.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.deji_cranium.unilorinleague.R;

/**
 * Created by cranium on 11/13/17.
 */

public class CreditsFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.credits_layout, container, false);
        getDialog().setTitle("Credits");

        Button mContactButton = (Button) view.findViewById(R.id.contact_button);

        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent displayCallLog = new Intent();
                displayCallLog.setAction(Intent.ACTION_DIAL);
                displayCallLog.setData(Uri.parse("tel:08100455706"));
                getContext().startActivity(displayCallLog);
            }});
        return view;
    }
}
