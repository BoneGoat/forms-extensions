package com.bincsoft.forms.antialiasing;

import java.awt.Graphics2D;

import java.awt.RenderingHints;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AntialiasingAspect {

    @Before("execution (* oracle.ewt.painter.TextPainter.paintText(..))")
    public void beforePaintText(JoinPoint joinPoint) {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[1]));
    }

    @Before("execution (* oracle.ewt.lwAWT.lwText.LWTextField.paintCanvasInterior(..))")
    public void beforePaintCanvasInterior(JoinPoint joinPoint) throws Throwable {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[0]));
    }

    @Before("execution (* oracle.ewt.lwAWT.lwText.Paragraph.doDrawString(..))")
    public void beforeDoDrawString(JoinPoint joinPoint) throws Throwable {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[0]));
    }

    @Before("execution (* oracle.ewt.multiLineLabel.MultiLineLabel._paintCanvasInterior(..))")
    public void before_paintCanvasInterior(JoinPoint joinPoint) throws Throwable {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[0]));
    }

    @Before("execution (* oracle.ewt.multiLineLabel.MultiLineLabel._paintFullJustified(..))")
    public void before_paintFullJustified(JoinPoint joinPoint) throws Throwable {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[0]));
    }

    @Before("execution (* oracle.forms.handler.PromptItem.drawPrompt(..))")
    public void beforeDrawPrompt(JoinPoint joinPoint) throws Throwable {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[0]));
    }

    @Before("execution (* oracle.graphics.vgs.ui.Rect.drawGraphic(..))")
    public void beforeDrawGraphic(JoinPoint joinPoint) throws Throwable {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[0]));
    }

    @Before("execution (* oracle.graphics.vgs.ui.Text.drawText(..))")
    public void beforeDrawText(JoinPoint joinPoint) throws Throwable {
        enableFontAntialiasing(((Graphics2D)joinPoint.getArgs()[0]));
    }

    private void enableFontAntialiasing(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    }
}
