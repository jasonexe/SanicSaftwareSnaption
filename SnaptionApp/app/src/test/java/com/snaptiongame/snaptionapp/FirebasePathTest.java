package com.snaptiongame.snaptionapp;

/**
 * Created by Hristo on 2/4/2017.
 */

import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import org.junit.Test;
import static org.junit.Assert.*;

public class FirebasePathTest {

    @Test
    public void testValidPath() {
        assertTrue(WallViewAdapter.validFirebasePath("aBCd"));
        assertTrue(WallViewAdapter.validFirebasePath("abcd123"));
        assertTrue(WallViewAdapter.validFirebasePath("123"));
        assertTrue(WallViewAdapter.validFirebasePath("123iop"));
    }

    @Test
    public void testInvalidPath() {
        assertFalse(WallViewAdapter.validFirebasePath("ab.c"));
        assertFalse(WallViewAdapter.validFirebasePath("$123"));
        assertFalse(WallViewAdapter.validFirebasePath("[bbdd]"));
        assertFalse(WallViewAdapter.validFirebasePath("$1.30"));
    }
}
