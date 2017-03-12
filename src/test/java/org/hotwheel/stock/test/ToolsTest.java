package org.hotwheel.stock.test;

import org.hotwheel.stock.util.StockApi;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author yilihjy Email:yilihjy@gmail.com
 * @version 1.0.0
 *
 */
public class ToolsTest {

	@Test
	public void testString2LocalDateTimeMethod() {
		String time1 = "2017-01-05";
		String time2 = "2017-01-05 15:00:00";
		Date ltime = new Date(2017, 1, 5, 15, 0);
		assertEquals(ltime, StockApi.string2LocalDateTime(time1));
		assertEquals(ltime, StockApi.string2LocalDateTime(time2));
	}

}
