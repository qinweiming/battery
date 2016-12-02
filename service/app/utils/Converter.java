/**
 * 
 */
package utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 值类型转换
 * 
 * @author zxy
 * @since Nov 28, 2013 10:16:17 AM
 */
public class Converter {
	/**
	 * 将对象转换为Double，如果转换失败，则返回0
	 * 
	 * @param object
	 * @return
	 */
	public static Double parseDouble(Object object) {
		return parseDouble(object, 0.0);
	}
	/**
	 * 将对象转换为Double，如果转换失败，则返回fallback指定的值
	 * 
	 * @param object
	 * @param fallback
	 * @return
	 */
	public static Double parseDouble(Object object, Double fallback) {
		if (Utility.isEmpty(object)) {
			return fallback;
		}
		try {
			return Double.valueOf(object.toString());
		} catch (Exception e) {
			return fallback;
		}
	}
	
	/**
	 * 将对象转换为整数，如果转换失败，则返回0
	 * 
	 * @param object
	 * @return
	 */
	public static Integer parseInt(Object object) {
		return parseInt(object, 0);
	}

	/**
	 * 将对象转换为整数，如果转换失败，则返回fallback指定的值
	 * 
	 * @param object
	 * @param fallback
	 * @return
	 */
	public static Integer parseInt(Object object, Integer fallback) {
		if (Utility.isEmpty(object)) {
			return fallback;
		}
		try {
			return Integer.valueOf(object.toString());
		} catch (Exception e) {
			return fallback;
		}
	}
	/**
	 * 将对象转换为long整数，如果转换失败，则返回0
	 *
	 * @param object
	 * @return
	 */
	public static Long parseLong(Object object) {
		return parseLong(object, 0L);
	}

	/**
	 * 将对象转换为long整数，如果转换失败，则返回fallback指定的值
	 *
	 * @param object
	 * @param fallback
	 * @return
	 */
	public static Long parseLong(Object object, long fallback) {
		if (Utility.isEmpty(object)) {
			return fallback;
		}
		try {
			return Long.valueOf(object.toString());
		} catch (Exception e) {
			return fallback;
		}
	}

	/**
	 * 将对象转换为日期，如果转换失败，则返回当前日期
	 * 
	 * @param object
	 * @return
	 */
	public static LocalDate parseLocalDate(Object object) {
		return parseLocalDate(object, LocalDate.now());
	}

	/**
	 * 将对象转换为日期，如果转换失败，则返回fallback指定的值
	 * 
	 * @param object
	 * @param fallback
	 * @return
	 */
	public static LocalDate parseLocalDate(Object object, LocalDate fallback) {
		if (Utility.isEmpty(object)) {
			return fallback;
		}
		try {
			//todo: get date patter from play conf
			DateTimeFormatter simpleDateFormat =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return LocalDate.parse(object.toString(),simpleDateFormat);

		} catch (Exception e) {
			return fallback;
		}
	}
	/**
	 * 将对象转换为日期，如果转换失败，则返回当前日期
	 *
	 * @param object
	 * @return
	 */
	public static Date parseDate(Object object) {
		return parseDate(object, new Date());
	}

	public static Date parseDate(Object object, Date fallback) {
		if (Utility.isEmpty(object)) {
			return fallback;
		}
		try {
			//todo: get date pattern from play conf file or from Constants
			SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd");
			return simpleDateFormat.parse(object.toString());

		} catch (Exception e) {
			return fallback;
		}
	}
}
