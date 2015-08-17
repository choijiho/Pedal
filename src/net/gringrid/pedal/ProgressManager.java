package net.gringrid.pedal;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ProgressManager extends AsyncTask<Integer, String, Integer> {

	private ProgressDialog mProgressDialog;
	private Context mContext;
	
	public ProgressManager(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.progress_loading));
		mProgressDialog.show();
		mProgressDialog.setMax(100);
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		int taskCount = params.length;
		// TODO Auto-generated method stub
		return null;
	}

}
