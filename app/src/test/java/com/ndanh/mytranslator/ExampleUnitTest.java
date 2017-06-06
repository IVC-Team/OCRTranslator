package com.ndanh.mytranslator;

import android.graphics.Rect;

import com.ndanh.mytranslator.model.DetectResult;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 1 + 2);
    }

    @Test
    public void parseRect() throws Exception {

        DetectResult item = DetectResult.parseDetectResult("<1-2-1-1>a");

        assertEquals(item.getText (), "a");
    }
}