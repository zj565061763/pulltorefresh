package com.fanwe.lib.pulltorefresh;

import android.support.v4.view.ViewCompat;
import android.view.View;

class Utils
{
    public static int getMinimumWidth(View view)
    {
        return ViewCompat.getMinimumWidth(view);
    }

    public static int getMinimumHeight(View view)
    {
        return ViewCompat.getMinimumHeight(view);
    }

    public static void offsetTopAndBottom(View view, int offset)
    {
        ViewCompat.offsetTopAndBottom(view, offset);
    }

    public static float getZ(View view)
    {
        return ViewCompat.getZ(view);
    }

    public static void setZ(View view, float z)
    {
        ViewCompat.setZ(view, z);
    }
}
