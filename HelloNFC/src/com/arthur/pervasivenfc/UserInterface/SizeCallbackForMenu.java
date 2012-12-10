package com.arthur.pervasivenfc.UserInterface;

import android.view.View;

import com.arthur.pervasivenfc.UserInterface.MyHorizontalScrollView.SizeCallback;

/**
 * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
 * showing.
 */
public class SizeCallbackForMenu implements SizeCallback {
    int btnWidth;
    View btnSlide;

    public SizeCallbackForMenu(View btnSlide) {
        super();
        this.btnSlide = btnSlide;
    }

    public void onGlobalLayout() {
        btnWidth = btnSlide.getMeasuredWidth();
        System.out.println("btnWidth=" + btnWidth);
    }

    public void getViewSize(int idx, int w, int h, int[] dims) {
        dims[0] = w;
        dims[1] = h;
        final int menuIdx = 0;
        if (idx == menuIdx) {
            dims[0] = w - btnWidth;
        }
    }
}
