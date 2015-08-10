package com.jishijiyu.takeadvantage.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jishijiyu.takeadvantage.R;
import com.jishijiyu.takeadvantage.utils.ToastUtils;

/**
 * 选择职业界面
 * 
 * @author shifeiyu
 * @version 2015年6月4日13:24:09
 * 
 */
public class SelectJobPopupWindow extends PopupWindow {
	Activity context;
	SonPopWindow sonpopwindow;
	public TextView select_trade_text, select_job_text, select_job_ok_btn,
			select_job_cancle_btn;
	public Button select_trade_btn, select_job_btn;
	private View mMenuView;
	public Dialog dialog;
	public ListView trade_list;
	public List<String> list;
	int k;
	int screenheight, screenwidth;
	LinearLayout pop_layout_job;
	String Job[][] = {
			{ "销售", "销售代表", "销售助理", "销售支持", "电话销售", "区域销售", "渠道专员", "客户经理",
					"医药代表", "汽车销售", "网络销售", "销售总监", "渠道经理/总监", "大客户经理",
					"团购业务员/经理", "会籍顾问" },
			{ "人事/行政/后勤", "人事经理/主管", "人事专员/助理", "薪酬/绩效/员工关系", "前台/总机/接待", "后勤",
					"文员", "经理助理/秘书", "猎头顾问", "行政专员/助理", "行政经理/主管", "人事总监",
					"行政总监", "培训专员/助理", "培训经理/主管", "招聘专员/助理", "招聘经理/主管" },
			{ "客服", "客服专员/助理", "客服经理/主管", "客服总监", "售前/售后服务", "电话客服", "客户关系经理" },
			{ "餐饮", "服务员", "传菜员", "厨师/厨师长", "面点师", "洗碗工", "后厨", "配菜/打荷",
					"餐饮管理", "大堂经理/领班", "迎宾/接待", "茶艺师", "送餐员", "学徒", "杂工",
					"咖啡师", "预订员" },
			{ "酒店/旅游", "酒店前台", "客服服务员", "楼面经理", "行李员", "救生员", "酒店管理", "订票员",
					"导游", "计调", "签证专员", "旅游顾问" },
			{ "美容/美发", "发型师", "美容师", "化妆师", "美容/瘦身顾问", "洗头工", "美发助理/学徒", "美甲师",
					"美容店长", "美容助理/学徒", "宠物美容", "彩妆培训师", "美容导师", "美体师" },
			{ "保健按摩", "按摩师", "足疗师", "针灸推拿", "搓澡工" },
			{ "运动健身", "健身教练", "瑜伽教练", "舞蹈老师", "游泳教练", "台球教练", "高尔夫球助理" },
			{ "市场/媒介/公关", "品牌专员/经理", "市场专员/助理", "市场拓展", "市场调研", "市场策划",
					"媒介专员/助理", "会务会展会员/经理", "市场经理/总监", "媒介经理/主管", "公关专员/助理",
					"公关经理/主管", "企划经理/主管" },
			{ "建筑", "建筑工程师/总工", "土木/土建工程师", "道路桥梁技术", "给排水/制冷/暖通", "幕墙工程师",
					"园林/景观设计", "测绘/测量", "造价师/预算师", "工程项目管理", "工程监理", "安防工程师",
					"安全管理/安全员", "资料员", "市政工程师", "综合布线/弱电" },
			{ "普工/技工", "木工", "油漆工", "普工", "电工", "钳工", "切割/焊工", "钣金工", "车工/铣工",
					"缝纫工", "锅炉工", "制冷/水暖工", "综合维修工", "铲车/叉车工", "铸造/注塑/模具工",
					"手机维修", "电梯工", "操作工", "包装工", "水泥工", "钢筋工", "管道工", "瓦工",
					"组装工", "样衣工", "染工", "纺织工", "印花工", "压熨工" },
			{ "生产管理/研发", "质量管理", "总工程师/副总工程师", "设备管理维护", "工艺设计", "车间主任",
					"生产主管/组长", "生产计划", "化验/检验", "厂长/副厂长", "生产总监", "维修工程师",
					"工业工程师", "材料工程师", "技术工程师" },
			{ "计算机/互联网/通信", "技术总监/经理", "技术支持/维护", "技术专员/助理", "软件工程师", "程序员",
					"硬件工程师", "质量工程师", "测试员", "系统架构师", "数据库管理/DBA", "游戏设计/开发",
					"网页设计/制作", "语音/视频/图形", "项目经理/主管", "产品经理/专员", "网站运营",
					"网站编辑", "网站策划", "网络管理员", "网络与信息安全工程师", "实施工程师", "通信技术工程师" },
			{ "汽车制造/服务", "汽车美容", "汽车/摩托车修理", "4S店管理", "二手车评估师", "汽车设计工程师",
					"装配工艺工程师", "汽车机械工程师", "汽车电子工程师", "汽车检验/检测", "发动机/总装工程师",
					"安全性能工程师", "理赔专员/顾问", "洗车工", "停车管理员", "加油站工作人员", "轮胎工" },
			{ "超市/百货/零售", "店员/营业员", "收银员", "促销/导购员", "理货员/陈列员", "防损员/内保",
					"店长/卖场经理", "招商经理/主管", "奢侈品业务", "品类管理", "食品加工/处理", "督导" },
			{ "财务/审计/统计", "财务总监", "财务/会计助理", "会计/会计师", "财务分析员", "审计专员/助理",
					"出纳", "税务专员/助理", "统计员", "财务经理/主管", "审计经理/主管", "税务经理/主管",
					"成本管理员" },
			{ "家政保洁/安保", "洗衣工", "保洁", "保姆", "月嫂", "钟点工", "保安", "育婴师/保育员", "护工",
					"送水工" },
			{ "司机", "商务司机", "出租车司机", "班车司机", "货运司机", "特种车司机", "客运司机", "驾校教练/陪练" },
			{ "编辑/出版/印刷", "总编/副总编/主编", "编辑/撰稿", "记者/采编", "出版/发行", "排版设计/制作",
					"印刷操作", "装订/烫金" },
			{ "教育培训", "教师/助教", "教学/教务管理", "幼教/早教", "培训师/讲师", "学术研究/科研", "家教",
					"教育产品研发", "培训策划", "野外拓展训练师" },
			{ "翻译", "英语翻译", "日语翻译", "俄语翻译", "小语种翻译", "韩语翻译", "法语翻译", "德语翻译",
					"西班牙语翻译", "意大利语翻译", "葡萄牙语翻译", "阿拉伯语翻译" },
			{ "娱乐/休闲", "调酒师", "酒吧服务员", "影视/后期制作", "主持人", "摄影师/摄像师", "音效师",
					"礼仪/迎宾", "配音员", "放映员", "灯光师", "娱乐厅服务员" },
			{ "广告/会展/咨询", "广告设计/制作", "广告文案", "广告创意", "创意指导/总监", "会展策划/设计",
					"客户专员/主管", "企业策划", "媒介策划/管理", "婚礼策划", "咨询顾问", "咨询经理/主管" },
			{ "美术/设计/创意", "美编/美术设计", "服装设计", "家具/家居用品设计", "平面设计", "美术指导",
					"店面/陈列/展览设计", "工艺/珠宝设计", "多媒体/动画设计", "产品/包装设计", "装修装潢设计",
					"CAD设计/制图" },
			{ "金融/银行/证券/投资", "证券/期货/外汇经纪人", "投资/理财顾问", "信贷管理/资信评估", "证券经理/总监",
					"外汇/基金/国债经理人", "融资经理/总监", "银行经理/主任", "风险管理/控制", "银行会计/柜员",
					"证券分析/金融研究", "信用卡/银行卡业务", "资产评估", "担保/拍卖/典当", "拍卖师",
					"融资专员", "股票/期货操盘手" },
			{ "保险", "保险经纪人", "保险精算师", "保险客户经理", "保险项目经理", "保险顾问", "车险专员",
					"保险内勤", "保险客服", "保险培训师", "储备经理人", "保险其他职位" },
			{ "贸易/采购", "外贸经理/主管", "外贸专员/助理", "报关员", "商务专员/助理", "采购经理/总监",
					"采购员", "买手", "采购助理" },
			{ "物流/仓储", "物流专员/助理", "物流经理/主管", "物流总监", "调度员", "快递员", "仓库管理员",
					"仓库经理/主管", "装卸/搬运工", "供应链管理", "单证员", "国际货运" },
			{ "法律", "律师/法律顾问", "法务专员/主管", "产权/专利顾问", "合规管理", "律师助理" },
			{ "医院/医疗/护理", "医疗管理", "医生", "美容整形师", "理疗师", "心理医生", "营养师", "药剂师",
					"医药质检", "护士/护理", "护理主任/护士长", "宠物护理/兽医", "验光师", "保健医生", "导医" },
			{ "制药/生物工程", "医药研发/生产/注册", "临床研究/协调", "生物工程/生物制药", "医疗器械研发/维修" },
			{ "电子/电气", "电路工程师/技术员", "电子/电气工程师", "自动化工程师", "无线电工程师",
					"测试/可靠性工程师", "产品工艺/规划工程师", "音频/视频工程师", "灯光/照明设计工程师",
					"研发工程师", "电子/电器维修" },
			{ "机械/仪器仪表", "机电工程师", "机械工程师", "仪器/仪表计量", "版图设计工程师", "研发工程师",
					"测试/可靠性工程师" },
			{ "质控/安防", "质量管理/测试经理", "质量检验员/测试员", "测试工程师", "安全消防", "认证工程师/审核员",
					"安全管理" },
			{ "高级管理", "CEO/总裁/总经理", "首席运营官COO", "首席财务官CFO", "首席技术官CTO",
					"副总裁/副总经理", "总裁助理/总经理助理", "总监", "分公司经理", "合伙人" },
			{ "服装/纺织/食品", "服装设计师", "服装打样/制版", "生产管理", "样衣工", "食品/饮料研发/检验",
					"板房/底格出格师", "电脑放码员", "纸样师/车板工", "纺织品设计师" },
			{ "环保", "污水处理工程师", "环境工程技术", "环境管理/保护", "环保技术", "EHS管理", "环保工程师",
					"环保检测", "水质检测员", "环境绿化" },
			{ "农/林/牧/渔业", "饲料业务", "养殖人员", "农艺师/花艺师", "畜牧师", "场长", "养殖部主管",
					"动物营养/饲料研发" },
			{ "物业管理", "物业维修", "物业管理员", "物业经理/主管", "合同管理", "招商经理/主管" },
			{ "房产中介", "房地产经纪人", "置业顾问", "房产店长/经理", "房产店员/助理", "房产客服", "房产内勤",
					"房地产开发/策划", "房产评估师", "其他房产职位" },
			{ "淘宝职位", "网店店长", "淘宝客服", "淘宝美工", "店铺文案编辑", "店铺推广", "活动策划" },
			{ "其他行业", "其他职位" } };

