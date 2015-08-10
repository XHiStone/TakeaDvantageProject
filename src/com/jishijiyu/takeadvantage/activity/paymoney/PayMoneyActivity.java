package com.jishijiyu.takeadvantage.activity.paymoney;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.jishijiyu.takeadvange.alipay.PayResult;
import com.jishijiyu.takeadvange.alipay.SignUtils;
import com.jishijiyu.takeadvantage.R;
import com.jishijiyu.takeadvantage.activity.HeadBaseActivity;
import com.jishijiyu.takeadvantage.activity.ernieonermb.OneRmbErnieActivity;
import com.jishijiyu.takeadvantage.activity.ernieonermb.RegistrationDetailActivity;
import com.jishijiyu.takeadvantage.activity.merchant_account.MentionActivity;
import com.jishijiyu.takeadvantage.entity.request.PayMoneyRequest;
import com.jishijiyu.takeadvantage.entity.request.QueryGoldNumRequest;
import com.jishijiyu.takeadvantage.entity.request.RefuseInviteReuqest;
import com.jishijiyu.takeadvantage.entity.result.GoldCoinResult;
import com.jishijiyu.takeadvantage.entity.result.LoginUserResult;
import com.jishijiyu.takeadvantage.entity.result.PayMoneyResult;
import com.jishijiyu.takeadvantage.entity.result.QueryGoldNumResult;
import com.jishijiyu.takeadvantage.entity.result.RefuseInviteResult;
import com.jishijiyu.takeadvantage.utils.AppManager;
import com.jishijiyu.takeadvantage.utils.Arith;
import com.jishijiyu.takeadvantage.utils.Constant;
import com.jishijiyu.takeadvantage.utils.DialogUtils;
import com.jishijiyu.takeadvantage.utils.GetUserIdUtil;
import com.jishijiyu.takeadvantage.utils.HttpConnectTool;
import com.jishijiyu.takeadvantage.utils.IntentActivity;
import com.jishijiyu.takeadvantage.utils.ToastUtils;
import com.jishijiyu.takeadvantage.utils.Util;
import com.jishijiyu.takeadvantage.utils.HttpConnectTool.ConnectListener;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 
 * @author zhengjianxiong
 * @Content 支付界面
 * @time 2015-7-13
 * 
 */

public class PayMoneyActivity extends HeadBaseActivity {
	public int checkedNum;
	private int userGoldNum;
	private TextView tv_pay_gold_num;
	private double mul = 0;
	private Button immediate_payment;
	private int goldNum;
	public int arr;
	public CheckBox checkBox;
	public String gold_um1;
	public int arr_gold;
	/**
	 * 是否选择邀请
	 */
	public boolean isCheck = false;
	private AlertDialog alertDialog = null;
	private Intent intent = null;
	private String RoomId, UserId, TokenId;
	// 选择支付方式
	public CheckBox cb_zhifubao, cb_weixin, have_gold_radio;

	// 剩余金币
	public TextView pay_gold_num_textview, price;

	public int cbox = 3;

	// 获取用户信息
	private LoginUserResult login;
	public String gold_num;
	// 需要支付金额
	public double data;
	// 信息
	public Message msg;

	//
	public PayMoneyResult payMoneyResult;

	private static final int SUCCESS = 0;
	private static final int FAIL = 1;
	private static final int SUCC = 4;
	private static final int SUCCFALI = 5;

	// 获取支付的roomIds
	public String roomIds = null;

	private QueryGoldNumResult queryGoldNumResult;
	/**
	 * refuseInviteSucc 不在接受他人邀请成功 refuseInviteFail 不在接受他人邀请失败
	 * 
	 */
	public static final int refuseInviteSucc = 6;
	public static final int refuseInviteFail = 7;

