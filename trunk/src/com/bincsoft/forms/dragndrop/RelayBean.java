/*
 * @(#)RelayBean.java v1.0.0 November 30th 2010
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

import java.awt.Frame;

import oracle.forms.engine.Main;
import oracle.forms.handler.IHandler;
import oracle.forms.properties.ID;
import oracle.forms.ui.CustomEvent;
import oracle.forms.ui.VBean;


public class RelayBean extends VBean {
    private IHandler mHandler;
    private static final ID DEBUG = ID.registerProperty("DEBUG");
    private Frame formsTopFrame = null;
    private Main formsMain = null;
    private static final ID DRAGNDROP_VALUES = ID.registerProperty("DRAGNDROP_VALUES");
    private static final ID DRAGNDROP_EVENT = ID.registerProperty("DRAGNDROP_EVENT");

    public RelayBean() {}

    public void init(IHandler handler) {
        mHandler = handler;
        super.init(handler);
        formsMain = mHandler.getApplet();
        formsTopFrame = formsMain.getFrame();
    }
    
    public void dispatchCustomEvent(String sMsg) {
        try {
            Logger.getLogger().log(this, "Dispatching message to Forms: " + sMsg);
            mHandler.setProperty(DRAGNDROP_VALUES, sMsg);
            CustomEvent ce = new CustomEvent(mHandler, DRAGNDROP_EVENT);
            dispatchCustomEvent(ce);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean setProperty(ID id, Object value) {
        String sParams = "";
        if (value != null)
            sParams = value.toString();
        
        if (id == DEBUG) {
            Logger.getLogger().setDebug(sParams.equalsIgnoreCase("TRUE"));
        }
        return super.setProperty(id, value);
    }
}
