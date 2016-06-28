
package custom.droid.reflection;

import android.content.Context;
import android.view.View;
import custom.droid.ICustomInterface;

public class ReflectClass implements ICustomInterface {

    private String str;

    public ReflectClass() {
    }

    public ReflectClass(String str) {
        this.str = str;
    }

    public ReflectClass(Context context) {

    }

    public void putString(String str) {
        this.str = str;
    }

    public String getString() {
        return this.str;
    }

    @Override
    public void onClick(View arg0) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void refreshView() {

    }
}
