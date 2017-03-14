package org.hotwheel.stock.test;

import org.hotwheel.stock.StockOptions;
import org.hotwheel.stock.data.HistoryUtils;
import org.hotwheel.stock.model.StockHistory;
import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * 测试HistoryData类
 * @author yilihjy Email:yilihjy@gmail.com
 * @version 1.0.0
 *
 */
public class StockHistoryUtilsTest {
	
	@Test
	public void testGetKLineDataMethod() {
		String pattern = "\\[(\\{day:\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\""
				+ ",open:\"\\d+\\.?\\d{0,3}\","
				+ "high:\"\\d+\\.?\\d{0,3}\""
				+ ",low:\"\\d+\\.?\\d{0,3}\""
				+ ",close:\"\\d+\\.?\\d{0,3}\""
				+ ",volume:\"\\d+\""
				+ ",ma_price5:\\d+\\.?\\d{0,3},"
				+ "ma_volume5:\\d+,"
				+ "ma_price10:\\d+\\.?\\d{0,3},"
				+ "ma_volume10:\\d+,"
				+ "ma_price30:\\d+\\.?\\d{0,3},"
				+ "ma_volume30:\\d+\\},{0,1}){50}\\]";
		Pattern r = Pattern.compile(pattern);
		String result = HistoryUtils.getKLineData("sz000002", "5","50");
		Matcher m = r.matcher(result);
		assertTrue(m.find());
	}
	
	@Test
	public void testGetKLineDataObjectsMethod(){
		List<StockHistory> result = HistoryUtils.getKLineDataObjects("sz000002", StockOptions.ONE_DAY);
		StockHistory h1 = result.get(0);
		StockHistory h5 = result.get(4);
		StockHistory h10 = result.get(9);
		StockHistory h30 = result.get(29);
		assertNotNull(h1.getDay());
		assertEquals(1991,h1.getDay().getYear());
		assertEquals(0.0,h1.getMA5(),0.0);
		assertNotNull(h5.getMA5());
		assertEquals(0.0,h5.getMA10(),0.0);
		assertNotNull(h10.getMA10());
		assertEquals(0.0,h10.getMA30(),0.0);
		assertNotNull(h30.getMA30());
		assertNotNull(h30.getMA30Volume());
	}

}
