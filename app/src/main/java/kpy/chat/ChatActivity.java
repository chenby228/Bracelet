package kpy.chat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kpy.bracelet.LoginActivity;
import kpy.bracelet.R;
import kpy.bracelet.RegisterActivity;
import kpy.chat.netty.star.client.ChatClient;
import kpy.chat.netty.star.client.listener.OnMsgReceivedListener;

public class ChatActivity extends Activity {
	public static ChatAdapter chatAdapter;

    private static String TAG = "ChatActivity";
	/**
	 * 声明ListView
	 */
	private ListView lv_chat_dialog;
	/**
	 * 集合
	 */
	public static List<PersonChat> personChats = new ArrayList<PersonChat>();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				/**
				 * ListView条目控制在最后一行
				 */
				lv_chat_dialog.setSelection(personChats.size());
				break;

			default:
				break;
			}
		};
	};

    private static ChatClient client = new ChatClient() {
        @Override
        public void msgSent(String msg) {
            Log.i(TAG, msg);
        }

        @Override
        public void msgReceived(String msg) {
            Log.i(TAG,msg);
            System.out.println("@@@@@@");


        }

        @Override
        public void clientMsg(String code, String info, String detail) {
            Log.i(TAG,"code:" + code + ",info:" + info + ",detail:" + detail);
        }
    };

    static {
        client.createSession(LoginActivity.userId);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_activity);



		lv_chat_dialog = (ListView) findViewById(R.id.lv_chat_dialog);
		Button btn_chat_message_send = (Button) findViewById(R.id.btn_chat_message_send);
		EditText et_chat_message = (EditText) findViewById(R.id.et_chat_message);
		/**
		 *setAdapter
		 */
		chatAdapter = new ChatAdapter(this, personChats);
		lv_chat_dialog.setAdapter(chatAdapter);
		/**
		 * 发送按钮的点击事件
		 */
		btn_chat_message_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(et_chat_message.getText().toString())) {
					Toast.makeText(ChatActivity.this, "发送内容不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				PersonChat personChat = new PersonChat();
				//代表自己发送
				personChat.setMeSend(true);


                String msg = client.createSendMessage("admin", et_chat_message.getText().toString());
                client.sendMessage(msg);


				//得到发送内容
				personChat.setChatMessage(et_chat_message.getText().toString());
				//加入集合
				personChats.add(personChat);
				//清空输入框
				et_chat_message.setText("");
				//刷新ListView
				chatAdapter.notifyDataSetChanged();
				handler.sendEmptyMessage(1);
			}
		});

	}



}
