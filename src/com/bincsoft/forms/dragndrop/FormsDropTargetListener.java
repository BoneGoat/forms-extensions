/*
 * @(#)FormsDropTargetListener.java v1.0.0 November 30th 2010
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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;


public class FormsDropTargetListener implements DropTargetListener {
    DnDHandler handler = null;
    
    public FormsDropTargetListener(DnDHandler handler) {
        super();
        this.handler = handler;
    }

    /**
     * The user has dragged something over this component. We now need to find
     * out if we can accept it.
     * @param dtde
     */
    public void dragEnter(DropTargetDragEvent dtde) {
        processDropTargetDragEvent(dtde);
    }

    public void dragExit(DropTargetEvent dte) {}

    /**
     * Executes when user has dragged something over this TextField but hasn't released the mouse button.
     * @param dtde
     */
    public void dragOver(DropTargetDragEvent dtde) {
        processDropTargetDragEvent(dtde);
    }

    /**
     * Here we would do things when the users suddenly presses a key while dragging.
     * Maybe we'll change the mouse cursor, I don't know.
     * @param dtde
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {}

    /**
     * Executes when user releases the mouse button.
     * @param dtde
     */
    public void drop(DropTargetDropEvent dtde) {
        processDropTargetDropEvent(dtde);
    }

    private void processDropTargetDropEvent(DropTargetDropEvent dtde) {
        try {
            Transferable tr = dtde.getTransferable();
            if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) { // We will accept strings.
                dtde.acceptDrop(DnDConstants.ACTION_COPY); // We will accept a copy drop.
                String s = (String)tr.getTransferData(DataFlavor.stringFlavor); // Get the string from the Transferable.
                setText(dtde.getDropTargetContext().getComponent(), s); // Set the text in this TextField to the string extracted from the Transferable.
                // We could do more advanced things here like inserting the text into existing text.
                // We could also notify Forms by doing something like this
                //handler.dispatchCustomEvent(this, "Some string...");
                dtde.getDropTargetContext().dropComplete(true); // Notify the drag source that we have completed.
            } else { // The Transferable did not contain any data that we are interested in.
                dtde.rejectDrop();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dtde.rejectDrop();
        }
    }

    private void processDropTargetDragEvent(DropTargetDragEvent dtde) {
        try {
            if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) { // Only accept strings.
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                dtde.rejectDrag(); // Not interested.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dtde.rejectDrag();
        }
    }

    /**
     * Helper method to set the text for a TextField or TextArea.
     * @param component
     * @param s
     */
    private void setText(Component component, String s) {
        if (component instanceof TextField) {
            ((TextField)component).setText(s);
        }

        if (component instanceof TextArea) {
          ((TextArea)component).setText(s);
        }
    }
}
