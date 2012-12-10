package com.arthur.pervasivenfc.UserInterface;

import java.util.Date;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

public class ClickListenerForScrolling implements OnClickListener {
    HorizontalScrollView scrollView;
    View menu;
    /**
     * Menu must NOT be out/shown to start with.
     */
    boolean menuOut = false;

    public ClickListenerForScrolling(HorizontalScrollView scrollView, View menu) {
        super();
        this.scrollView = scrollView;
        this.menu = menu;
    }

    public void onClick(View v) {
        Context context = menu.getContext();
        String msg = "Slide " + new Date();
        Toast.makeText(context, msg, 1000).show();
        System.out.println(msg);

        int menuWidth = menu.getMeasuredWidth();

        // Ensure menu is visible
        menu.setVisibility(View.VISIBLE);

        if (!menuOut) {
            // Scroll to 0 to reveal menu
            int left = 0;
            scrollView.smoothScrollTo(left, 0);
        } else {
            // Scroll to menuWidth so menu isn't on screen.
            int left = menuWidth;
            scrollView.smoothScrollTo(left, 0);
        }
        menuOut = !menuOut;
    }
}