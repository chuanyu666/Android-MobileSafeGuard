package com.yc.mobilesafeguard.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.db.dao.BlackNumberDao;
import com.yc.mobilesafeguard.domain.BlackNumberInfo;

public class CallSMSActivity extends Activity {

	private ListView lv_block_list;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	private CallSMSAdapter adapter;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxnumber = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_block_list = (ListView) findViewById(R.id.lv_block_list);
		dao = new BlackNumberDao(this);
		fillData();

		lv_block_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					int lastPosition = lv_block_list.getLastVisiblePosition();
					if(lastPosition==infos.size()-1){
						System.out.println("loading more data");
						offset += maxnumber;
						fillData(); 
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:

					break;
				case OnScrollListener.SCROLL_STATE_FLING:

					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				if(infos==null){
					infos = dao.findPart(offset, maxnumber);					
				}else{
					 infos.addAll(dao.findPart(offset, maxnumber));
				}
				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter == null){
							adapter = new CallSMSAdapter();							 
							lv_block_list.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
					}
				});
			};
		}.start();
	}

	private class CallSMSAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewHolder holder;
			// release view creation
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_blacknumber, null);
				// reduce search view
				holder = new ViewHolder();
				holder.tv_black_number = (TextView) view
						.findViewById(R.id.tv_black_number);
				holder.tv_black_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			holder.tv_black_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_black_mode.setText("Block Call");
			} else if ("2".equals(mode)) {
				holder.tv_black_mode.setText("Block SMS");
			} else if ("3".equals(mode)) {
				holder.tv_black_mode.setText("Block Call&SMS");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new Builder(
							CallSMSActivity.this);
					builder.setTitle("Warning");
					builder.setMessage("Do you want to delete?");
					builder.setNegativeButton("cancel", null);
					builder.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dao.delete(infos.get(position).getNumber());
									infos.remove(position);
									adapter.notifyDataSetChanged();
								}
							});
					builder.show();
				}
			});
			return view;
		}
	}

	class ViewHolder {
		TextView tv_black_number;
		TextView tv_black_mode;
		ImageView iv_delete;
	}

	private EditText et_black_number;
	private CheckBox cb_call;
	private CheckBox cb_sms;
	private Button btn_cancel;
	private Button btn_confirm;

	public void addNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_set_blacknumber,
				null);
		et_black_number = (EditText) contentView
				.findViewById(R.id.et_black_number);
		cb_call = (CheckBox) contentView.findViewById(R.id.cb_call);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_SMS);
		btn_cancel = (Button) contentView.findViewById(R.id.btn_cancel);
		btn_confirm = (Button) contentView.findViewById(R.id.btn_confirm);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = et_black_number.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "Number is null",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String mode = "";
				if (cb_call.isChecked() && cb_sms.isChecked()) {
					mode = "3";
				} else if (cb_call.isChecked()) {
					mode = "1";
				} else if (cb_sms.isChecked()) {
					mode = "2";
				} else {
					Toast.makeText(getApplicationContext(),
							"Please check block mode", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				dao.insert(number, mode);
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(number);
				info.setMode(mode);
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}
}
