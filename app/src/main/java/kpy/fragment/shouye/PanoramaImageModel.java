package kpy.fragment.shouye;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * @author sunfusheng on 2017/8/25.
 */
public class PanoramaImageModel implements MultiItemEntity, Serializable {

    private int type;
    private String title;
    private String desc;
    private int resourceName;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }





    public int getResourceName() {
        return resourceName;
    }

    public void setResourceName(int resourceName) {
        this.resourceName = resourceName;
    }

    public PanoramaImageModel(int type, String title, String desc, int resourceName) {
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.resourceName = resourceName;
    }

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public String toString() {
        return "PanoramaImageModel{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", resourceName=" + resourceName +
                '}';
    }
}
