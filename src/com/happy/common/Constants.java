package com.happy.common;

import java.awt.Color;
import java.io.File;

public class Constants {
	/**
	 * 临时目录
	 */
	public final static String PATH_TEMP = "haplayer";

	/**
	 * ksc歌词图片
	 */
	public final static String PATH_KSCIMAGE = PATH_TEMP + File.separator
			+ "kscImage";
	/**
	 * 字体
	 */
	public final static String PATH_FONTS = PATH_TEMP + File.separator
			+ "fonts";
	/**
	 * 歌词目录
	 */
	public final static String PATH_KSC = PATH_TEMP + File.separator + "ksc";

	/**
	 * ==================================
	 * <p>
	 * 基本数据
	 * </p>
	 * ==================================
	 */
	/**
	 * 未读歌词颜色
	 */
	public static Color DESLRCNOREADCOLORFRIST[] = { new Color(0, 52, 138),
			new Color(255, 255, 255), new Color(255, 172, 0),
			new Color(225, 225, 225), new Color(64, 0, 128) };

	public static Color DESLRCNOREADCOLORSECOND[] = { new Color(3, 202, 252),
			new Color(76, 166, 244), new Color(170, 0, 0), new Color(0, 0, 0),
			new Color(255, 128, 255) };
	/**
	 * 已读歌词颜色
	 */
	public static Color DESLRCREADEDCOLORFRIST[] = { new Color(130, 247, 253),
			new Color(255, 100, 26), new Color(255, 255, 0),
			new Color(0, 255, 255), new Color(255, 243, 146) };

	public static Color DESLRCREADEDCOLORSECOND[] = { new Color(255, 255, 255),
			new Color(255, 255, 255), new Color(255, 100, 26),
			new Color(255, 255, 255), new Color(255, 243, 134) };

	/***
	 * 桌面歌词颜色
	 */
	public static String desktopLrcIndex_KEY = "DEF_DES_COLOR_INDEX_KEY";
	public static int desktopLrcIndex = 0;
}
