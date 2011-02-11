/*
 * @(#)TextArea.java v1.0.0 November 30th 2010
 *
 * Copyright (c) 2010 Bincsoft and/or its affiliates. All rights reserved.
 *
 * Bincsoft grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Bincsoft.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package com.bincsoft.forms.dragndrop;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import oracle.ewt.LookAndFeel;
import oracle.ewt.UIDefaults;
import oracle.ewt.lwAWT.lwText.LWTextArea;

import oracle.forms.handler.IHandler;
import oracle.forms.ui.VTextArea;


public class TextArea extends VTextArea implements ITextItem, MouseListener {
    private DnDHandler dndHandler = null;
    private String sSelectedText = null;

    public TextArea() {
        super();
        addMouseListener(this);
    }

    /**
     * Oracle seems to want us to do this... Anyway, good time to init the DnDHandler.
     */
    public void init(IHandler handler) {
        super.init(handler);
        dndHandler = new DnDHandler(this, handler.getApplet().getFrame());
    }

    /**
     * Implementation of method in ITextItem, used by FormsDragGestureListener.
     * If using getSelectedText() the drag events will be too "sticky".
     * @return
     */
    public String getSelectedTextForDrag() {
        return sSelectedText;
    }
    
    /**
     * Implementation of method in ITextItem, used by FormsDragGestureListener for starting a drag event.
     * @return
     */
    public DragSourceListener getDragSourceListener() {
        return dndHandler.getDragSourceListener();
    }

    /**
     * There does not seem to be any implemented setText() method so we'll just create one.
     * @param s
     */
    public void setText(String s) {
        ((LWTextArea)getContent()).setText(s);
    }

    /**
     * We need to get selected text when a mouse button is pressed.
     * @return
     */
    public String getSelectedText() {
        return ((LWTextArea)getContent()).getSelectedText();
    }

    /**
     * Finds out what color a certain point on screen is.
     * @param p Coordinates on screen
     * @return The color at the coordinates
     */
    public Color getColorAtPoint(Point p) {
        try {
            Robot robot = new Robot();
            return robot.getPixelColor(p.x, p.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return null;
    }

    // MouseListener

    /**
     * When the user presses a mouse button we'll get the color at the mouse
     * pointer coordinates. Then we'll check if that color matches the current
     * highlight color and if it does, remember selected text and consume the event.
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        //Logger.getLogger().log(this, "mousePressed() " + e.getID());
        UIDefaults ui = getUIDefaults();
        Color clrPoint = getColorAtPoint(e.getLocationOnScreen());
        Color clrTextHighlight = ui.getColor(LookAndFeel.TEXT_HIGHLIGHT);

        if (clrPoint != null &&
            clrPoint.getRed() == clrTextHighlight.getRed() &&
            clrPoint.getGreen() == clrTextHighlight.getGreen() &&
            clrPoint.getBlue() == clrTextHighlight.getBlue()) {
            sSelectedText = getSelectedText();
            e.consume();
        } else {
            sSelectedText = null;
        }
    }

    public void mouseReleased(MouseEvent e) {
        //Logger.getLogger().log(this, "mouseReleased() " + e.getID());
    }

    public void mouseEntered(MouseEvent e) {
        //Logger.getLogger().log(this, "mouseEntered() " + e.getID());
    }

    public void mouseExited(MouseEvent e) {
        //Logger.getLogger().log(this, "mouseExited() " + e.getID());
    }

    public void mouseClicked(MouseEvent e) {
        //Logger.getLogger().log(this, "mouseClicked() " + e.getID());
        e = new MouseEvent((Component)e.getSource(), MouseEvent.MOUSE_PRESSED, e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
        super.processMouseEvent(e);
    }
}
