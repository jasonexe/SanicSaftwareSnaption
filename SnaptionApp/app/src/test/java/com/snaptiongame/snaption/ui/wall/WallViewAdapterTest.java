package com.snaptiongame.snaption.ui.wall;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.LinearLayout;

import com.snaptiongame.snaption.BuildConfig;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by brittanyberlanga on 3/13/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "src/main/AndroidManifest.xml", sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@SuppressStaticInitializationFor({"com.snaptiongame.snaption.servercalls.FirebaseResourceManager", "com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager"})
@PrepareForTest(FirebaseResourceManager.class)
public class WallViewAdapterTest {
    WallViewAdapter adapter;
    List<Game> games;

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    ProfileActivity.ProfileActivityCreator mockCreator;
    @Mock
    Game game1;
    @Mock
    Caption caption1;
    @Mock
    WallViewHolder wallViewHolder;

    @Before
    public void setup() {
        initMocks(this);
        mockStatic(FirebaseResourceManager.class);
        mockStatic(FirebaseUserResourceManager.class);
        games = new ArrayList<>();
        games.add(game1);
        Map<String, Integer> votes = new HashMap<>();
        votes.put("id1", 0);
        votes.put("id2", 0);
        when(game1.getId()).thenReturn("game1_id");
        when(game1.getIsOpen()).thenReturn(true);
        when(game1.getImagePath()).thenReturn("game1_image_path");
        when(game1.getIsPublic()).thenReturn(true);
        when(game1.getPicker()).thenReturn("game1_picker");
        when(game1.getTopCaption()).thenReturn(caption1);
        when(game1.getVotes()).thenReturn(votes);
        when(caption1.retrieveCaptionText()).thenReturn(new SpannableStringBuilder("caption text"));
    }

    public void createAdapter() {
        adapter = new WallViewAdapter(games, mockCreator);
    }

    @Test
    public void clearItemsTest() {
        createAdapter();
        assertEquals(1, adapter.getItemCount());
        adapter.clearItems();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void addItemsTest() {
        createAdapter();
        assertEquals(1, adapter.getItemCount());
        List<Game> games = new ArrayList<>();
        games.add(game1);
        games.add(game1);
        adapter.addItems(games);
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void onBindViewHolderTest() {
        // tests populating a wall view holder with a game a public, open game with two upvotes and
        // a top caption
        createAdapter();
        wallViewHolder = adapter.onCreateViewHolder(new LinearLayout(RuntimeEnvironment.application), 0);
        adapter.onBindViewHolder(wallViewHolder, 0);
        assertEquals("2", wallViewHolder.upvoteCountText.getText());
        assertEquals(View.VISIBLE, wallViewHolder.captionerLayout.getVisibility());
        assertEquals("caption text", wallViewHolder.captionText.getText().toString());
        assertFalse(wallViewHolder.captionText.getTypeface().isBold());
        assertFalse(wallViewHolder.captionText.getTypeface().isItalic());
    }
}
