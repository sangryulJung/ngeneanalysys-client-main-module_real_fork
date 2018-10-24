package ngeneanalysys.model.render;

import ngeneanalysys.util.LoggerUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polyline;

/**
 * SNPs-INDELs > Overview > clincal SIGNIFICANT 레이더 그래프 화면 출력
 * 
 * @author gjyoo
 * @since 2016. 6. 11. 오후 4:05:52
 */
public class SNPsINDELsOverviewRadarGraph {
	private static Logger logger = LoggerUtil.getLogger();
	
	/** 그래프 객체 */
	private Polyline polyline;
	/** 그래프 배경 박스 */
	private VBox box;
	/** 애니메이션 타이머 */
	private AnimationTimer timer;

	/** 그래프 박스 가로 사이즈 */
	private double minX = 100;
	/** 그래프 박스 세로 사이즈 */
	private double minY = 116;
	
	/** 최상단 퍼센트값 */
	private double percent0;
	/** 우측 상단 퍼센트값 */
	private double percent1;
	/** 우측 하단 퍼센트값 */
	private double percent2;
	/** 최하단 퍼센트값 */
	private double percent3;
	/** 좌측 하단 퍼센트값 */
	private double percent4;
	/** 좌측 상단 퍼센트값 */
	private double percent5;
	/** 애니메이션 재생 스피드 **/
	private final int gaugeSpeed = 10;
	public SNPsINDELsOverviewRadarGraph(VBox box, Polyline polyline, double percent0, double percent1, double percent2, double percent3, double percent4, double percent5) {
		logger.debug(String.format("percent0 : %s, percent1 : %s, percent2 : %s, percent3 : %s, percent4 : %s, percent5 : %s", percent0, percent1, percent2, percent3, percent4, percent5));
		this.box = box;
		this.polyline = polyline;
		this.percent0 = percent0;
		this.percent1 = percent1;
		this.percent2 = percent2;
		this.percent3 = percent3;
		this.percent4 = percent4;
		this.percent5 = percent5;
		this.timer = new PolygonAnimationTimer();
	}

	/**
	 * Polygon 박스의 Margin값 설정 객체 반환
	 * @param polyline
	 * @return Polygon 박스자체에 Margin 설정이 되지 않아 부모 객체의 Padding값을 변경하여 처리함.
	 */
	public Insets getPolylineMargin(Polyline polyline) {
		if(polyline != null && polyline.getPoints().size() > 0) {
			int idx = 0;
			for (Double point : polyline.getPoints()) {
				if(idx % 2 == 0) {
					if(point.intValue() < minX) minX = point.intValue();
				} else {
					if(point.intValue() < minY) minY = point.intValue();
				}
				idx++;
			}
			return new Insets(minY, 0, 0, minX);
		}
		return null;
	}
	
	/**
	 * 최상단 꼭지점
	 * @param percent
	 * @return
	 */
	public Double[] getPoint0(double percent) {
		// [100% = x : 50, y : 0], [0% = x : 50, y : 58]
		double boxHeight = 58;
		double pointY = Math.round(boxHeight - (boxHeight*(percent/100)));
		return new Double[]{50d, pointY};
	}
	
	/**
	 * 우측 상단 꼭지점
	 * @param percent
	 * @return
	 */
	public Double[] getPoint1(double percent) {
		// [100% = x : 100, y : 29], [0% = x : 50, y : 58]
		double addedY = 29;
		double boxWidth = 50;
		double boxHeight = 29;
		double pointX = Math.round(boxWidth + (boxWidth*(percent/100)));
		double pointY = Math.round(boxHeight - (boxHeight*(percent/100))) + addedY;
		return new Double[]{pointX, pointY};
	}
	
	/**
	 * 우측 하단 꼭지점
	 * @param percent
	 * @return
	 */
	public Double[] getPoint2(double percent) {
		// [100% = x : 100, y : 86], [0% = x : 50, y : 58]
		double addedY = 58;
		double boxWidth = 50;
		double boxHeight = 29;
		double pointX = Math.round(boxWidth + (boxWidth*(percent/100)));
		double pointY = Math.round(boxHeight*(percent/100)) + addedY;
		return new Double[]{pointX, pointY};
	}
	
