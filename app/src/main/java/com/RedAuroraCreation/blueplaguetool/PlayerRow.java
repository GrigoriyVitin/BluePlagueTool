package com.RedAuroraCreation.blueplaguetool;

import android.content.Context;
import android.util.Log;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerRow extends TableRow {
    int background;
    boolean bullshit;

    public PlayerRow(Context context) {
        super(context);
    }

    public void select(boolean bullshit){
        Log.d("PlayerRow", ""+bullshit);
        if(bullshit){
            setBackgroundColor(getResources().getColor(R.color.purple_500));
        }else{
            setBackgroundColor(getResources().getColor(R.color.color0));
        }
        this.bullshit = bullshit;
    }


    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        background = color;
    }
    public int getBackgroundColor() {
        return background;
    }
}
