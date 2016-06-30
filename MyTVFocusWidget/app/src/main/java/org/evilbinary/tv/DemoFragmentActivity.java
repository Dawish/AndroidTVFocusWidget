package org.evilbinary.tv;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import org.evilbinary.tv.widget.BorderView;

/**
 * 作者:evilbinary on 2/28/16.
 * 邮箱:rootdebug@163.com
 */
public class DemoFragmentActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_fragment);
        Fragment fragment=new MyFragment2();
        BorderView borderView = new BorderView(this);
        getFragmentManager().beginTransaction().replace(R.id.fragment,fragment).commit();

        fragment.getView().getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {

            }
        });

    }


}
