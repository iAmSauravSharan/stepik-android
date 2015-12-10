package org.stepic.droid.view.decorators;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecorationHorizontal extends RecyclerView.ItemDecoration{
    private int space;

    public SpacesItemDecorationHorizontal(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildPosition(view) != 0)
            outRect.left = space;
    }
}
