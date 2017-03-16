package com.snaptiongame.snaption.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.ui.HomeAppCompatActivity;

/**
 * Created by austinrobarts on 3/9/17.
 */
public class ProfileActivity extends HomeAppCompatActivity {

    public static String USER_ID_KEY = "userId";

    public interface ProfileActivityCreator {
        void create(String userId);
    }

    public static ProfileActivityCreator getProfileActivityCreator(final Context context) {
        return new ProfileActivityCreator() {
            @Override
            public void create(String userId) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(USER_ID_KEY, userId);
                context.startActivity(intent);
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //go to the user's profile
        Fragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", getIntent().getStringExtra("userId"));
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profile_fragment, fragment)
                .commit();
    }
}
