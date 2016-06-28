
package custom.droid.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import custom.droid.ICustomInterface;
import custom.droid.R;

public class ReflectionDemo extends Activity implements ICustomInterface {
    private static final String TAG = "ReflectionDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflection_demo);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void refreshView() {
        try {
            Class<?> c = Class.forName("custom.droid.reflection.ReflectClass");
            for (Constructor<?> cons : c.getDeclaredConstructors()) {
                Log.w(TAG, "cons : " + cons.toGenericString());
            }

            for (Method m : c.getDeclaredMethods()) {
                Log.w(TAG, "m : " + m.toGenericString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
