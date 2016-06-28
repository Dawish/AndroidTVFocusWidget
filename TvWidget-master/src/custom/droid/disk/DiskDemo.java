
package custom.droid.disk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import custom.droid.ICustomInterface;
import custom.droid.R;

public class DiskDemo extends Activity implements ICustomInterface {

    private static final String TAG = "DiskDemo";

    private TextView mInternalVolume;

    private TextView mSdCardVolume;

    private StatFs mInternal;

    private StatFs mSdCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_disk_demo);
        initView();

        mInternal = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        mSdCard = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_1) {
            PackageManager pm = getPackageManager();
            ComponentName name = new ComponentName("com.demo.factorytest", "com.demo.factorytest.FactoryTestActivity");
            pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            Log.d(TAG, "onKeyDown");
            startActivity(new Intent().setComponent(name));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void initView() {
        mInternalVolume = (TextView) findViewById(R.id.volume_data);
        mSdCardVolume = (TextView) findViewById(R.id.volume_sdcard);
    }

    @Override
    public void refreshView() {
        mInternalVolume.setText(Formatter.formatFileSize(this,
                mInternal.getAvailableBlocksLong() * mInternal.getBlockSizeLong()));
        mSdCardVolume.setText(Formatter.formatFileSize(this,
                mSdCard.getAvailableBlocksLong() * mSdCard.getBlockSizeLong()));
    }
}
