package kpy.bracelet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import kpy.fragment.SettingFragment;
import kpy.fragment.ShouYeFragment;
import kpy.fragment.StepFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    /**
     * tab页面
     * */
    private LinearLayout firsttab;
    private LinearLayout secondtab;
    private LinearLayout thirdtab;

    private ImageButton firstimg;
    private ImageButton secondimg;
    private ImageButton thirdimg;

    private Fragment tab1;
    private Fragment tab2;
    private Fragment tab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
        InitEvent();
        SetSelect(0);
    }

    private void InitView() {
        //tab
        firsttab= (LinearLayout) findViewById(R.id.tab_first);
        secondtab= (LinearLayout) findViewById(R.id.tab_second);
        thirdtab= (LinearLayout) findViewById(R.id.tab_third);

        //image button
        firstimg= (ImageButton) findViewById(R.id.tab_first_img);
        secondimg= (ImageButton) findViewById(R.id.tab_second_img);
        thirdimg= (ImageButton) findViewById(R.id.tab_third_img);

    }

    private void InitEvent() {
        firsttab.setOnClickListener(this);
        secondtab.setOnClickListener(this);
        thirdtab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ResetImg();
        switch (v.getId())
        {
            case R.id.tab_first:
                SetSelect(0);
                break;
            case R.id.tab_second:
                SetSelect(1);
                break;
            case R.id.tab_third:
                SetSelect(2);
                break;
            default:
                break;
        }
    }

    private void SetSelect(int i) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (i)
        {
            case 0:
                if(tab1==null) {
                    tab1=new ShouYeFragment();
                    transaction.add(R.id.content_frame,tab1);
                }
                else {
                    transaction.show(tab1);
                }
                firstimg.setImageResource(R.mipmap.tab_main_selected);
                break;
            case 1:
                if(tab2==null) {
                    tab2=new StepFragment();
                    transaction.add(R.id.content_frame,tab2);
                }
                else {
                    transaction.show(tab2);
                }
                secondimg.setImageResource(R.mipmap.tab_sport_selected);
                break;
            case 2:

                if(tab3==null)
                {
                    tab3=new SettingFragment();
                    transaction.add(R.id.content_frame,tab3);
                }
                else
                {
                    transaction.show(tab3);
                }
                thirdimg.setImageResource(R.mipmap.tab_me_selected);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(tab1!=null)
        {
            transaction.hide(tab1);
        }
        if(tab2!=null)
        {
            transaction.hide(tab2);
        }
        if(tab3!=null)
        {
            transaction.hide(tab3);
        }
    }

    private void ResetImg() {
        firstimg.setImageResource(R.mipmap.tab_main_unselected);
        secondimg.setImageResource(R.mipmap.tab_sport_unselected);
        thirdimg.setImageResource(R.mipmap.tab_me_unselected);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
