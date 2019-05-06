package kpy.bracelet;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by KPY on 2017/4/17.
 */

public class BaseActivity extends FragmentActivity {
    protected void ShowToast(String toast){
        Toast.makeText(this,toast,Toast.LENGTH_LONG).show();
    }
}