	public Bundle bundle;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 返回
		case R.id.btn_top_left:
			finish();
			break;
		case R.id.immediate_payment:
			if (cbox == 3) {
				if (goldNum < arr_gold && cb_zhifubao.isChecked() == false
						&& cb_weixin.isChecked() == false) {
					// 选中金币支付
					ToastUtils.makeText(PayMoneyActivity.this,
							"金币不足，请选择另一种支付方式!", ToastUtils.LENGTH_LONG).show();
				} else {

					PayGoldMoney(RoomId, UserId, TokenId);

				}

			} else if (cbox == 1) {
				// 支付宝付款
				// 检查支付宝是否可以
				if (Util.checkLocalAppExistOrNot(PayMoneyActivity.this,
						"com.eg.android.AlipayGphone")) {
					pay("加入房间", "加入房间", price.getText().toString(), UserId,
							RoomId);
				} else {
					ToastUtils.makeText(PayMoneyActivity.this,
							"没有支付宝应用，请你到软件商店下载!", 0).show();
				}

			} else if (cbox == 2) {

				// 微信付款
				ToastUtils.makeText(PayMoneyActivity.this, "选择微信",
						ToastUtils.LENGTH_LONG).show();
			} else {
				ToastUtils.makeText(PayMoneyActivity.this, "请选择支付方式",
						ToastUtils.LENGTH_LONG).show();
			}
			break;
		case R.id.btn_yes:
			// 跳转到房间界面
			// 拒接邀请
			if (isCheck == true) {
				/**
				 * 执行不在接受他人邀请
				 */
				RefuseInvite(RoomId);
			} else {
				alertDialog.dismiss();
			}
			break;

		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				break;
			case FAIL:
				ToastUtils.makeText(PayMoneyActivity.this, "访问服务器失败!", 0)
						.show();
				alertDialog.dismiss();
				
				break;
			case SUCC:
				ToastUtils.makeText(PayMoneyActivity.this, "创建房间成功!", 0).show();
//				alertDialog = DialogUtils.PayMoneyDialog(PayMoneyActivity.this,
//						PayMoneyActivity.this);
//				checkBox = (CheckBox) alertDialog.findViewById(R.id.is_check);
//				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						// TODO Auto-generated method stub
//						if (checkBox.isChecked()) {
//							isCheck = true;
//						} else {
//							isCheck = false;
//						}
//					}
//				});
				finish();
				break;
			case SUCCFALI:
				ToastUtils.makeText(PayMoneyActivity.this, "创建房间失败!", 0).show();
				break;
			case refuseInviteSucc:
//				ToastUtils.makeText(PayMoneyActivity.this, "设置不在接受他人邀请成功!", 0)
//						.show();
//				alertDialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(PayMoneyActivity.this,
						OneRmbErnieActivity.class);
				startActivity(intent);
				PayMoneyActivity.this.finish();
				break;
			case refuseInviteFail:
