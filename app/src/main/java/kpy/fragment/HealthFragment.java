package kpy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import kpy.bracelet.R;


public class HealthFragment extends BaseFragment implements View.OnClickListener{

	private TextView physical;
	private TextView motion;
//	private TextView sleep;
	private Fragment physical_tab;
	private Fragment motion_tab;
//	private Fragment sleep_tab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.inflater=inflater;
		View rootView = inflater.inflate(R.layout.activity_health, container, false);
		initView(rootView);
		initEvent();
		SetSelect(1);
		changeBackground(motion);
		return rootView;
	}

	private void initView(View rootView) {

		physical = (TextView) rootView.findViewById(R.id.physical);
		motion = (TextView) rootView.findViewById(R.id.motion);
//		sleep = (TextView) rootView.findViewById(R.id.sleep);
	}

	public void initEvent()
	{
		physical.setOnClickListener(this);
		motion.setOnClickListener(this);
//		sleep.setOnClickListener(this);
	}

	public void onClick(View v){
		ResetImg();
		switch (v.getId()) {
		case R.id.physical:
			SetSelect(0);
			changeBackground(v);
			break;
		case R.id.motion:
			SetSelect(1);
			changeBackground(v);
			break;
//		case R.id.sleep:
//			SetSelect(2);
//			changeBackground(v);
//			break;
		}
	}

	private void ResetImg() {
		physical.setBackgroundResource(R.drawable.transparent);
		changeTextColor(physical,R.color.white);
		motion.setBackgroundResource(R.drawable.transparent);
		changeTextColor(motion,R.color.white);
//		sleep.setBackgroundResource(R.drawable.transparent);
//		changeTextColor(sleep,R.color.white);
	}

	private void SetSelect(int i) {
		FragmentManager fragmentManager=getFragmentManager();
		FragmentTransaction transaction=fragmentManager.beginTransaction();
		hideFragment(transaction);
		switch (i)
		{
			case 0:
				if(physical_tab==null)
				{
					physical_tab=new HealthPhysicalFragment();
					transaction.add(R.id.content_health,physical_tab);
				}
				else
				{
					transaction.show(physical_tab);
				}
				break;
			case 1:
				if(motion_tab==null)
				{
					motion_tab=new HealthMotionFragment();
					transaction.add(R.id.content_health,motion_tab);
				}
				else
				{
					transaction.show(motion_tab);
				}
				break;
//			case 2:
//				if(sleep_tab==null)
//				{
//					sleep_tab=new HealthSleepFragment();
//					transaction.add(R.id.content_health,sleep_tab);
//				}
//				else
//				{
//					transaction.show(sleep_tab);
//				}
//				break;
			default:
				break;
		}
		transaction.commit();
	}

	private void hideFragment(FragmentTransaction transaction) {
		if(physical_tab!=null)
		{
			transaction.hide(physical_tab);
		}
		if(motion_tab!=null)
		{
			transaction.hide(motion_tab);
		}
//		if(sleep_tab!=null)
//		{
//			transaction.hide(sleep_tab);
//		}
	}

	private void changeBackground(View v) {
		v.setBackgroundResource(R.drawable.login_button_pressed);
		changeTextColor((TextView)v,R.color.yellow);
	}

	private void changeTextColor(TextView v, int color) {
		v.setTextColor(getResources().getColor(color,null));
	}

}