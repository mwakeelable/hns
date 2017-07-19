package com.linked_sys.hns.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ScrollViewExt extends ScrollView {
    ScrollViewListener listener;
    public ScrollViewExt(Context context) {
        super(context);
    }

    public ScrollViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public ScrollViewExt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null)
            listener.onScrollChanged(this, l,t,oldl,oldt);
    }

    public void setScrollViewListener(ScrollViewListener listener){
        this.listener = listener;
    }
}