	/**
	 * 최하단 꼭지점
	 * @param percent
	 * @return
	 */
	public Double[] getPoint3(double percent) {
		// [100% = x : 50, y : 116], [0% = x : 50, y : 58]
		double boxHeight = 58;
		double pointY = Math.round(boxHeight + (boxHeight*(percent/100)));
		return new Double[]{50d, pointY};
	}
	
	/**
	 * 좌측 하단 꼭지점
	 * @param percent
	 * @return
	 */
	public Double[] getPoint4(double percent) {
		// [100% = x : 0, y : 86], [0% = x : 50, y : 58]
		double addedY = 58;
		double boxWidth = 50;
		double boxHeight = 29;
		double pointX = Math.round(boxWidth - (boxWidth*(percent/100)));
		double pointY = Math.round(boxHeight*(percent/100)) + addedY;
		return new Double[]{pointX, pointY};
	}
	
	/**
	 * 좌측 상단 꼭지점
	 * @param percent
	 * @return
	 */
	public Double[] getPoint5(double percent) {
		// [100% = x : 0, y : 29], [0% = x : 50, y : 58]
		double addedY = 29;
		double boxWidth = 50;
		double boxHeight = 29;
		double pointX = Math.round(boxWidth - (boxWidth*(percent/100)));
		double pointY = Math.round(boxHeight - (boxHeight*(percent/100))) + addedY;
		return new Double[]{pointX, pointY};
	}
	
	/**
	 * 
	 * 
	 * @author gjyoo
	 * @since 2016. 6. 11. 오후 4:10:29
	 */
	public class PolygonAnimationTimer extends AnimationTimer {
		int idx0 = 0, idx1 = 0, idx2 = 0, idx3 = 0, idx4 = 0, idx5 = 0;
		
		@Override
		public void handle(long now) {
			Double[] points0 = getPoint0(idx0);
			Double[] points1 = getPoint1(idx1);
			Double[] points2 = getPoint2(idx2);
			Double[] points3 = getPoint3(idx3);
			Double[] points4 = getPoint4(idx4);
			Double[] points5 = getPoint5(idx5);
			
			Double[] points = points0;
			points = ArrayUtils.add(points, points1[0]);
			points = ArrayUtils.add(points, points1[1]);
			points = ArrayUtils.add(points, points2[0]);
			points = ArrayUtils.add(points, points2[1]);
			points = ArrayUtils.add(points, points3[0]);
			points = ArrayUtils.add(points, points3[1]);
			points = ArrayUtils.add(points, points4[0]);
			points = ArrayUtils.add(points, points4[1]);
			points = ArrayUtils.add(points, points5[0]);
			points = ArrayUtils.add(points, points5[1]);
			points = ArrayUtils.add(points, points0[0]);
			points = ArrayUtils.add(points, points0[1]);
			
			polyline.getPoints().setAll(points);
			box.setPadding(getPolylineMargin(polyline));
			
			if(idx0 < percent0) idx0 += gaugeSpeed;
			if(idx1 < percent1) idx1 += gaugeSpeed;
			if(idx2 < percent2) idx2 += gaugeSpeed;
			if(idx3 < percent3) idx3 += gaugeSpeed;
			if(idx4 < percent4) idx4 += gaugeSpeed;
			if(idx5 < percent5) idx5 += gaugeSpeed;
			
			if(idx0 > percent0) {
				idx0 = (int)percent0;
			}
			if(idx1 > percent1) {
				idx1 = (int)percent1;
			}
			if(idx2 > percent2) {
				idx2 = (int)percent2;
			}
			if(idx3 > percent3) {
				idx3 = (int)percent3;
			}
			if(idx4 > percent4) {
				idx4 = (int)percent4;
			}
			if(idx5 > percent5) {
				idx5 = (int)percent5;
			}
			
			if((idx0 == percent0) && (idx1 == percent1) && (idx2 == percent2) && (idx3 == percent3) && (idx4 == percent4) && (idx5 == percent5)) {
				stop();
			}
		}
	}
	
	/**
	 * 화면 출력
	 */
	public void display() {
		timer.start();
	}
}
