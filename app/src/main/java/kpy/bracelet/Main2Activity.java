package kpy.bracelet;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import kpy.bracelet.R;
import kpy.chat.ChatActivity;
import kpy.fragment.shouye.PanoramaImageModel;

public class Main2Activity extends AppCompatActivity {

    private ImageButton imageButton;

    private TextView textView1;

    private TextView textView2;

    private PanoramaImageModel panoramaImageModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageButton = findViewById(R.id.imageView_1);
        textView1 = findViewById(R.id.tv_title_1);
        textView2 = findViewById(R.id.tv_desc_1);

        panoramaImageModel = (PanoramaImageModel) getIntent().getSerializableExtra("panoramaImageModel");

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView1.setText(panoramaImageModel.getTitle());
                textView2.setText(panoramaImageModel.getDesc());
                imageButton.setImageResource(panoramaImageModel.getResourceName());
            }
        },100);
    }

    public void siLiao(View view) {
        startActivity(new Intent(Main2Activity.this, ChatActivity.class));
    }
}
