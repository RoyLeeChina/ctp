package org.hotwheel.ctp.util;

import org.hotwheel.assembly.Api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证集合工具类
 * 
 * @author wangfeng
 * @date 2016年2月25日 下午11:11:30
 */
public final class Validate {
	/** 电话格式验证 **/
	private static final String PHONE_CALL_PATTERN = "^(\\(\\d{3,4}\\)|\\d{3,4}-)?\\d{7,8}(-\\d{1,4})?$";

	/**
	 * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700
	 **/
	private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|8[019])\\d{8}$)|(^1700\\d{7}$)";

	/**
	 * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1709
	 **/
	private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)";

	/**
	 * 中国移动号码格式验证
	 * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184
	 * ,187,188,147,178,1705
	 **/
	private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";

	/**
	 * 验证电话号码的格式
	 * 
	 * @author LinBilin
	 * @param str
	 *            校验电话字符串
	 * @return 返回true,否则为false
	 */
	public static boolean isPhoneCallNum(String str) {

		return str == null || str.trim().equals("") ? false : match(PHONE_CALL_PATTERN, str);
	}

	/**
	 * 验证【电信】手机号码的格式
	 * 
	 * @author LinBilin
	 * @param str
	 *            校验手机字符串
	 * @return 返回true,否则为false
	 */
	public static boolean isChinaTelecomPhoneNum(String str) {

		return str == null || str.trim().equals("") ? false : match(CHINA_TELECOM_PATTERN, str);
	}

	/**
	 * 验证【联通】手机号码的格式
	 * 
	 * @author LinBilin
	 * @param str
	 *            校验手机字符串
	 * @return 返回true,否则为false
	 */
	public static boolean isChinaUnicomPhoneNum(String str) {

		return str == null || str.trim().equals("") ? false : match(CHINA_UNICOM_PATTERN, str);
	}

	/**
	 * 验证【移动】手机号码的格式
	 * 
	 * @author LinBilin
	 * @param str
	 *            校验手机字符串
	 * @return 返回true,否则为false
	 */
	public static boolean isChinaMobilePhoneNum(String str) {

		return str == null || str.trim().equals("") ? false : match(CHINA_MOBILE_PATTERN, str);
	}

	/**
	 * 验证手机和电话号码的格式
	 * 
	 * @author LinBilin
	 * @param str
	 *            校验手机字符串
	 * @return 返回true,否则为false
	 */
	public static boolean isPhoneNum(String str) {
		// 如果字符串为空，直接返回false
		if (str == null || str.trim().equals("")) {
			return false;
		} else {
			int comma = str.indexOf(",");// 是否含有逗号
			int caesuraSign = str.indexOf("、");// 是否含有顿号
			int space = str.trim().indexOf(" ");// 是否含有空格
			if (comma == -1 && caesuraSign == -1 && space == -1) {
				// 如果号码不含分隔符,直接验证
				str = str.trim();
				return (/*isPhoneCallNum(str) ||*/ isChinaTelecomPhoneNum(str) || isChinaUnicomPhoneNum(str)
						|| isChinaMobilePhoneNum(str)) ? true : false;
			} else {
				// 号码含分隔符,先把分隔符统一处理为英文状态下的逗号
				if (caesuraSign != -1) {
					str = str.replaceAll("、", ",");
				}
				if (space != -1) {
					str = str.replaceAll(" ", ",");
				}

				String[] phoneNumArr = str.split(",");
				// 遍历验证
				for (String temp : phoneNumArr) {
					temp = temp.trim();
					if (isPhoneCallNum(temp) || isChinaTelecomPhoneNum(temp) || isChinaUnicomPhoneNum(temp)
							|| isChinaMobilePhoneNum(temp)) {
						continue;
					} else {
						return false;
					}
				}
				return true;
			}

		}

	}

	/**
	 * 执行正则表达式
	 * 
	 * @param pat
	 *            表达式
	 * @param str
	 *            待验证字符串
	 * @return 返回true,否则为false
	 */
	private static boolean match(String pat, String str) {
		Pattern pattern = Pattern.compile(pat);
		Matcher match = pattern.matcher(str);
		return match.find();
	}

	/**
	 * 功能：判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 功能：设置地区编码
	 * 
	 * @return Hashtable 对象
	 */
	private static Hashtable<String, String> GetAreaCode() {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	/**
	 * 验证日期字符串是否是YYYY-MM-DD格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDataFormat(String str) {
		boolean flag = false;
		// String
		// regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
		String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern pattern1 = Pattern.compile(regxStr);
		Matcher isNo = pattern1.matcher(str);
		if (isNo.matches()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 功能：身份证的有效验证
	 * 
	 * @param id
	 *            身份证号
	 * @return 有效：返回"" 无效：返回String信息
	 * @throws ParseException
	 */
	public static String isIdCard(String id) {
		String errorInfo = "";// 记录错误信息
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (id.length() != 15 && id.length() != 18) {
			errorInfo = "身份证号码长度应该为15位或18位";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 数字 除最后以为都为数字 ================
		if (id.length() == 18) {
			Ai = id.substring(0, 17);
		} else if (id.length() == 15) {
			Ai = id.substring(0, 6) + "19" + id.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "身份证生日无效";
			return errorInfo;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
					|| (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				errorInfo = "身份证生日不在有效范围";
				return errorInfo;
			}
		} catch (NumberFormatException | ParseException e) {
			return "身份证生日非有效日期";
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "身份证月份无效";
			return errorInfo;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "身份证日期无效";
			return errorInfo;
		}
		// =====================(end)=====================

		// ================ 地区码时候有效 ================
		Hashtable<?, ?> h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "身份证地区编码错误";
			return errorInfo;
		}
		// ==============================================

		// ================ 判断最后一位的值 ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (id.length() == 18) {
			if (Ai.equalsIgnoreCase(id) == false) {
				errorInfo = "身份证无效，不是合法的身份证号码";
				return errorInfo;
			}
		} else {
			return "";
		}
		// =====================(end)=====================
		return "";
	}

	public static void main(String args[]) {
		long a = 111;
		System.out.println(a/10);
		String id = "412727199107028011";
		System.out.println(isIdCard(id));
		System.out.println(isPhoneNum("17750581369"));
		System.out.println(isPhoneNum("13306061248"));
		System.out.println(isPhoneNum("17750581369,13306061248"));
		System.out.println(isPhoneNum("17750581369 13306061248"));
		System.out.println(isPhoneNum("17750581369、13306061248"));
		System.out.println(isPhoneNum("+8618612033288"));
		System.out.println(isPhoneNum("15901099"));
		long numberOfIP = 10000000000000l;
		System.out.println(String.format("当日IP查询超过%d次", numberOfIP));
		
		System.out.println(Api.md5("5gohh"));
	}
}
