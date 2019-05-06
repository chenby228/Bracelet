package kpy.bracelet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import kpy.adapter.CommonAdapter;
import kpy.adapter.CommonViewHolder;
import kpy.bean.StepData;
import kpy.utils.DbUtils;


public class HistoryActivity extends BaseActivity {
    private LinearLayout layout;
    private ImageView iv_left;
    private ImageView iv_right;
    private ListView lv;
    private List<StepData> stepDatas;
    private static String CURRENTDATE = "";

    private void assignViews() {
        layout = (LinearLayout) findViewById(R.id.layout_titlebar);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        lv = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        assignViews();
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        initData();
    }

    private void initData() {
        setEmptyView(lv);
        if(DbUtils.getLiteOrm()==null){
            DbUtils.createDb(this, "step");
        }
        stepDatas =DbUtils.getQueryAll(StepData.class);
        lv.setAdapter(new CommonAdapter<StepData>(this,stepDatas,R.layout.item) {
            @Override
            protected void convertView(View item, StepData stepData) {
                TextView tv_date= CommonViewHolder.get(item,R.id.tv_date);
                TextView tv_step= CommonViewHolder.get(item,R.id.tv_step);
                tv_date.setText(stepData.getToday());
                tv_step.setText(stepData.getStep()+"步");
            }
        });

//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                Toast.makeText(HistoryActivity.this, "您选择的是" + lv.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(HistoryActivity.this).setTitle("我的提示").setMessage("确定要删除吗？")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                CURRENTDATE=stepDatas.get(position).getToday();
//                                Toast.makeText(HistoryActivity.this,"is "+CURRENTDATE, Toast.LENGTH_SHORT).show();
//                                DbUtils.deleteWhere(StepData.class, "today", new String[]{CURRENTDATE});
//                                stepDatas.remove(position);
////                                stepDatas =DbUtils.getQueryAll(StepData.class);
//                                lv.setAdapter(new CommonAdapter<StepData>(getApplicationContext(),stepDatas,R.layout.item) {
//                                    @Override
//                                    protected void convertView(View item, StepData stepData) {
//                                        TextView tv_date= CommonViewHolder.get(item,R.id.tv_date);
//                                        TextView tv_step= CommonViewHolder.get(item,R.id.tv_step);
//                                        tv_date.setText(stepData.getToday());
//                                        tv_step.setText(stepData.getStep()+"步");
//                                    }
//                                });
//                            }
//                        }).show();
//                return false;
//            }
//        });
    }

    protected <T extends View> T setEmptyView(ListView listView) {
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无数据！");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        return (T) emptyView;
    }
}
