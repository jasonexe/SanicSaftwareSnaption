package com.snaptiongame.snaption.ui.wall;

import android.text.SpannableStringBuilder;

import com.snaptiongame.snaption.BuildConfig;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;

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
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
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
    List<GameMetadata> games;

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    GameMetadata game1;
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
        Map<String, Integer> upvotes = new HashMap<>();
        upvotes.put("id1", 0);
        upvotes.put("id2", 0);
        when(game1.getId()).thenReturn("game1_id");
        when(game1.isOpen()).thenReturn(true);
        when(game1.getImagePath()).thenReturn("game1_image_path");
        when(game1.getIsPublic()).thenReturn(true);
        when(game1.getPickerId()).thenReturn("game1_picker");
        when(game1.getTopCaption()).thenReturn(caption1);
        when(game1.getUpvotes()).thenReturn(upvotes);
        when(caption1.retrieveCaptionText()).thenReturn(new SpannableStringBuilder("caption text"));
    }

    public void createAdapter() {
        adapter = new WallViewAdapter(games);
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
        List<GameMetadata> games = new ArrayList<>();
        games.add(game1);
        games.add(game1);
        adapter.addItems(games);
        assertEquals(3, adapter.getItemCount());
    }
}
