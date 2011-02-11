/*
 * @(#)FormsDragSourceListener.java v1.0.0 November 30th 2010
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import java.io.IOException;


public class FormsDragSourceListener implements DragSourceListener {
    DnDHandler handler = null;

    public FormsDragSourceListener(DnDHandler handler) {
        super();
        this.handler = handler;
    }

    public void dragEnter(DragSourceDragEvent dsde) {
        Logger.getLogger().log(this, "dragEnter()");
    }

    public void dragOver(DragSourceDragEvent dsde) {
        //Logger.getLogger().log(this, "dragOver()");
    }

    public void dragExit(DragSourceEvent dse) {
        Logger.getLogger().log(this, "dragExit()");
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
        Logger.getLogger().log(this, "dropActionChanged()");
    }

    /**
     * Callback method when FormsDropTargetListener has processed processDropTargetDropEvent().
     * @param dsde
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        Logger.getLogger().log(this, "dragDropEnd()");
        if (dsde.getDropSuccess()) {
            Logger.getLogger().log(this, "Drop was successful, notifying Forms...");
            // The drop was successful, now we can notify Forms using dispatchCustomEvent().
            try {
                handler.dispatchCustomEvent((String)dsde.getDragSourceContext().getTransferable().getTransferData(DataFlavor.stringFlavor));
            } catch (UnsupportedFlavorException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // We can also do other things like deleting the selected text from the source if
            // this was a move event. This code only implements copy events.
        } else {
            Logger.getLogger().log(this, "Drop was unsuccessful.");
        }
    }
}