//				ToastUtils.makeText(PayMoneyActivity.this, "设置不在接受他人邀请失败!", 0)
//						.show();
//				alertDialog.dismiss();
				break;
			default:
				break;
			}

		}

	};

	/**
	 * 
	 * @param roomIds房间ID
	 * @param UserId用户ID
	 * @param TokenId
	 */
	private void PayGoldMoney(String roomIds, String UserId, String TokenId) {
		// TODO Auto-generated method stub
		PayMoneyRequest payMoneyRequest = new PayMoneyRequest();
		PayMoneyRequest.Pramater pramater = payMoneyRequest.p;
		pramater.userId = UserId;
		pramater.tokenId = TokenId;
		pramater.roomIds = roomIds;
		final Gson gson = new Gson();
		String json = gson.toJson(payMoneyRequest);
		HttpConnectTool.update(json, PayMoneyActivity.this,
				new ConnectListener() {

					@Override
					public void contectSuccess(String result) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(result)) {
							payMoneyResult = gson.fromJson(result,
									PayMoneyResult.class);
							if (payMoneyResult.p.isTrue) {
								if (payMoneyResult.p.isSucce) {
									msg = new Message();
									msg.what = SUCC;
									handler.sendMessage(msg);
									QueryGoldNum();
								} else {
									msg = new Message();
									msg.what = SUCCFALI;
									handler.sendMessage(msg);
								}
							} else {
								IntentActivity.mIntent(PayMoneyActivity.this);
							}

						}
					}

					@Override
					public void contectStarted() {
						// TODO Auto-generated method stub

					}

					@Override
					public void contectFailed(String path, String request) {
						// TODO Auto-generated method stub
						msg = new Message();
						msg.what = FAIL;
						handler.sendMessage(msg);
					}
				});
	}

	@Override
	public void appHead(View view) {
		// TODO Auto-generated method stub
		btn_left.setVisibility(View.VISIBLE);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_left.setText("返回");
		top_text.setText("支付订单");
	}

	@Override
	public void initReplaceView() {
		// TODO Auto-generated method stub
		FrameLayout base_centent = (FrameLayout) findViewById(R.id.base_content);
		View view = View.inflate(PayMoneyActivity.this,
				R.layout.pay_order_form, null);
		login = GetUserIdUtil.getLogin(PayMoneyActivity.this);
		// 获取当前金币数量
		goldNum = login.p.user.goldNum;
		base_centent.addView(view);
		AppManager.getAppManager().addActivity(this);
		bundle = getIntent().getExtras();
		if (bundle != null) {
			RoomId = bundle.getString("RoomId");
			UserId = bundle.getString("UserId");
			TokenId = bundle.getString("TokenId");
			if (bundle.getString("checkedNum") != null) {
				checkedNum = Integer.parseInt(bundle.getString("checkedNum"));
				mul = Arith.mul(checkedNum, 100);
			}
		}
		initView();
		initOnclick();
	}

	private void initView() {
		tv_pay_gold_num = (TextView) findViewById(R.id.tv_pay_gold_num);
		immediate_payment = (Button) findViewById(R.id.immediate_payment);
		cb_zhifubao = (CheckBox) findViewById(R.id.cb_zhifubao);
		cb_weixin = (CheckBox) findViewById(R.id.cb_weixin);
		have_gold_radio = (CheckBox) findViewById(R.id.have_gold_radio);
		pay_gold_num_textview = (TextView) findViewById(R.id.pay_gold_num_textview);
		price = (TextView) findViewById(R.id.price);
		pay_gold_num_textview.setText(goldNum + "金币");
		// 选中
		int arr = (new Double(mul)).intValue();
		if (arr == 0) {
			tv_pay_gold_num.setText("100");
		} else {
			tv_pay_gold_num.setText(mul + "");
		}
		String data_arr = tv_pay_gold_num.getText().toString();
		arr_gold = (new Double(data_arr)).intValue();// 需要的金币
		if (bundle != null && bundle.getString("checkedNum") != null) {
			if (arr_gold < goldNum) {
				price.setText("0.0");
			} else {
				Arith.sub(arr_gold, goldNum);
				cb_zhifubao.setFocusable(true);
				price.setText(Arith.div(Arith.sub(arr_gold, goldNum), 100) + "");
				if (cb_zhifubao.isChecked() == false
						|| cb_weixin.isChecked() == false) {
					ToastUtils.makeText(PayMoneyActivity.this,
							"金币不足，请选择一种支付方式!", ToastUtils.LENGTH_LONG).show();
				}
			}

		} else {
			if (arr_gold < goldNum) {
				price.setText("0.0");
			} else {
				double sub = Arith.sub(arr_gold, goldNum);
				data = Arith.div(sub, 100);
				price.setText(data + "");
				cb_zhifubao.setFocusable(true);
				immediate_payment.setFocusable(false);
				immediate_payment.setFocusableInTouchMode(false);
				if (cb_zhifubao.isChecked() == false
						|| cb_weixin.isChecked() == false) {
					ToastUtils.makeText(PayMoneyActivity.this,
							"金币不足，请选择一种支付方式!", ToastUtils.LENGTH_LONG).show();
				}
			}
		}
	}

	public void initOnclick() {
		immediate_payment.setOnClickListener(this);
		cb_zhifubao.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (cb_zhifubao.isChecked()) {
					cb_weixin.setChecked(false);
					cbox = 1;
				} else {
					cbox = 0;
				}
			}
		});
		cb_weixin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (cb_weixin.isChecked()) {
					cb_zhifubao.setChecked(false);
					cbox = 2;
				} else {
					cbox = 0;
				}
			}
		});
		have_gold_radio
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (have_gold_radio.isChecked()) {
							cbox = 3;
							gold_num = tv_pay_gold_num.getText().toString();
							int num = (new Double(gold_num)).intValue();
							if (bundle != null
									&& bundle.getString("checkedNum") != null) {
								if (num < goldNum) {
									price.setText("0.0");
								} else {
									Arith.sub(num, goldNum);
									cb_zhifubao.setFocusable(true);
									price.setText(Arith.div(
											Arith.sub(num, goldNum), 100)
											+ "");
									immediate_payment.setFocusable(false);
									immediate_payment
											.setFocusableInTouchMode(false);
									if (cb_zhifubao.isChecked() == false
											|| cb_weixin.isChecked() == false) {
										ToastUtils.makeText(
												PayMoneyActivity.this,
												"金币不足，请选择一种支付方式!",
												ToastUtils.LENGTH_LONG).show();
									}
								}

							} else {
								if (num < goldNum) {
									price.setText("0.0");
								} else if (num > goldNum) {
									double sub = Arith.sub(num, goldNum);
									data = Arith.div(sub, 100);
									price.setText(data + "");
									immediate_payment.setFocusable(false);
									immediate_payment
											.setFocusableInTouchMode(false);
									if (cb_zhifubao.isChecked() == false
											|| cb_weixin.isChecked() == false) {

										ToastUtils.makeText(
												PayMoneyActivity.this,
												"金币不足，请选择一种支付方式!",
												ToastUtils.LENGTH_LONG).show();
									}
								}
							}
						} else {
							gold_num = tv_pay_gold_num.getText().toString();

							price.setText(Arith.div(
									Double.parseDouble(gold_num), 100)
									+ "");
						}
					}
				});
	}

	private void RefuseInvite(String roomId) {
		RefuseInviteReuqest refuseInviteReuqest = new RefuseInviteReuqest();
		RefuseInviteReuqest.Parameter parameter = refuseInviteReuqest.p;
		parameter.userId = GetUserIdUtil.getUserId(PayMoneyActivity.this);
		parameter.tokenId = GetUserIdUtil.getTokenId(PayMoneyActivity.this);
		parameter.roomId = roomId;
		final Gson gson = new Gson();
		String json = gson.toJson(refuseInviteReuqest);
		HttpConnectTool.update(json, PayMoneyActivity.this,
				new ConnectListener() {
					@Override
					public void contectSuccess(String result) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(result)) {
							RefuseInviteResult reInviteResult = gson.fromJson(
									result, RefuseInviteResult.class);
							if (reInviteResult.p.isTrue) {
								if (reInviteResult.p.isSucce) {
									msg = new Message();
									msg.what = refuseInviteSucc;
									handler.sendMessage(msg);
								} else {
									msg = new Message();
									msg.what = refuseInviteFail;
									handler.sendMessage(msg);
								}
							} else {
								IntentActivity.mIntent(PayMoneyActivity.this);
							}
						}
					}

					@Override
					public void contectStarted() {
						// TODO Auto-generated method stub

					}

					@Override
					public void contectFailed(String path, String request) {
						// TODO Auto-generated method stub
						msg = new Message();
						msg.what = FAIL;
						handler.sendMessage(msg);
					}
				});
	}

	private void QueryGoldNum() {
		// TODO Auto-generated method stub
		QueryGoldNumRequest queryGoldNumRequest = new QueryGoldNumRequest();
		QueryGoldNumRequest.User user = queryGoldNumRequest.p;
		user.userId = GetUserIdUtil.getUserId(PayMoneyActivity.this);
		user.tokenId = GetUserIdUtil.getTokenId(PayMoneyActivity.this);
		final Gson gson = new Gson();
		String json = gson.toJson(queryGoldNumRequest);
		HttpConnectTool.update(json, PayMoneyActivity.this,
				new ConnectListener() {

					@Override
					public void contectSuccess(String result) {

						if (!TextUtils.isEmpty(result)) {
							queryGoldNumResult = gson.fromJson(result,
									QueryGoldNumResult.class);
							if (queryGoldNumResult.p.isTrue) {
								msg = new Message();
								msg.what = SUCCESS;
								handler.sendMessage(msg);
								// 获取金币数量
								userGoldNum = queryGoldNumResult.p.user.goldNum;
								LoginUserResult login = GetUserIdUtil
										.getLogin(PayMoneyActivity.this);
								login.p.user.goldNum = userGoldNum;
								GetUserIdUtil.saveLoginUserResult(
										PayMoneyActivity.this, login);
							} else {
								IntentActivity.mIntent(PayMoneyActivity.this);
							}
						}

					}

					@Override
					public void contectStarted() {
						// TODO Auto-generated method stub

					}

					@Override
					public void contectFailed(String path, String request) {
						// TODO Auto-generated method stub
						msg = new Message();
						msg.what = FAIL;
						handler.sendMessage(msg);
					}
				});

	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String commName, String commDes, String commPrice,
			String userId, String roomId) {
		// 订单
		String orderInfo = getOrderInfo(commName, commDes, commPrice, userId,
				roomId);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayMoneyActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(PayMoneyActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(PayMoneyActivity.this);
		String version = payTask.getVersion();
		Toast.makeText(PayMoneyActivity.this, version, Toast.LENGTH_SHORT)
				.show();
	}

	// 支付
	// 商户PID
	public static final String PARTNER = "2088911869365628";
	// 商户收款账号
	public static final String SELLER = "jishijiyu2015@163.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMgPFtsXAjDbRyv2CCWF9sHbHpPaAZtMVV09bdZkuoDUTi05U0kF8OgBfcv3ez/tIuQ71nEyzOkF6A7LeAMNjNNxDN0YGojIzXec1RX4qIOUDocf934o7c5NpWBxs9sKqwkUM2pymdhHYaPkqJdW54MCmz8PfX58azoPoQTzYceHAgMBAAECgYBarLwnQR51Fm4DtteqajWVV8NcMAtaYBaw1A7chXsiuRdV2A2vo0m1XjeuItVx/AE8gQKI/AVz+IFdc0tJ1vJkdksamfrvEIb7d3kjmrO5KDHGd7R/UUjJSjFSkvIUQpRQ3Vt/ur1qEdTPwC1vFnFAykUlhfvDPU//OAD+vswPgQJBAOMVz1jV7Fv5AC6VkLjrcJghh1+ZfsDUhiIqQtfAMQA4OfsTjSEzpu6FOG1mZ2lb/64TcLRclSFrpD/9K+sv208CQQDhiFEzz0EcCnyLyWNLQi1tCt1VivWiokHeuG/0yKXuy7iJvwTon9winAJ8UOEWkMJ0iibtAluFt7QUK+QQuGJJAkAeIMuktqUoQq4CgDd2QU5r8K7cQSou/UNajw7VPxuBMvSGWj60M/0m0if9Y8O+l8UhXFTsqQfkKjrdfoJwLqZDAkAx+JtNaHmbtfMcofWfQj2AcKzT/GMqstr3d1RPH3osq/TqiGmVBX6oKHsggmctMcv0OHwhfak7upVq9sKktcYJAkB39WTFpPcBzP7ALuVTb2qsZEprT9rOLKob9UiNDkWHwHVbeFtteTQmNE3Zi5C7zhBEp5LNX9syXKidRWoU3Rkb";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					// ToastUtils.makeText(PayMoneyActivity.this, "支付成功!", 0)
					// .show();
					QueryGoldNum();
					alertDialog = DialogUtils.PayMoneyDialog(
							PayMoneyActivity.this, PayMoneyActivity.this);
					checkBox = (CheckBox) alertDialog
							.findViewById(R.id.is_check);
					checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (checkBox.isChecked()) {
								isCheck = true;
							} else {
								isCheck = false;
							}
						}
					});
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						ToastUtils.makeText(PayMoneyActivity.this, "支付结果确认中!",
								0).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						ToastUtils.makeText(PayMoneyActivity.this, "支付失败!", 0)
								.show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(PayMoneyActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		}

	};

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price,
			String userId, String roomId) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "H" + userId
				+ "H" + roomId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url="
				+ "\""
				+ "http://103.44.145.243:34461/AppServer/alipayEnrollRoomNotifyUrl.html"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
