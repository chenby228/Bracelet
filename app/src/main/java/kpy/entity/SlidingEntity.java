package kpy.entity;

/**
 * 右部展开控制对象
 * 
 * @author gdpancheng@gmail.com 2013-10-28 上午10:05:43
 */
public class SlidingEntity {

	private boolean isSlidingEnable = false;

	/**
	 * 因为viewpage在按下去的时候 我们设置了 右侧菜单禁止
	 * 当在viewpage在触摸后抬起的时候 我们得还原它之前的状态
	 */
	private boolean isViewPage = false;
	
	/**
	 * 当在fragment 按了后退按钮的时候 那么如果那个页面有右侧导航
	 * 则恢复
	 */
	private boolean isResume = false;

	public boolean isSlidingEnable() {
		return isSlidingEnable;
	}

	public void setSlidingEnable(boolean isSlidingEnable) {
		this.isSlidingEnable = isSlidingEnable;
	}

	public boolean isViewPage() {
		return isViewPage;
	}

	public void setViewPage(boolean isViewPage) {
		this.isViewPage = isViewPage;
	}

	public boolean isResume() {
		return isResume;
	}

	public void setResume(boolean isResume) {
		this.isResume = isResume;
	}

	@Override
    public String toString() {
	    return "SlidingEntity [isSlidingEnable=" + isSlidingEnable + ", isViewPage=" + isViewPage + ", isResume=" + isResume + "]";
    }

}
