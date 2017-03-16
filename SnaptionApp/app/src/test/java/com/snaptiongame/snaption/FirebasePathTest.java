package com.snaptiongame.snaption;

/**
 * Created by Hristo on 2/4/2017.
 */

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@SuppressStaticInitializationFor({"com.google.firebase.database.FirebaseDatabase",
        "com.google.firebase.storage.FirebaseStorage"})
@PrepareForTest({FirebaseDatabase.class, FirebaseStorage.class})
public class FirebasePathTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private FirebaseDatabase firebaseDatabase;
    @Mock
    private FirebaseStorage firebaseStorage;

    @Before
    public void setup() {
        initMocks(this);
        mockStatic(FirebaseDatabase.class);
        mockStatic(FirebaseStorage.class);
        try {
            when(FirebaseDatabase.class, "getInstance").thenReturn(firebaseDatabase);
            when(FirebaseStorage.class, "getInstance").thenReturn(firebaseStorage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testValidPath() {
        assertTrue(FirebaseResourceManager.validFirebasePath("aBCd"));
        assertTrue(FirebaseResourceManager.validFirebasePath("abcd123"));
        assertTrue(FirebaseResourceManager.validFirebasePath("123"));
        assertTrue(FirebaseResourceManager.validFirebasePath("123iop"));
    }

    @Test
    public void testInvalidPath() {
        assertFalse(FirebaseResourceManager.validFirebasePath("ab.c"));
        assertFalse(FirebaseResourceManager.validFirebasePath("$123"));
        assertFalse(FirebaseResourceManager.validFirebasePath("[bbdd]"));
        assertFalse(FirebaseResourceManager.validFirebasePath("$1.30"));
    }
}
