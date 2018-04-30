package ngeneanalysys.animaition;

import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public final class ClinicalSignificantTimer extends AnimationTimer {
	private final double outerLineWidth = 2.0;
	private double outerCircleSize;
	private final double gaugeStartAngle = 90.0;
	private final double innerOuterCircleGap = 2.0;
	private double innerCircleSize;
	private final double defaultMarginX = outerLineWidth + 5.0;
	private final double defaultMarginY = outerLineWidth + 5.0;
	private double value;
	private double angle;
	private double angleStep;
	private double currAngle = 0.0;
	private double gaugeSpeed;
	private String labelText1, labelText2;
	private GraphicsContext gc = null;
	private Canvas canvas;

	public ClinicalSignificantTimer(GraphicsContext gc, double value, String labelText1, String labelText2, double gaugeSpeed) {
		this.gc = gc;
		this.canvas = this.gc.getCanvas();
		this.outerCircleSize = canvas.getWidth() - this.defaultMarginX - 10;
		this.value = value;			
		this.angle = 360.0 * value;
		this.gaugeSpeed = gaugeSpeed;
		this.angleStep = angle / this.gaugeSpeed;
		this.innerCircleSize = outerCircleSize - (innerOuterCircleGap * 2);
		this.labelText1 = labelText1 != null ? labelText1.trim() : labelText1;
		this.labelText2 = labelText2 != null ? labelText2.trim() : labelText2;
	}

	@Override
	public void handle(long now) {
		gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight() );
		gc.setStroke(Color.web("#FF9482"));
		gc.setLineWidth(this.outerLineWidth);
		gc.strokeArc(this.defaultMarginX, this.defaultMarginY, this.outerCircleSize, this.outerCircleSize, this.gaugeStartAngle, -360, ArcType.OPEN);
		currAngle += angleStep;
		if (currAngle > angle) {
			currAngle = angle;
		}
		gc.setFill(Color.web("#FF9482"));
		if (value >= 0.0) {
			gc.fillArc(this.defaultMarginX + this.innerOuterCircleGap, this.defaultMarginY + this.innerOuterCircleGap, this.innerCircleSize, this.innerCircleSize, this.gaugeStartAngle, -this.currAngle, ArcType.ROUND);
		}
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Font.font(8));
		//gc2.strokeText("Hello", defaultMargin, defaultMargin);
		gc.setFill(Color.BLACK);
		gc.fillText(labelText1, this.defaultMarginX + (this.outerCircleSize / 2.0), this.defaultMarginY + this.outerCircleSize + 10);
		if (value >= 0.0) {
			//gc.fillText((int)(value) + " %", defaultMarginX + (outerCircleSize / 2.0), defaultMarginY + (outerCircleSize / 2.0));
			gc.fillText(labelText2.replace(" ", "\n"), this.defaultMarginX + (this.outerCircleSize / 2.0), this.defaultMarginY + (this.outerCircleSize / 2.0));
		} else {
			gc.fillText("N/A", this.defaultMarginX + (this.outerCircleSize / 2.0), this.defaultMarginY + (this.outerCircleSize / 2.0));
		}
		
		if (currAngle >= angle) {
			stop();
			reset();
		}
	}
	
	public void reset() {
		this.currAngle = 0.0; 
	}
}
