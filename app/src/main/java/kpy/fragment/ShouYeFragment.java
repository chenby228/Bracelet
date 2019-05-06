package kpy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import kpy.App;
import kpy.bracelet.R;
import kpy.fragment.shouye.ModelUtil;
import kpy.fragment.shouye.PanoramaImageModel;
import kpy.fragment.shouye.adapter.PanoramaImageAdapter;
import kpy.fragment.shouye.utils.ImageUtil;

public class ShouYeFragment extends Fragment {
    private ImageView ivMine;
    private TextView tvTitle;
    //    public VrPanoramaView vrPanoramaView;
    private RecyclerView recyclerView;

    private PanoramaImageAdapter mAdapter;
    private int currPosition = 0;

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
        tvTitle = view.findViewById(R.id.tv_title);
        ImageUtil.colorImageViewDrawable(ivMine, R.color.transparent60_white);

        currPosition = new Random().nextInt(ModelUtil.getPanoramaImageList().size());

        recyclerView = view.findViewById(R.id.recyclerView);
        mAdapter = new PanoramaImageAdapter(App.getContext(), ModelUtil.getPanoramaImageList());
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    private void initListener() {

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (currPosition == position) return;
            System.out.println("@@@@@@@@@  " + position);
            currPosition = position;
            PanoramaImageModel model = mAdapter.getData().get(position);
            System.out.println(model.toString());

            //flag
        });
    }

}
