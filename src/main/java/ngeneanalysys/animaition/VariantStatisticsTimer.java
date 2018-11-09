package ngeneanalysys.animaition;

import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public final class VariantStatisticsTimer extends AnimationTimer {
	private double outerLineWidth = 1.0;
	private double outerCircleSize = 0;
	private double innerLineWidth = 10.0;
	private double innerOuterCircleGap = 1.0;
	private double innerCircleSize = 0.0;
	private double defaultMargin = outerLineWidth + 10.0;
	private double angle = 0.0;
	private double angleStep = 1.0;
	private double currAngle = 0.0;
	private GraphicsContext gc = null;
	private final ArcType arcType = ArcType.OPEN;
	private String labelText1 = null;
	private Canvas canvas = null;

	public VariantStatisticsTimer(GraphicsContext gc, double value, String labelText1, double gaugeSpeed) {
		this.gc = gc;
		this.canvas = gc.getCanvas();
		this.outerCircleSize = canvas.getWidth() - this.defaultMargin - 10;
		this.innerOuterCircleGap = (this.innerLineWidth + this.outerLineWidth) / 2.0 + 3.0;
		this.innerCircleSize = this.outerCircleSize - (this.innerOuterCircleGap * 2);
		this.angle = 360.0 * value;
		this.angleStep = this.angle / gaugeSpeed;
		if (labelText1 != null) {
			this.labelText1 = labelText1.trim();
		}
	}

	@Override
	public void handle(long now) {
		gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		gc.setStroke(Color.web("#C5C5C5"));
		gc.setLineWidth(this.outerLineWidth);
		double gaugeStartAngle = 90.0;
		gc.strokeArc(this.defaultMargin, this.defaultMargin, this.outerCircleSize, this.outerCircleSize,
				gaugeStartAngle, -360, this.arcType);
		gc.setStroke(Color.web("#F6545C"));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Font.font(12));
		// gc2.strokeText("Hello", defaultMargin, defaultMargin);
		gc.setFill(Color.web("#8D8D8D"));
		double labelMargin = 10.0;
		gc.fillText(this.labelText1, this.defaultMargin + (this.outerCircleSize / 2.0),
				this.defaultMargin + (this.outerCircleSize / 2.0) - labelMargin);
		gc.setFill(Color.BLACK);
		gc.setLineWidth(this.innerLineWidth);
		currAngle += angleStep;
		if (currAngle > angle) {
			currAngle = angle;
		}
		gc.strokeArc(this.defaultMargin + this.innerOuterCircleGap, this.defaultMargin + this.innerOuterCircleGap,
				this.innerCircleSize, this.innerCircleSize, gaugeStartAngle, -this.currAngle, this.arcType);
		if (currAngle >= angle) {
			stop();
			reset();
		}
	}
	public void reset() {
		this.currAngle = 0.0; 
	}
}