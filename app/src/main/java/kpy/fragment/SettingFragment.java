package kpy.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kpy.bracelet.R;
import kpy.bracelet.SetAddressActivity;
import kpy.bracelet.SetEmailActivity;
import kpy.bracelet.SetNameActivity;
import kpy.bracelet.SetSignActivity;
import kpy.sqlite.UserInfoDBHelper;

import static android.app.Activity.RESULT_OK;

/**
 * Created by KPY on 2017/4/17.
 */

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private View view;

    private ImageView user_picture;

    private ImageButton user_name;
    private ImageButton user_email;
    private ImageButton user_sex;
    private ImageButton user_address;
    private ImageButton user_sign;
    private Button user_setting;

    private TextView user_name_text;
    private TextView user_number_text;
    private TextView user_email_text;
    private TextView user_sex_text;
    private TextView user_address_text;
    private TextView user_sign_text;

    private SQLiteDatabase database;

    private AlertDialog alertDialog;

    private String[] items = new String[]{"选择本地图片", "拍照"};

    private String[] sexes = new String[]{"男", "女"};

    private static String path = "/sdcard/MyImage/"; //sd路径

    private Bitmap head;//头像Bitmap


    private SQLiteDatabase userinfo;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String phone;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.actiity_mysetting, container, false);
        userinfo=UserInfoDBHelper.getInstance().getWritableDatabase();
        sp = this.getActivity().getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        editor = sp.edit();
        phone=sp.getString("user","");
        initView();
        initEvent();
        initUserData();
        return view;
    }


    @Override
    public void onResume() {
        initUserData();
        super.onResume();
    }

    private void initUserData() {

        Cursor cursor = userinfo.rawQuery("select * from UserInfo where user_id=?", new String[]{phone});
        while (cursor.moveToNext())
        {
            user_picture = (ImageView) view.findViewById(R.id.user_picture);
            user_name_text = (TextView) view.findViewById(R.id.user_name_text);
            user_number_text = (TextView) view.findViewById(R.id.user_number_text);
            user_email_text = (TextView) view.findViewById(R.id.user_email_text);
            user_sex_text = (TextView) view.findViewById(R.id.user_sex_text);
            user_address_text = (TextView) view.findViewById(R.id.user_address_text);
            user_sign_text = (TextView) view.findViewById(R.id.user_sign_text);

            user_number_text.setText(cursor.getString(cursor.getColumnIndex("user_id")));
            user_name_text.setText(cursor.getString(cursor.getColumnIndex("user_name")));
            user_email_text.setText(cursor.getString(cursor.getColumnIndex("user_email")));
            user_sex_text.setText(cursor.getString(cursor.getColumnIndex("user_sex")));
            user_address_text.setText(cursor.getString(cursor.getColumnIndex("user_address")));
            user_sign_text.setText(cursor.getString(cursor.getColumnIndex("user_sign")));
            if(cursor.getString(cursor.getColumnIndex("user_photo"))==null)
            {
                Toast.makeText(getActivity(),"未设置头像",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Bitmap bm = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("user_photo")));
                //将图片显示到ImageView中
                user_picture.setImageBitmap(bm);
            }
        }
    }

    private void initView() {

        user_picture = (ImageView) view.findViewById(R.id.user_picture);
        user_name = (ImageButton) view.findViewById(R.id.user_name);
        user_email = (ImageButton) view.findViewById(R.id.user_email);
        user_sex = (ImageButton) view.findViewById(R.id.user_sex);
        user_address = (ImageButton) view.findViewById(R.id.user_address);
        user_sign = (ImageButton) view.findViewById(R.id.user_sign);
        user_setting = (Button) view.findViewById(R.id.user_setting);

        user_name_text = (TextView) view.findViewById(R.id.user_name_text);
        user_number_text = (TextView) view.findViewById(R.id.user_number_text);
        user_email_text = (TextView) view.findViewById(R.id.user_email_text);
        user_sex_text = (TextView) view.findViewById(R.id.user_sex_text);
        user_address_text = (TextView) view.findViewById(R.id.user_address_text);
        user_sign_text = (TextView) view.findViewById(R.id.user_sign_text);
    }

    private void initEvent() {
        user_picture.setOnClickListener(this);
        user_name.setOnClickListener(this);
        user_email.setOnClickListener(this);
        user_sex.setOnClickListener(this);
        user_address.setOnClickListener(this);
        user_sign.setOnClickListener(this);
        user_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_picture:
                ChangePhoto();
                break;
            case R.id.user_name:
                ChangeName();
                break;
            case R.id.user_email:
                ChangeEmail();
                break;
            case R.id.user_sex:
                ChangeSex();
                break;
            case R.id.user_address:
                ChangeAddress();
                break;
            case R.id.user_sign:
                ChangeSign();
                break;
            case R.id.user_setting:
                editor.clear();
                editor.commit();
                System.exit(0);
                break;
            default:
                break;
        }
    }

    private void ChangeSign() {
        Intent intent = new Intent(getContext(),SetSignActivity.class);
//        startActivityForResult(intent,1);
        startActivity(intent);
    }

    private void ChangeAddress() {
        Intent intent = new Intent(getContext(),SetAddressActivity.class);
//        startActivityForResult(intent,1);
        startActivity(intent);
    }

    private void ChangeEmail() {
        Intent intent = new Intent(getContext(),SetEmailActivity.class);
//        startActivityForResult(intent,1);
        startActivity(intent);
    }


    private void ChangeName() {
        Intent intent = new Intent(getContext(),SetNameActivity.class);
//        startActivityForResult(intent,1);
        startActivity(intent);
    }

    private void ChangeSex() {
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("性别")
                .setItems(sexes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //男
                                user_sex_text.setText("男");
                                ContentValues values=new ContentValues();
                                values.put("user_sex","男");
                                userinfo.update("UserInfo",values,"user_id=?",new String[]{phone});
                                values.clear();
                                break;
                            case 1: //女
                                user_sex_text.setText("女");
                                ContentValues value=new ContentValues();
                                value.put("user_sex","女");
                                userinfo.update("UserInfo",value,"user_id=?",new String[]{phone});
                                value.clear();
                                break;
                        }
                    }
                }).create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }


    private void ChangePhoto() {
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("更换头像")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //相册
                                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT, null);
                                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent1, 1);
                                break;
                            case 1: //相机
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path, getTodayDate()+".jpg")));
                                startActivityForResult(intent2, 2);
                                break;
                        }
                    }
                }).create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    CropPhoto(data.getData());//裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    SetPicToView(bitmap);
                    user_picture.setImageBitmap(bitmap);

//                    CropPhoto(data.getData());//裁剪图片
//                    File temp = new File(path+getTodayDate()+".jpg");
//                    CropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case 3:
                Bundle extras = data.getExtras();
                head = extras.getParcelable("data");
                SetPicToView(head);//保存在SD卡中
                user_picture.setImageBitmap(head);
                Toast.makeText(getActivity(), "更换头像成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    Bitmap cursorToBmp(Cursor c, int columnIndex) {
        byte[] data = c.getBlob(columnIndex);
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }

    public void CropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }


    private void SetPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        Toast.makeText(getActivity(), "保存"+Environment.getExternalStorageDirectory(), Toast.LENGTH_SHORT).show();

        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + getTodayDate()+".jpg";//
        ContentValues value=new ContentValues();
        value.put("user_photo",fileName);
        userinfo.update("UserInfo",value,"user_id=?",new String[]{phone});
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
        return sdf.format(date);
    }
}
