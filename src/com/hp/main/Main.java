package com.hp.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import com.happy.common.Constants;
import com.happy.model.KscLyricsLineInfo;
import com.happy.util.DataUtil;
import com.happy.util.FontsUtil;
import com.happy.util.KscLyricsParserUtil;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		DataUtil.init();
		Main main = new Main();
		main.init();
	}

	public void init() {
		int viewWidth = 300;// 图片宽度
		int viewHeight = 70;// 图片
		int paddingHeight = 10;// 歌词间隔
		int fontSize = (viewHeight - paddingHeight * 3) / 2;
		String fileName = "蔡健雅 - Beautiful Love";
		String kscFilePath = Constants.PATH_KSC + File.separator + fileName
				+ ".ksc";
		File kscFile = new File(kscFilePath);
		if (!kscFile.exists()) {
			return;
		} else {
			KscLyricsParserUtil kpu = new KscLyricsParserUtil(kscFilePath);
			TreeMap<Integer, KscLyricsLineInfo> lyricsLineTreeMap = kpu
					.getLyricsLineTreeMap();
			printImage(fileName, viewWidth, viewHeight, paddingHeight,
					fontSize, lyricsLineTreeMap, kpu);
		}
	}

	/**
	 * 生成照片
	 * 
	 * @param fileName
	 *            文件名
	 * @param viewWidth
	 *            宽度
	 * @param viewHeight
	 *            高度
	 * @param paddingHeight
	 *            间隔
	 * @param fontSize
	 *            字体大小
	 * @param lyricsLineTreeMap
	 *            歌词集合
	 * @param kpu
	 */
	private void printImage(String fileName, int width, int height,
			int paddingHeight, int fontSize,
			TreeMap<Integer, KscLyricsLineInfo> lyricsLineTreeMap,
			KscLyricsParserUtil kscLyricsParser) {
		int progress = 0;
		int maxProgress = 0;
		if (lyricsLineTreeMap.size() > 0) {
			KscLyricsLineInfo temp = lyricsLineTreeMap.get(lyricsLineTreeMap
					.size() - 1);
			maxProgress = temp.getEndTime();
		} else {
			return;
		}
		// 统一歌词字体
		String fontFilePath = Constants.PATH_FONTS + File.separator
				+ "方正黑体简体.TTF";
		Font kscFont = FontsUtil.getFontByFile(fontFilePath, Font.BOLD,
				fontSize);
		//

		/**
		 * 显示放大缩小的歌词文字的大小值
		 */
		float SCALEIZEWORDDEF = fontSize;

		/**
		 * 歌词每行的间隔
		 */
		float INTERVAL = paddingHeight;
		/**
		 * 当前歌词的所在行数
		 */
		int lyricsLineNum = -1;

		/**
		 * 当前歌词的第几个字
		 */
		int lyricsWordIndex = -1;

		/**
		 * 当前歌词第几个字 已经播放的时间
		 */
		int lyricsWordHLEDTime = 0;

		/**
		 * 当前歌词第几个字 已经播放的长度
		 */
		float lineLyricsHLWidth = 0;

		/** 高亮歌词当前的其实x轴绘制坐标 **/
		float highLightLrcMoveX;

		while (progress < maxProgress + 100) {

			int newLyricsLineNum = kscLyricsParser
					.getLineNumberFromCurPlayingTime(progress);
			if (newLyricsLineNum != lyricsLineNum) {
				lyricsLineNum = newLyricsLineNum;
				highLightLrcMoveX = 0;
			}
			lyricsWordIndex = kscLyricsParser
					.getDisWordsIndexFromCurPlayingTime(lyricsLineNum, progress);

			lyricsWordHLEDTime = kscLyricsParser.getLenFromCurPlayingTime(
					lyricsLineNum, progress);

			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			// 获取Graphics2D
			Graphics2D g2d = image.createGraphics();
			// ---------- 增加下面的代码使得背景透明 -----------------
			image = g2d.getDeviceConfiguration().createCompatibleImage(width,
					height, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = image.createGraphics();
			// ---------- 背景透明代码结束 -----------------

			// 设置“抗锯齿”的属性
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			//
			// g2d.setFont(new Font("宋体", Font.BOLD, (int) SCALEIZEWORDDEF));
			g2d.setFont(kscFont);

			// 画之前的歌词
			if (lyricsLineNum == -1) {
				String lyricsLeft = lyricsLineTreeMap.get(0).getLineLyrics();
				FontMetrics fm2 = g2d.getFontMetrics();
				int textHeight2 = fm2.getHeight();
				drawBackground(g2d, lyricsLeft, 10, SCALEIZEWORDDEF + INTERVAL);
				GradientPaint paintHLDEF = initPaintHLDEFColor(10,
						SCALEIZEWORDDEF + INTERVAL, textHeight2);
				g2d.setPaint(paintHLDEF);
				g2d.drawString(lyricsLeft, 10, SCALEIZEWORDDEF + INTERVAL);
				if (lyricsLineNum + 2 < lyricsLineTreeMap.size()) {
					String lyricsRight = lyricsLineTreeMap.get(
							lyricsLineNum + 2).getLineLyrics();

					FontMetrics fm = g2d.getFontMetrics();
					Rectangle2D rc = fm.getStringBounds(lyricsRight, g2d);
					int textHeight = fm.getHeight();
					int lyricsRightWidth = (int) rc.getWidth();
					float textRightX = width - lyricsRightWidth - 10;
					// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
					textRightX = Math.max(textRightX, 10);
					drawBackground(g2d, lyricsRight, textRightX,
							(SCALEIZEWORDDEF + INTERVAL) * 2);

					GradientPaint paintHLDEFTemp = initPaintHLDEFColor(
							textRightX, (SCALEIZEWORDDEF + INTERVAL) * 2,
							textHeight);
					g2d.setPaint(paintHLDEFTemp);
					g2d.drawString(lyricsRight, textRightX,
							(SCALEIZEWORDDEF + INTERVAL) * 2);
				}
			} else {

				// 先设置当前歌词，之后再根据索引判断是否放在左边还是右边

				KscLyricsLineInfo kscLyricsLineInfo = lyricsLineTreeMap
						.get(lyricsLineNum);
				// 当行歌词
				String currentLyrics = kscLyricsLineInfo.getLineLyrics();

				FontMetrics fm = g2d.getFontMetrics();
				Rectangle2D rc = fm.getStringBounds(currentLyrics, g2d);

				int currentTextWidth = (int) rc.getWidth();
				int currentTextHeight = fm.getHeight();

				if (lyricsWordIndex != -1) {

					String lyricsWords[] = kscLyricsLineInfo.getLyricsWords();
					int wordsDisInterval[] = kscLyricsLineInfo
							.getWordsDisInterval();
					// 当前歌词之前的歌词
					String lyricsBeforeWord = "";
					for (int i = 0; i < lyricsWordIndex; i++) {
						lyricsBeforeWord += lyricsWords[i];
					}
					// 当前歌词
					String lyricsNowWord = lyricsWords[lyricsWordIndex].trim();// 去掉空格

					Rectangle2D rc2 = fm.getStringBounds(lyricsBeforeWord, g2d);
					// 当前歌词之前的歌词长度
					int lyricsBeforeWordWidth = (int) rc2.getWidth();

					Rectangle2D rc3 = fm.getStringBounds(lyricsNowWord, g2d);
					// 当前歌词长度
					float lyricsNowWordWidth = (int) rc3.getWidth();

					float len = lyricsNowWordWidth
							/ wordsDisInterval[lyricsWordIndex]
							* lyricsWordHLEDTime;
					lineLyricsHLWidth = lyricsBeforeWordWidth + len;
				} else {
					// 整行歌词
					lineLyricsHLWidth = currentTextWidth;
				}
				// 当前歌词行的x坐标
				float textX = 0;

				// 当前歌词行的y坐标
				float textY = 0;
				if (lyricsLineNum % 2 == 0) {

					if (currentTextWidth > width) {
						if (lineLyricsHLWidth >= width / 2) {
							if ((currentTextWidth - lineLyricsHLWidth) >= width / 2) {
								highLightLrcMoveX = (width / 2 - lineLyricsHLWidth);
							} else {
								highLightLrcMoveX = width - currentTextWidth
										- 10;
							}
						} else {
							highLightLrcMoveX = 10;
						}
						// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
						textX = highLightLrcMoveX;
					} else {
						// 如果歌词宽度小于view的宽
						textX = 10;
					}

					// 画下一句的歌词
					if (lyricsLineNum + 1 < lyricsLineTreeMap.size()) {
						String lyricsRight = lyricsLineTreeMap.get(
								lyricsLineNum + 1).getLineLyrics();

						Rectangle2D rc4 = fm.getStringBounds(lyricsRight, g2d);

						int lyricsRightWidth = (int) rc4.getWidth();
						float textRightX = width - lyricsRightWidth - 10;
						// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
						textRightX = Math.max(textRightX, 10);
						drawBackground(g2d, lyricsRight, textRightX,
								(SCALEIZEWORDDEF + INTERVAL) * 2);

						int textHeight = fm.getHeight();
						GradientPaint paintHLDEF = initPaintHLDEFColor(
								textRightX, (SCALEIZEWORDDEF + INTERVAL) * 2,
								textHeight);
						g2d.setPaint(paintHLDEF);
						g2d.drawString(lyricsRight, textRightX,
								(SCALEIZEWORDDEF + INTERVAL) * 2);
					}

					textY = (SCALEIZEWORDDEF + INTERVAL);

				} else {

					if (currentTextWidth > width) {
						if (lineLyricsHLWidth >= width / 2) {
							if ((currentTextWidth - lineLyricsHLWidth) >= width / 2) {
								highLightLrcMoveX = (width / 2 - lineLyricsHLWidth);
							} else {
								highLightLrcMoveX = width - currentTextWidth
										- 10;
							}
						} else {
							highLightLrcMoveX = 10;
						}
						// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
						textX = highLightLrcMoveX;
					} else {
						// 如果歌词宽度小于view的宽
						textX = width - currentTextWidth - 10;
					}

					// 画下一句的歌词
					if (lyricsLineNum + 1 != lyricsLineTreeMap.size()) {
						String lyricsLeft = lyricsLineTreeMap.get(
								lyricsLineNum + 1).getLineLyrics();

						drawBackground(g2d, lyricsLeft, 10, SCALEIZEWORDDEF
								+ INTERVAL);

						int textHeight = fm.getHeight();
						GradientPaint paintHLDEF = initPaintHLDEFColor(10,
								(SCALEIZEWORDDEF + INTERVAL), textHeight);
						g2d.setPaint(paintHLDEF);
						g2d.drawString(lyricsLeft, 10, SCALEIZEWORDDEF
								+ INTERVAL);
					}

					textY = (SCALEIZEWORDDEF + INTERVAL) * 2;

				}

				// System.out.println(width);

				drawBackground(g2d, currentLyrics, textX, textY);

				int textHeight = fm.getHeight();
				GradientPaint paintHLDEF = initPaintHLDEFColor(textX, textY,
						textHeight);

				g2d.setPaint(paintHLDEF);
				// 画当前歌词
				g2d.drawString(currentLyrics, textX, textY);

				// 这里不知为何还要减去fm.getDescent() + fm.getLeading() 绘画时才能把全文字绘画完整
				int clipY = (int) (textY - currentTextHeight + (fm.getDescent() + fm
						.getLeading()));

				g2d.setClip((int) textX, clipY, (int) lineLyricsHLWidth,
						currentTextHeight);

				GradientPaint paintHLED = initPaintHLEDColor(textX, textY,
						textHeight);
				g2d.setPaint(paintHLED);
				g2d.drawString(currentLyrics, textX, textY);
			}
			String kscFileImagePath = Constants.PATH_KSCIMAGE + File.separator
					+ fileName + File.separator;
			File kscFileImageFile = new File(kscFileImagePath);
			if (!kscFileImageFile.exists()) {
				kscFileImageFile.mkdirs();
			}
			try {
				ImageIO.write(image, "png", new java.io.File(kscFileImagePath
						+ progress + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			progress += 100;
		}
	}

	/**
	 * 初始化高亮字体的渐变颜色
	 * 
	 */
	private GradientPaint initPaintHLEDColor(float x, float y, int height) {

		GradientPaint paintHLED = new GradientPaint(x, y - height / 2,
				Constants.DESLRCREADEDCOLORFRIST[Constants.desktopLrcIndex], x,
				y + height,
				Constants.DESLRCREADEDCOLORSECOND[Constants.desktopLrcIndex],
				true);
		return paintHLED;
	}

	/**
	 * 初始化默认字体的渐变颜色
	 * 
	 */
	private GradientPaint initPaintHLDEFColor(float x, float y, int height) {
		GradientPaint paintHLDEF = new GradientPaint(x, y - height / 2,
				Constants.DESLRCNOREADCOLORFRIST[Constants.desktopLrcIndex], x,
				y + height,
				Constants.DESLRCNOREADCOLORSECOND[Constants.desktopLrcIndex],
				true);
		return paintHLDEF;
	}

	/**
	 * 描绘轮廓
	 * 
	 * @param canvas
	 * @param string
	 * @param x
	 * @param y
	 */
	private void drawBackground(Graphics2D g2d, String string, float x, float y) {
		g2d.setColor(new Color(0, 0, 0, 200));
		g2d.drawString(string, x - 1, y);
		g2d.drawString(string, x + 1, y);
		g2d.drawString(string, x, y + 1);
		g2d.drawString(string, x, y - 1);

	}

}
