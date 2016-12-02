/**
 * 
 */
package utils;

import play.Logger;
import play.libs.IO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import static java.net.URLEncoder.encode;

/**
 * 
 * @author zxy
 * @since Oct 4, 2013 10:40:33 AM
 */
public final class Utility {
	private final static byte commonCsvHead[] = { (byte) 0xEF, (byte) 0xBB,
			(byte) 0xBF };

	/**
	 * 判断value是否为null、空白字符串""、trim后为空白字符串
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(Object value) {
		if (value == null)
			return true;
		boolean isEmpty = true;
		String string = value.toString();
		if (string != null && !"".equals(string) && string.trim().length() > 0)
			isEmpty = false;
		return isEmpty;
	}

	/**
	 * 判断value是否不为null、空白字符串""、trim后为空白字符串
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotEmpty(Object value) {
		return !isEmpty(value);
	}

	public static <T> boolean isNotEmptyList(List<T> value) {
		return value != null && value.size() > 0;
	}

	/**
	 * 读取固定路径固定文件
	 * 
	 * @return
	 * @throws IOException
	 * @throws IOException
	 */
	public static Map<String, String> getPropertiesVal(InputStream is) {

		Map<String, String> map = new HashMap<>();
		try {
			InputStream input = new BufferedInputStream(is);
			Properties props = IO.readUtf8Properties(input);
			Enumeration en = props.propertyNames();
			// 在这里遍历
			while (en.hasMoreElements()) {
				String key = en.nextElement().toString();// key值
				map.put(key, props.getProperty(key));
			}
			input.close();
		} catch (Exception e) {
			Logger.error(e, "getPropertiesVal error");
		}
		return map;
	}

	/**
	 * 经验值转成等级<br>
	 * 等级从0级到12级，分别对应的图标为无，1个星星，2个星星，3个星星，1个月亮，2个月亮直到3个皇冠 <br>
	 * 一个星星：10分，一个月亮：40分，一个太阳：160分，一个皇冠：640分<br>
	 * 例如：11分==1级，只显示一个星星，20分==2级，显示两个星星
	 * 
	 * @param score
	 * @return 级别数0-12
	 */
	public static int score2level(int score) {
		int[] levelScoreArray = new int[] { 10, 20, 30, 40, 80, 120, 160, 320,
				480, 640, 1280, 1920 };
		int level = Arrays.binarySearch(levelScoreArray, score);
		level = level <= 0 ? Math.abs(level + 1) : level + 1;
		return level;
	}


	/**
	 * 获取当前日期之后months月后的日期
	 * 
	 * @param months
	 * @return
	 */
	public static Date after(int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();

	}

	/**
	 * 获取指定月的月初时间
	 * 
	 * @return
	 */
	public static Date monthStart(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	/**
	 * 获取指定月的月末时间
	 * 
	 * @return
	 */
	public static Date monthEnd(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 缩放图像
	 * 
	 * @param file
	 *            源图像文件
	 * @param widths
	 *            图片宽度
	 * @param heights
	 *            图片高度
	 * @return
	 * @throws IOException
	 */
	public static File changeImageSize(File file, int widths, int heights,
									   String type) throws IOException {
		BufferedImage src = ImageIO.read(file); // 读入文件
		Image image = src.getScaledInstance(widths, heights,
				Image.SCALE_DEFAULT);
		BufferedImage tag = new BufferedImage(widths, heights,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = tag.getGraphics();
		g.drawImage(image, 0, 0, null); // 绘制缩小后的图
		g.dispose();
		ImageIO.write(tag, type, file);// 输出到文件流
		return file;
	}


	/**
	 * 截取URL，用省略号代替中间的部分
	 * 
	 * @param src
	 * @param length
	 * @return
	 */
	public static String truncWithDots(String src, int length) {
		String dots = "...";
		if (src != null && src.length() > length)
			return src.substring(0, length - dots.length()) + dots
					+ src.substring(src.length() - dots.length(), src.length());
		else
			return src;
	}

	/**
	 * 正规化 URL
	 * 
	 * @param src
	 * @return
	 */
	public static String urlNormalized(String src) {
		if (isNotEmpty(src)) {
			if (src.toLowerCase().startsWith("http://")
					|| src.toLowerCase().startsWith("https://"))
				return src;
			else
				return "http://" + src;
		} else
			return "";
	}

	/**
	 * Converts a map to URL- encoded content. This is a convenience method
	 * which can be used in combination . It makes it easy to convert
	 * parameters to submit a string:
	 * 
	 * <code>
	 *     key=value&key1=value1
	 * </code>
	 * 
	 * 
	 * 
	 * @param params
	 *            map with keys and values to be posted. This map is used to
	 *            build content to be posted, such that keys are names of
	 *            parameters, and values are values of those posted parameters.
	 *            This method will also URL-encode keys and content using UTF-8
	 *            encoding.
	 *            <p>
	 *            String representations of both keys and values are used.
	 *            </p>
	 * @return   object.
	 */
	public static String map2QueryString(Map<?, ?> params) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			Set<?> keySet = params.keySet();
			Object[] keys = keySet.toArray();

			for (int i = 0; i < keys.length; i++) {
				stringBuilder
						.append(encode(keys[i].toString(), "UTF-8"))
						.append('=')
						.append(encode(params.get(keys[i]).toString(), "UTF-8"));
				if (i < (keys.length - 1)) {
					stringBuilder.append('&');
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("failed to generate content from map", e);
		}
		return stringBuilder.toString();
	}


}