	public SelectJobPopupWindow(Activity context, OnClickListener itemsOnClick,final int sonId) {
		super(context);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.layout_select_job, null);
		select_trade_text = (TextView) mMenuView
				.findViewById(R.id.select_trade_text);
		select_job_text = (TextView) mMenuView
				.findViewById(R.id.select_job_text);
		select_job_ok_btn = (TextView) mMenuView
				.findViewById(R.id.select_job_ok_btn);
		select_job_cancle_btn = (TextView) mMenuView
				.findViewById(R.id.select_job_cancle_btn);
		pop_layout_job = (LinearLayout) mMenuView
				.findViewById(R.id.pop_layout_job);

		DisplayMetrics metric = new DisplayMetrics();
		this.context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		final int screenheight = metric.heightPixels;
		final int screenwidth = metric.widthPixels;
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		pop_layout_job.measure(w, h);

		Log.e("****screenheight****", "" + screenheight);
		Log.e("****screenwidth****", "" + screenwidth);
		Log.e("****pop_layout_job****", "" + pop_layout_job.getMeasuredHeight());

		/**
		 * 取消
		 */
		select_job_cancle_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});
		/**
		 * 点击 行业
		 */
		select_trade_text.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = (LayoutInflater) SelectJobPopupWindow.this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				mMenuView = inflater.inflate(R.layout.layout_select_job, null);
				sonpopwindow = new SonPopWindow(
						SelectJobPopupWindow.this.context);
				sonpopwindow.showAtLocation(SelectJobPopupWindow.this.context
						.findViewById(sonId), Gravity.NO_GRAVITY, 22,
						170);
				sonpopwindow.trade_list.setAdapter(new ArrayAdapter<String>(
						SelectJobPopupWindow.this.context,
						R.layout.layout_job_item, getdata()));
				sonpopwindow.trade_list
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								k = position;
								sonpopwindow.dismiss();
								select_trade_text.setText(Job[position][0]);
								select_job_text.setText(Job[position][1]);
							}
						});
			}
		});

		/**
		 * 点击 职业
		 */
		select_job_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LayoutInflater inflater = (LayoutInflater) SelectJobPopupWindow.this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				mMenuView = inflater.inflate(R.layout.layout_select_job, null);
				if (TextUtils.isEmpty(select_trade_text.getText())) {
					ToastUtils.makeText(SelectJobPopupWindow.this.context,
							"请选择行业", 0).show();
				} else {
					sonpopwindow = new SonPopWindow(
							SelectJobPopupWindow.this.context);
					sonpopwindow.showAtLocation(
							SelectJobPopupWindow.this.context
									.findViewById(sonId),
							Gravity.NO_GRAVITY, screenwidth / 2 + 6, 170);

					sonpopwindow.trade_list
							.setAdapter(new ArrayAdapter<String>(
									SelectJobPopupWindow.this.context,
									R.layout.layout_job_item, getdata2()));
					sonpopwindow.trade_list
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									sonpopwindow.dismiss();
									select_job_text
											.setText(Job[k][position + 1]);

								}
							});
				}
			}
		});
		select_job_ok_btn.setOnClickListener(itemsOnClick);
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout_job)
						.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

	public List<String> getdata() {

		list = new ArrayList<String>();
		for (int i = 0; i < Job.length; i++) {
			list.add(Job[i][0]);
		}

		return list;

	}

	public List<String> getdata2() {
		list = new ArrayList<String>();
		for (int j = 1; j < Job[k].length; j++) {
			list.add(Job[k][j]);
		}
		return list;

	}

}
