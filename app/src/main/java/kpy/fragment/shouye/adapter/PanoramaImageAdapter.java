package kpy.fragment.shouye.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import kpy.bracelet.R;
import kpy.fragment.shouye.PanoramaImageModel;

public class PanoramaImageAdapter extends BaseMultiItemQuickAdapter<PanoramaImageModel, BaseViewHolder> {

    private Context context;

    public PanoramaImageAdapter(Context context, List<PanoramaImageModel> data) {
        super(data);
        this.context = context;
        addItemType(0, R.layout.item_panorana_image);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected void convert(BaseViewHolder helper, PanoramaImageModel item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_desc, item.getDesc());
        helper.setImageResource(R.id.imageView, item.getResourceName());
    }

}
