package kpy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

import kpy.App;
import kpy.bracelet.IssuePostActivity;
import kpy.bracelet.RaidersActivity;
import kpy.bracelet.R;
import kpy.fragment.shouye.ModelUtil;
import kpy.fragment.shouye.PanoramaImageModel;
import kpy.fragment.shouye.adapter.PanoramaImageAdapter;

public class ShouYeFragment extends Fragment implements View.OnClickListener {
    //    public VrPanoramaView vrPanoramaView;
    private RecyclerView recyclerView;

    private PanoramaImageAdapter mAdapter;
    private int currPosition = 0;

    private ImageView sousuo;
    private ImageView shangchuan;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_shouye,container,false);
        initView();
        initListener();
        return view;
    }


    private void initView() {

        currPosition = new Random().nextInt(ModelUtil.getPanoramaImageList().size());
        sousuo = view.findViewById(R.id.iv_sousuo);
        sousuo.setOnClickListener(this);
        shangchuan = view.findViewById(R.id.shangchuan);
        shangchuan.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        mAdapter = new PanoramaImageAdapter(App.getContext(), ModelUtil.getPanoramaImageList());
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getContext()));
        recyclerView.setAdapter(mAdapter);

    }

    private Intent intent;
    private void initListener() {

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            currPosition = position;
            //TODO
            PanoramaImageModel panoramaImageModel = mAdapter.getData().get(position);
            intent = new Intent(App.getContext(), RaidersActivity.class);
            intent.putExtra("panoramaImageModel",panoramaImageModel);
            startActivity(intent);

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shangchuan:
                startActivity(new Intent(App.getContext(), IssuePostActivity.class));
                break;
            case R.id.iv_sousuo:
                Toast.makeText(App.getContext(),"搜索功能还么做", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
