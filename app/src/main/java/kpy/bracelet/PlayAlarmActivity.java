package kpy.bracelet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by nomasp on 2015/10/07.
 */
public class PlayAlarmActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;  //声明一个振动器对象
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alarm_player);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{200, 400, 600, 800}, 1);

        setFinishOnTouchOutside(false);

        //显示对话框
        alertDialog=new AlertDialog.Builder(PlayAlarmActivity.this).setTitle("闹钟")
                .setMessage("时间到了！")
                .setIcon(R.mipmap.clockicon)
                .setCancelable(true)
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        vibrator.cancel();
                        PlayAlarmActivity.this.finish();
                    }
                }).create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }
}
