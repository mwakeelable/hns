package com.linked_sys.hns.ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linked_sys.hns.R;
import com.linked_sys.hns.ui.Activities.MailActivity;

import in.myinnos.customimagetablayout.ChangeColorTab;

public class MessagesFragment extends Fragment {
    MailActivity activity;
    public InboxFragment inboxFragment;
    public OutboxFragment outboxFragment;
    public TrashFragment trashFragment;
    String[] titles = new String[3];
    MainAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new MainAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MailActivity) getActivity();
        return inflater.inflate(R.layout.main_message_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        ChangeColorTab changeColorTab = (ChangeColorTab) view.findViewById(R.id.tabChangeColorTab);
        changeColorTab.setViewpager(viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    public class MainAdapter extends FragmentStatePagerAdapter {

        MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (inboxFragment == null)
                    inboxFragment = new InboxFragment();
                return inboxFragment;
            } else if (position == 1) {
                if (outboxFragment == null)
                    outboxFragment = new OutboxFragment();
                return outboxFragment;
            } else {
                if (trashFragment == null)
                    trashFragment = new TrashFragment();
                return trashFragment;
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
