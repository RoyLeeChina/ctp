package org.hotwheel.ctp.util;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类
 * 
 * @author wangfeng
 * @date 2016年3月4日 上午6:02:12
 */
public class DateUtils {

	
	public final static Date getDateZero() {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		//gc.set
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		return gc.getTime();
	}

	/**
	 * 今日凌晨0点0分0秒0毫秒
	 * @return
	 */
	public final static Date midnight() {
		return getDateZero();
	}

	/**
	 * 获得当日0点0分0秒0毫秒
	 * @param date
	 * @return
	 */
	public final static Date getZero(final Date date) {
		Date dt = date;
		if (Api.isEmpty(date)) {
			dt  = new Date(0);
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(dt);
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		return gc.getTime();
	}

	/**
	 * 获得当天剩余秒数
	 * @return
	 */
	public static int getRemainingTime() {
		Date now = new Date();
		Date today = DateUtils.getDateZero();
		long tmpTime = (now.getTime() - today.getTime()) / 1000L;
		long iRet = StockOptions.SecondOfDay - tmpTime;
		return (int) iRet;
	}
}
