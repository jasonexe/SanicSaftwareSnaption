package com.snaptiongame.snaption.ui.friends;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.servercalls.FirebaseReporter;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaption.servercalls.Uploader.ITEM_ALREADY_EXISTS_ERROR;
import static com.snaptiongame.snaption.servercalls.Uploader.UploadListener;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by brittanyberlanga on 2/12/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@SuppressStaticInitializationFor("com.snaptiongame.snaption.servercalls.FirebaseResourceManager")
@PrepareForTest(FirebaseResourceManager.class)
public class FriendsViewModelTest {
    private static final String FB_PROVIDER_LABEL = "Facebook friends on Snaption";
    private static final String GOOGLE_PROVIDER_LABEL = "Google+ friends on Snaption";
    private static final String ADD_FRIEND_SUCCESS = "Brittany is now your friend!";
    private static final String ADD_FRIEND_FAIL = "Problem adding Brittany as your friend.";
    private static final String ADD_FRIEND_ALREADY_EXISTS = "Brittany is already your friend.";
    private static final String TEST_SNAPTION_ID = "snaption123456789";
    private static final String FRIENDS_PATH = "users/snaption123456789/friends";
    private static final String TEST_FB_ID = "123456789";
    private static final String TEST_FRIEND_NAME = "Brittany";
    private FriendsViewModel viewModel;

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    private User user;
    @Mock
    private Friend friend;
    @Mock
    private Uploader uploader;
    @Mock
    private UploadListener uploadListener;
    @Mock
    private ResourceListener resourceListener;

    @Before
    public void setup() {
        initMocks(this);
        viewModel = new FriendsViewModel(user, uploader);
    }

    @Test
    public void testGetFbProviderLabel() {
        List<String> providers = new ArrayList<>();
        providers.add(FacebookAuthProvider.PROVIDER_ID);
        PowerMockito.mockStatic(FirebaseResourceManager.class);
        BDDMockito.given(FirebaseResourceManager.getProviders())
                .willReturn(providers);
        assertEquals(FB_PROVIDER_LABEL,
                viewModel.getLoginProviderLabel(RuntimeEnvironment.application));
    }

    @Test
    public void testGetGoogleProviderLabel() {
        List<String> providers = new ArrayList<>();
        providers.add(GoogleAuthProvider.PROVIDER_ID);
        PowerMockito.mockStatic(FirebaseResourceManager.class);
        BDDMockito.given(FirebaseResourceManager.getProviders())
                .willReturn(providers);
        assertEquals(GOOGLE_PROVIDER_LABEL,
                viewModel.getLoginProviderLabel(RuntimeEnvironment.application));
    }

    @Test
    public void testGetAddedFriendTextSuccess() {
        assertEquals(ADD_FRIEND_SUCCESS,
                viewModel.getAddedFriendText(RuntimeEnvironment.application, TEST_FRIEND_NAME,
                        true, ""));
    }

    @Test
    public void testGetAddedFriendTextFail() {
        assertEquals(ADD_FRIEND_FAIL,
                viewModel.getAddedFriendText(RuntimeEnvironment.application, TEST_FRIEND_NAME,
                        false, ""));
        assertEquals(ADD_FRIEND_ALREADY_EXISTS,
                viewModel.getAddedFriendText(RuntimeEnvironment.application, TEST_FRIEND_NAME,
                        false, ITEM_ALREADY_EXISTS_ERROR));
    }

    @Test
    public void testGetFbProviderFriends() {
        List<String> providers = new ArrayList<>();
        providers.add(FacebookAuthProvider.PROVIDER_ID);
        PowerMockito.mockStatic(FirebaseResourceManager.class);
        BDDMockito.given(FirebaseResourceManager.getProviders())
                .willReturn(providers);
        when(user.getFacebookId()).thenReturn(TEST_FB_ID);
        when(user.getId()).thenReturn(TEST_SNAPTION_ID);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> friendsListCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<ResourceListener> friendsListListenerCaptor = ArgumentCaptor.forClass(ResourceListener.class);
        ArgumentCaptor<ResourceListener> listenerCaptor = ArgumentCaptor.forClass(ResourceListener.class);
        try {
            doNothing().when(
                    FirebaseResourceManager.class, "retrieveStringMapNoUpdates",
                    pathCaptor.capture(), friendsListListenerCaptor.capture());
            doNothing().when(
                    FirebaseResourceManager.class, "getFacebookFriends", userCaptor.capture(),
                    friendsListCaptor.capture(), listenerCaptor.capture());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        viewModel.getLoginProviderFriends(resourceListener);

        // check that retrieveStringListNoUpdates was called with the correct arguments
        assertEquals(FRIENDS_PATH, pathCaptor.getValue());

        // emulate data returning from the call
        Map<Friend, Integer> mockFriends = new HashMap<>();
        mockFriends.put(friend, 1);
        friendsListListenerCaptor.getValue().onData(mockFriends);

        // check that getFacebookFriends was called with the correct arguments
        assertEquals(user, userCaptor.getValue());
        assertEquals(mockFriends, friendsListCaptor.getValue());
        assertEquals(resourceListener, listenerCaptor.getValue());
    }

    @Test
    public void testAddFriend() {
        viewModel.addFriend(friend, uploadListener);
        // check that the method was called once with the correct arguments
        verify(uploader, times(1)).addFriend(user, friend, uploadListener);
    }
}
