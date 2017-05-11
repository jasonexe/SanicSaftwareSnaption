package com.snaptiongame.snaption.ui.profile;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by austinrobarts on 5/8/17.
 */

public class ProfileFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 2;
    private String tabTitles[];
    private User user;

    public ProfileFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        String gamesText = context.getResources().getString(R.string.games_text);
        String captionsText = context.getResources().getString(R.string.captions_text);
        tabTitles = new String[] {gamesText, captionsText};
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return ProfileTabFragment.newInstance(position, user);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
