package ngeneanalysys.animaition;

import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * @author Jang
 * @since 2018-03-06
 */
public class HddStatusTimer extends AnimationTimer {
    private double outerLineWidth = 0.0;
    private double outerCircleSize = 0;
    private double gaugeStartAngle = 90.0;
    private double innerLineWidth = 0.0;
    private double innerOuterCircleGap = 1.0;
    private double innerCircleSize = 0.0;
    private double defaultMargin = outerLineWidth + 10.0;
    private double value;
    private double angle = 0.0;
    private double angleStep = 1.0;
    private double currAngle = 0.0;
    private GraphicsContext gc = null;
    private final ArcType arcType = ArcType.OPEN;
    private final double labelMargin = 10.0;
    private String labelText1 = null;
    private String labelText2 = null;
    private Canvas canvas = null;
    private double gaugeSpeed;
    public HddStatusTimer(GraphicsContext gc, double value, String labelText1, String labelText2, double gaugeSpeed) {
        this.gc = gc;
        this.canvas = gc.getCanvas();
        this.outerCircleSize = canvas.getWidth() - this.defaultMargin - 1;
        this.innerOuterCircleGap = (this.innerLineWidth + this.outerLineWidth) / 2.0 + 3.0;
        this.innerCircleSize = this.outerCircleSize - (this.innerOuterCircleGap * 2);
        this.value = value;
        this.angle = 360.0 * this.value;
        this.gaugeSpeed =  gaugeSpeed;
        this.angleStep = this.angle / this.gaugeSpeed;
        if (labelText1 != null) {
            this.labelText1 = labelText1.trim();
        }
        if (labelText2 != null) {
            this.labelText2 = labelText2.trim();
        }
    }

    @Override
    public void handle(long now) {
        gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
        gc.setStroke(Color.web("#C5C5C5"));
        gc.setLineWidth(2);
        for(int i = 0; i < 25; i++) {
            gc.strokeArc(this.defaultMargin + this.innerOuterCircleGap + i, this.defaultMargin + this.innerOuterCircleGap + i,
                    this.innerCircleSize - (i * 2), this.innerCircleSize - (i * 2), this.gaugeStartAngle, -360, this.arcType);
        }
        gc.setStroke(Color.web("#223654"));

        currAngle += angleStep;
        if (currAngle > angle) {
            currAngle = angle;
        }
        gc.setLineWidth(2);
        for(int i = 0; i < 25; i++) {
            gc.strokeArc(this.defaultMargin + this.innerOuterCircleGap + i, this.defaultMargin + this.innerOuterCircleGap + i,
                    this.innerCircleSize - (i * 2), this.innerCircleSize - (i * 2), this.gaugeStartAngle, -this.currAngle, this.arcType);
        }
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Noto Sans KR Light",14));
        gc.setFill(Color.web("#585858"));
        gc.fillText(this.labelText1, this.defaultMargin + (this.outerCircleSize / 2.0),
                this.defaultMargin + (this.outerCircleSize / 2.0) - this.labelMargin);
        gc.setFont(Font.font("Noto Sans KR Light",10));
        gc.setFill(Color.web("#585858"));
        gc.fillText(this.labelText2, this.defaultMargin + (this.outerCircleSize / 2.0),
                this.defaultMargin + (this.outerCircleSize / 2.0) + this.labelMargin);
        if (currAngle >= angle) {
            stop();
            reset();
        }
    }
    public void reset() {
        this.currAngle = 0.0;
    }
}
