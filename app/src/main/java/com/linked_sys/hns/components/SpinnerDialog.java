package com.linked_sys.hns.components;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import com.linked_sys.hns.R;


public class SpinnerDialog extends Dialog {
	Context context;
	public SpinnerDialog(Context context) {
		super(context);
		this.context=context;
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated constructor stub
	}
	//private CircularProgressDrawable mDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		RelativeLayout rl=new RelativeLayout(context);
		ProgressBar prog=new ProgressBar(context);
		rl.addView(prog);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(getPx(37), getPx(37));
		p.setMargins(getPx(6), getPx(6), getPx(6), getPx(6));
		prog.setLayoutParams(p);
		//prog.setPadding(getPx(12), getPx(12), getPx(12), getPx(12));

		setContentView(rl);
		getWindow().setLayout(getPx(37), getPx(37));
		getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.spinner_progress_dialog_bg));
 


	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		super.hide();

	}
	public int getPx(int dimensionDp) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dimensionDp * density + 0.5f);
	}
}
