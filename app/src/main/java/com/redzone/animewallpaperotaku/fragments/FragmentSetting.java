package com.redzone.animewallpaperotaku.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.redzone.animewallpaperotaku.Dialog;
import com.redzone.animewallpaperotaku.R;

public class FragmentSetting extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.fragment_setting,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btndialog = view.findViewById(R.id.open_dialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog();

            }
        });

    }

    public  void opendialog(){

        Dialog dialog = new Dialog();
        dialog.show(getFragmentManager(),"dialogue");
    }
}
