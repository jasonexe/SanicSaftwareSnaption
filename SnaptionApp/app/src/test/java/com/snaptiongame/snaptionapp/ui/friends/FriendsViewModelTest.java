package com.snaptiongame.snaptionapp.ui.friends;

import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
@SuppressStaticInitializationFor("com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager")
@PrepareForTest(FirebaseResourceManager.class)
public class FriendsViewModelTest {
    private static final String FB_PROVIDER_LABEL = "Facebook friends on Snaption";
    private static final String GOOGLE_PROVIDER_LABEL = "Google+ friends on Snaption";
    private static final String ADD_FRIEND_SUCCESS = "Brittany is now your friend!";
    private static final String ADD_FRIEND_FAIL = "Problem adding Brittany as your friend.";
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
    private Uploader.UploadListener uploadListener;
    @Mock
    private ResourceListener resourceListener;

    @Before
    public void setup() {
        initMocks(this);
        user = mock(User.class);
        uploader = mock(Uploader.class);
        resourceListener = mock(ResourceListener.class);
        viewModel = new FriendsViewModel(user, uploader);
    }

    @Test
    public void testGetFbProviderLabel() {
        when(user.getFacebookId()).thenReturn(TEST_FB_ID);
        assertEquals(FB_PROVIDER_LABEL,
                viewModel.getLoginProviderLabel(RuntimeEnvironment.application));
    }

    @Test
    public void testGetGoogleProviderLabel() {
        when(user.getFacebookId()).thenReturn(null);
        assertEquals(GOOGLE_PROVIDER_LABEL,
                viewModel.getLoginProviderLabel(RuntimeEnvironment.application));
    }

    @Test
    public void testGetAddedFriendTextSuccess() {
        assertEquals(ADD_FRIEND_SUCCESS,
                viewModel.getAddedFriendText(RuntimeEnvironment.application, TEST_FRIEND_NAME,
                        true));
    }

    @Test
    public void testGetAddedFriendTextFail() {
        assertEquals(ADD_FRIEND_FAIL,
                viewModel.getAddedFriendText(RuntimeEnvironment.application, TEST_FRIEND_NAME,
                        false));
    }

    @Test
    public void testGetFbProviderFriends() {
        mockStatic(FirebaseResourceManager.class);
        when(user.getFacebookId()).thenReturn(TEST_FB_ID);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ResourceListener> listenerCaptor = ArgumentCaptor.forClass(ResourceListener.class);
        try {
            doNothing().when(
                    FirebaseResourceManager.class, "getFacebookFriends", idCaptor.capture(), listenerCaptor.capture());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        viewModel.getLoginProviderFriends(resourceListener);
        // check that the static method was called with the correct arguments
        assertEquals(TEST_FB_ID, idCaptor.getValue());
        assertEquals(resourceListener, listenerCaptor.getValue());
    }

    @Test
    public void testAddFriend() {
        viewModel.addFriend(friend, uploadListener);
        // check that the method was called once with the correct arguments
        verify(uploader, times(1)).addFriend(user, friend, uploadListener);
    }
}
