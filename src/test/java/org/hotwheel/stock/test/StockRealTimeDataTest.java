package org.hotwheel.stock.test;

import org.hotwheel.stock.data.RealTimeData;
import org.hotwheel.stock.model.StockRealTime;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 
 * @author yilihjy Email:yilihjy@gmail.com
 * @version 1.0.0
 *
 */
public class StockRealTimeDataTest {

	@Test
	public void testGetRealTimeDataObjectsMethod() throws UnsupportedEncodingException {
		String[] li ={"s_sh000001","s_sz399001","sz000002","sh603377","sz300443"};
		List<StockRealTime> list = RealTimeData.getRealTimeDataObjects(li);
		assertEquals("上证指数",list.get(0).getName());
		assertEquals(0.0,list.get(0).getOpen(),0.0);
		assertEquals(StockRealTime.INDEX,list.get(0).getType());
		assertEquals("深证成指",list.get(1).getName());
		assertEquals("万 科Ａ",list.get(2).getName());
		assertNotEquals(StockRealTime.INDEX,list.get(2).getType());
		assertEquals(StockRealTime.STOCK,list.get(2).getType());
		assertEquals("东方时尚",list.get(3).getName());
		assertEquals("金雷风电",list.get(4).getName());
	}

}
