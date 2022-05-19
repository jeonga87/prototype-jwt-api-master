package kr.co.demo.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Function {
  public static List<BigDecimal> getBigDecimalValue(List<Map<String, Object>> dataList, List<String> labels) {
		List<BigDecimal> dataValue = new ArrayList<>();
		for ( Map data : dataList ) {
			int i = 0;
			for ( String label : labels ) {
				if ( data == null ) {
					dataValue.add( i, null );
				} else {
					dataValue.add( i, (BigDecimal) data.get(label) );
				}
				i++;
			}
		}
		return dataValue;
	}

	public static String getRandomStr(int length) {
  	StringBuffer result = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < length; i++) {
				int rIndex = rnd.nextInt(3);
				switch (rIndex) {
					case 0:
						// a-z
						result.append((char) ((int) (rnd.nextInt(26)) + 97));
						break;
					case 1:
						// A-Z
						result.append((char) ((int) (rnd.nextInt(26)) + 65));
						break;
					case 2:
						// 0-9
						result.append((rnd.nextInt(10)));
						break;
				}
		}
		return result.toString();
	}
}
