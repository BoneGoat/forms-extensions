/*
 * @(#)DnDHandler.java v1.0.0 November 30th 2010
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
import java.awt.Container;
import java.awt.Frame;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;

public class DnDHandler {
    private FormsDragSourceListener fdsl = new FormsDragSourceListener(this); // Need a listener
    private Frame formsTopFrame = null; // Reference to the application frame so we can find the RelayBean

    /**
     * Constructor initializes drag and drop for the callee.
     * @param c
     * @param frame
     */
    public DnDHandler(Component c, Frame frame) {
        formsTopFrame = frame;
        c.setDropTarget(new DropTarget(c, new FormsDropTargetListener(this)));
        new DragSource().createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_COPY, new FormsDragGestureListener((ITextItem)c));
    }

    /**
     * Used by FormsDragGestureListener when a drag gesture has been recognized.
     * @return
     */
    public DragSourceListener getDragSourceListener() {
        return fdsl;
    }

    /**
     * Used to send events back to Forms. Not needed if running Forms 11.
     * @param sMsg
     */
    protected void dispatchCustomEvent(String sMsg) {
        findRelayBean(formsTopFrame, sMsg);
    }

    /**
     * Recursive method which finds the RelayBean if present in the application
     * and uses it to send a string back to Forms.
     * @param component
     * @param sMsg
     */
    private void findRelayBean(Component component, String sMsg) {
        if (component != null && component.getClass() != null) {
            if (component.getClass().getName().equals(RelayBean.class.getName())) {
                Logger.getLogger().log(this, "Found the RelayBean.");
                ((RelayBean)component).dispatchCustomEvent(sMsg);
                return;
            }
        }

        if (component instanceof Container) {
            Component components[] = ((Container)component).getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] != null) {
                    findRelayBean(components[i], sMsg);
                }
            }
        }
    }
}
