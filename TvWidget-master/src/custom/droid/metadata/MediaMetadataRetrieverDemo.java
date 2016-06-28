
package custom.droid.metadata;

import java.io.File;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import custom.droid.R;

public class MediaMetadataRetrieverDemo extends Activity {

    private TextView mMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_metadata_demo);

        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshViews();
    }

    private void findViews() {
        mMetadata = (TextView) findViewById(R.id.media_metadata);
    }

    private void refreshViews() {
        final String path = "/storage/external_storage/sda1/张信哲-过火.m4a";// "/mnt/usb/sda1/张信哲-过火.m4a";
        mMetadata.setText(getMetadata(path));
    }

    private String getMetadata(final String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }

        File f = new File(path);
        if (!f.exists()) {
            return "";
        }

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        StringBuffer sb = new StringBuffer();
        sb.append(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        sb.append("\n");
        sb.append(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        sb.append("\n");
        sb.append(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

        mmr.release();
        return sb.toString();
    }
}
