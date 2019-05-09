package kpy.bracelet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;


/**
 * 发布帖子
 * 
 * @author C3407096
 *
 */
public class IssuePostActivity extends BaseActivity implements View.OnClickListener {
	private ImageView tvCancel;// 取消
	private ImageView tvIssue;// 发布
	private EditText etContent;// 内容
    private EditText etTitle; //标题
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_post109);
		initView();
	}

	private void initView() {
		// 取消
		tvCancel = findViewById(R.id.back);
		// 发布
		tvIssue = findViewById(R.id.upload);
		// 内容
		etContent = findViewById(R.id.et_post_content);
		//标题
        etTitle = findViewById(R.id.et_post_title);

        tvCancel.setOnClickListener(this);

        tvIssue.setOnClickListener(this);
	}


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.upload:
                Toast.makeText(this, "发布好没做", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
