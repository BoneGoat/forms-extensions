/*
 * @(#)TextItem.java v1.0.1 November 20th 2010
 * Pluggable Java Component (PJC) for Oracle Forms
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

package com.bincsoft.forms.columnsort;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;

import oracle.forms.engine.Main;
import oracle.forms.handler.IHandler;
import oracle.forms.properties.ID;
import oracle.forms.ui.VTextField;


public class TextItem extends VTextField {
    private boolean bDebug = false;
    private IHandler mHandler;
    private Frame formsTopFrame = null;
    private Main formsMain = null;

    private FormsColumnHeader formsColumnHeader;

    private static final ID idDebug = ID.registerProperty("DEBUG");
    private static final ID idCreateHeader = ID.registerProperty("CREATE_HEADER");
    private static final ID idSortMode = ID.registerProperty("SORT_MODE");
    private static final ID idChangeHeaderSize = ID.registerProperty("CHANGE_HEADER_SIZE");

    public TextItem() {
        super();
    }

    public void init(IHandler handler) {
        mHandler = handler;
        super.init(handler);
        formsMain = mHandler.getApplet();
        formsTopFrame = formsMain.getFrame();
    }

    public boolean setProperty(ID id, Object value) {
        String sParams = "";
        if (value != null)
            sParams = value.toString();
        if (id == idCreateHeader) {
            String arrParams[] = sParams.split("\\|");
            debugMessage("CREATE_HEADER: Received '" + arrParams.length + "' parameters, '" + sParams + "'");
            if (arrParams.length < 1) {
                System.out.println("CREATE_HEADER: Unsupported parameter count '" + sParams + "'. Should be '<name as string>|<height ratio as double>' (optional).");
                return true;
            }
            formsColumnHeader = new FormsColumnHeader(arrParams[0]);
            getParent().add(formsColumnHeader);
            double dHeight = getBounds().height * 0.8;

            if (arrParams.length == 2) {
                dHeight = getBounds().height * Double.parseDouble(arrParams[1]);
            }
            int iHeight = (int)Math.round(dHeight);
            formsColumnHeader.setBounds(getBounds().x, getBounds().y - iHeight, getBounds().width, iHeight);
            recursiveFindComponentAndAddListener(formsTopFrame, RelayBean.class.getName());
            return true;
        }

        else if (id == idSortMode) {
            String[] arrParams = sParams.split("\\|");
            String sAscDesc = "";
            String sSortOrder = "";
            if (arrParams.length > 1) { // We have two +n parameters
                sSortOrder = arrParams[0];
                sAscDesc = arrParams[1];
            } else { // We have only one parameter
                sAscDesc = arrParams[0];
            }

            if (sAscDesc.equalsIgnoreCase("DESC")) {
                formsColumnHeader.setSortMode(FormsColumnHeader.DESCENDING);
            } else if (sAscDesc.equalsIgnoreCase("ASC")) {
                formsColumnHeader.setSortMode(FormsColumnHeader.ASCENDING);
            } else {
                // Do nothing
                System.out.println("SORT_MODE: Unsupported sort mode '" + sParams + "'");
            }

            if (!sSortOrder.equals("")) {
                formsColumnHeader.setSortOrder(Integer.parseInt(sSortOrder));
            }

            if (arrParams.length > 1)
                formsColumnHeader.firePropertyChangeClickedProgrammaticallyWithMask();
            else
                formsColumnHeader.firePropertyChangeClickedProgrammatically();

            return true;
        }

        else if (id == idChangeHeaderSize) {
            String arrParams[] = sParams.split("\\|");
            debugMessage("CHANGE_HEADER_SIZE: Received '" + arrParams.length + "' parameters, '" + sParams + "'");
            if (arrParams.length < 1) {
                System.out.println("CHANGE_HEADER_SIZE: Unsupported parameter count '" + sParams + "'. Should be '<height ratio as double>|<width ratio as double>' (optional).");
                return true;
            }

            double dHeight = getBounds().height * Double.parseDouble(arrParams[0]);
            double dWidth = getBounds().width;

            if (arrParams.length == 2) {
                dWidth = getBounds().width * Double.parseDouble(arrParams[1]);
            }
            int iHeight = (int)Math.round(dHeight);
            int iWidth = (int)Math.round(dWidth);
            formsColumnHeader.setBounds(getBounds().x, getBounds().y - iHeight, iWidth, iHeight);
            return true;
        }
        return super.setProperty(id, value);
    }

    private void recursiveFindComponentAndAddListener(Component component, String sComponentName) {
        final String sName = component.getClass().getName();
        if (sName.equals(sComponentName)) {
            final RelayBean bean = (RelayBean)component;
            bean.addFormsColumnHeaderListener(formsColumnHeader);
        }

        if (component instanceof Container) {
            Component components[] = ((Container)component).getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] != null) {
                    recursiveFindComponentAndAddListener(components[i], sComponentName);
                }
            }
        }
    }

    protected void debugMessage(String s) {
        if (bDebug) {
            System.out.println(getClass().getName() + ": " + s);
        }
    }
}
