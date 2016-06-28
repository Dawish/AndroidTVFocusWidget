
package org.bangbang.song.demo.focuslayer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SimpleDataAdapter extends ArrayAdapter<String> {
    private static final String TAG = SimpleDataAdapter.class.getSimpleName();
    private static final int OBJECTS = 50;

    private static final String[] sObjects = new String[OBJECTS];
    static {
        for (int i = 0; i < OBJECTS; i++) {
            sObjects[i] = "data " + i;
        }
    }
    private final static int COLOR_COUNT = 5;
    private final static int[] sColors = new int[COLOR_COUNT];
    static {
        sColors[0] = Color.GREEN;
        sColors[1] = Color.YELLOW;
        sColors[2] = Color.GRAY;
        sColors[3] = Color.RED;
        sColors[4] = Color.MAGENTA;
    }

    public SimpleDataAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1, sObjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView(). position: " + position);
        View v = super.getView(position, convertView, parent);

        int bkColor = Color.BLUE;
        if (position % 3 == 1) {
            bkColor = (Color.GRAY);
        } else if (position % 3 == 2) {
            bkColor = (Color.YELLOW);
        }
        bkColor = sColors[position % COLOR_COUNT];
        v.setBackgroundColor(bkColor);

        return v;
    }

}
