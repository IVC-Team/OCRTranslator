package com.ndanh.mytranslator;

import com.ndanh.mytranslator.screen.camera.CameraActivity;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by ndanh on 5/29/2017.
 */

public class TranslateModuleTest {
    @Test
    public void addition_isCorrect() throws Exception {
        int a = CameraActivity.determineMaxTextSize("aaaaaaaaaaa", 300f, 15f);
        assertEquals ( "abc", 10 , a );
    }

}
