/*
 * @(#)TextField.java v1.0.0 November 30th 2010
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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;

import oracle.forms.handler.IHandler;
import oracle.forms.ui.VTextField;

public class TextField extends VTextField implements ITextItem {
    private DnDHandler dndHandler = null;
    private String sSelectedText = null;

    public TextField() {
        super();
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
     * Overrides processMouseEvent in super class in order to catch click events on selected text.
     * @param e
     */
    protected void processMouseEvent(MouseEvent e) {
        //Logger.getLogger().log(this, "processMouseEvent() " + e.getID());
        
        // Catch the MOUSE_PRESSED event when the cursor is default because we know that the cursor is over some selected text.
        if (e.getID() == MouseEvent.MOUSE_PRESSED && getCursor().equals(Cursor.getDefaultCursor())) {
            e.consume();
        }
        
        // Forms seems to unselect text on MOUSE_PRESSED so we need to change the MOUSE_CLICKED event.
        if (e.getID() == MouseEvent.MOUSE_CLICKED && getCursor().equals(Cursor.getDefaultCursor())) {
            e = new MouseEvent((Component)e.getSource(), MouseEvent.MOUSE_PRESSED, e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
        }

        super.processMouseEvent(e);
    }

    /**
     * Overrides processMouseMotionEvent in super class in order to change mouse pointer over selected text.
     * Will also set sSelectedText which is used later when a drag gesture is recognized.
     * @param e
     */
    protected void processMouseMotionEvent(MouseEvent e) {
        //Logger.getLogger().log(this, "processMouseMotionEvent()" + e.getID());
        
        // Find out if the mouse cursor is over some selected text.
        if (getSelectionRectangle().contains(e.getPoint())) {
            setCursor(Cursor.getDefaultCursor());
            sSelectedText = getSelectedText();
        } else { // Nope, not over selected text.
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            sSelectedText = null;
        }
        super.processMouseMotionEvent(e);
    }

    /**
     * Calculates the rectangle used to select text. This was by far the hardest part to implement!
     * This method will return a correct rectangle event if the beginning of the string is hidden, i.e. the
     * user has scrolled horizontally in the text box.
     */
    private Rectangle getSelectionRectangle() {
        String str = getDisplayStringFilter().convertString(getText());
        FontMetrics fontMetrics = getFontMetrics(getFont());        
        int i = getDisplayStringFilter().valueIndexToDisplayIndex(str, getSelectionStart());
        int x = convertCanvasToOuter(fontMetrics.stringWidth(str.substring(0, i)), 0).x;
        int y = (getInnerHeight() - fontMetrics.getHeight()) / 2;
        int width = fontMetrics.stringWidth(str.substring(getSelectionStart(), getSelectionEnd()));
        int height = fontMetrics.getAscent() + fontMetrics.getDescent();
        return new Rectangle(x, y, width, height);
    }
}
