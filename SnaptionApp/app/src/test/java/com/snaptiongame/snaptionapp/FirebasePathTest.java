package com.snaptiongame.snaptionapp;

/**
 * Created by Hristo on 2/4/2017.
 */

import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FirebasePathTest {

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
