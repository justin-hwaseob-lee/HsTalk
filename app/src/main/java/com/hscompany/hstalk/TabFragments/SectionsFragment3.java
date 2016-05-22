package com.hscompany.hstalk.TabFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hscompany.hstalk.R;

/**
 * Created by hs695 on 2016-02-07.
 */
public class SectionsFragment3 extends Fragment {

    public SectionsFragment3() {

    }

    // PlaceholderFragment.newInstance() 와 똑같이 추가
    public static SectionsFragment3 newInstance(int SectionNumber){
        SectionsFragment3 fragment = new SectionsFragment3();
        Bundle args = new Bundle();
        args.putInt("section_number", SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page3,
                container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.section_label3);
        tv.setText("서비스 열심히 준비중입니다!");
        return rootView;
    }
}
