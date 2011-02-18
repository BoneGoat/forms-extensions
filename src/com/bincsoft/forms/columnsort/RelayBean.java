/*
 * @(#)RelayBean.java v1.0.1 November 20th 2010
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collections;

import oracle.forms.engine.Main;
import oracle.forms.handler.IHandler;
import oracle.forms.properties.ID;
import oracle.forms.ui.CustomEvent;
import oracle.forms.ui.VBean;


public class RelayBean extends VBean {

    protected static final ID pDebug = ID.registerProperty("DEBUG");
    protected static final ID pHeaderListen = ID.registerProperty("HEADER_LISTEN");
    protected static final ID pHeaderClicked = ID.registerProperty("HEADER_CLICKED");
    protected static final ID pEventValues = ID.registerProperty("EVENT_VALUES");

    private IHandler mHandler = null; // Forms Handler
    private Main formsMain = null; // Forms main class
    private boolean bDebug = false;
    private FormsColumnHeaderList lstSortColumns = new FormsColumnHeaderList();

    public RelayBean() {
    }

    public final void init(IHandler handler) {
        mHandler = handler;
        super.init(handler);
        formsMain = mHandler.getApplet();
    }

    public void addFormsColumnHeaderListener(FormsColumnHeader header) {
        header.addPropertyChangeListener(formsColumnHeaderChangeListener);
        header.setMonitored(true);
    }

    private void recursiveFindComponent(Component component, String sComponentName) {
        final String sName = component.getClass().getName();
        if (sName.equals(sComponentName)) {
            final FormsColumnHeader header = (FormsColumnHeader)component;
            if (!header.isMonitored()) {
                debugMessage("Found one column header: " + sName);
                header.addPropertyChangeListener(formsColumnHeaderChangeListener);
                header.setMonitored(true);
            } else
                debugMessage("Found one column header but it's already monitored: " + sName);
        }

        if (component instanceof Container) {
            Component components[] = ((Container)component).getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] != null) {
                    recursiveFindComponent(components[i], sComponentName);
                }
            }
        }
    }

    public boolean setProperty(ID _ID, Object _args) {
        String sParams = "";
        if (_args != null)
            sParams = _args.toString();
        if (_ID == pDebug) {
            bDebug = ((String)_args).equalsIgnoreCase("TRUE");
            debugMessage("DEBUG: " + sParams);
            return true;
        } else if (_ID == pHeaderListen) {
            debugMessage("HEADER_LISTEN");
            recursiveFindComponent(formsMain.getFrame(), FormsColumnHeader.class.getName());
            return true;
        } else {
            return super.setProperty(_ID, _args);
        }
    }

    private void debugMessage(String st) {
        if (bDebug) {
            System.out.println(this.getClass().getName() + " - " + st);
        }
    }

  private PropertyChangeListener formsColumnHeaderChangeListener = new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            if (e.getSource() instanceof FormsColumnHeader) {            
              if (e.getPropertyName().equals("SortMode")) {
                sortMode(e, true);
              } else if (e.getPropertyName().equals("SortModeProgrammatically")) {
                sortMode(e, false);
              } else if (e.getPropertyName().equals("ClickedWithMask")) {
                clickedWithMask(e, true);
              } else if (e.getPropertyName().equals("ClickedProgrammaticallyWithMask")) {
                clickedWithMask(e, false);
              } else {} // What!? We can have other events?
            }
          }
          
          private void sortMode(PropertyChangeEvent e, boolean bNotify) {
            FormsColumnHeader header = (FormsColumnHeader)e.getSource();
            int iRememberSortMode = header.getSortMode();
            lstSortColumns.resetAllInBlock(header.getBlockName());
            header.setSortOrder(FormsColumnHeader.NOORDER);
            header.setSortMode(iRememberSortMode);
            
            if (bNotify) {
              try {
                  mHandler.setProperty(pEventValues, ((FormsColumnHeader)e.getSource()).getName() + "," + ((FormsColumnHeader)e.getSource()).getSortModeAsString());
                  CustomEvent ce = new CustomEvent(mHandler, pHeaderClicked);
                  dispatchCustomEvent(ce);
              } catch (Exception ex) {
                  ex.printStackTrace();
              }
            }
          }
          
          private void clickedWithMask(PropertyChangeEvent e, boolean bNotify) {       
            if (bNotify) {
                String sSortString = "";
                //System.out.println("UtilBean - BlockName: " + ((FormsColumnHeader)e.getSource()).getBlockName());
                FormsColumnHeaderList lst = lstSortColumns.getList(((FormsColumnHeader)e.getSource()).getBlockName());
                if (((FormsColumnHeader)e.getSource()).getSortOrder() == FormsColumnHeader.NOORDER)
                  ((FormsColumnHeader)e.getSource()).setSortOrder(99);
                Collections.sort(lst, new BySortOrder());
                for (int i=0; i<lst.size(); i++) {
                  lst.getFormsColumnHeader(i).setSortOrder(i+1);
                  if (!sSortString.equals("")) sSortString += "|";
                  sSortString += lst.getFormsColumnHeader(i).getName() + "," + lst.getFormsColumnHeader(i).getSortModeAsString();
                }
                
              try {
                  mHandler.setProperty(pEventValues, sSortString);
                  CustomEvent ce = new CustomEvent(mHandler, pHeaderClicked);
                  dispatchCustomEvent(ce);
              } catch (Exception ex) {
                  ex.printStackTrace();
              }
            }
            
            Collections.sort(lstSortColumns, new BySortOrder());
          }
      };

    public class BySortOrder implements java.util.Comparator {
        public int compare(Object header1, Object header2) {
          return ((FormsColumnHeader)header1).getSortOrder() - ((FormsColumnHeader)header2).getSortOrder(); 
        }
      }
      
      public class FormsColumnHeaderList extends ArrayList {
          public FormsColumnHeader getFormsColumnHeader(int i) {
              return (FormsColumnHeader)get(i);
          }
          
          public FormsColumnHeaderList getList(String sBlockName) {
              FormsColumnHeaderList lst = new FormsColumnHeaderList();
              for (int i=0; i<size(); i++) {
                  if (getFormsColumnHeader(i).getBlockName().equals(sBlockName) && getFormsColumnHeader(i).getSortMode() != FormsColumnHeader.UNSORTED) {
                      lst.add(getFormsColumnHeader(i));
                  }
              }
              return lst;
          }
          
        public void resetAllInBlock(String sBlockName) {
            for (int i=0; i<size(); i++) {
                if (getFormsColumnHeader(i).getBlockName().equals(sBlockName)) {
                  getFormsColumnHeader(i).setSortMode(FormsColumnHeader.UNSORTED);
                  getFormsColumnHeader(i).setSortOrder(FormsColumnHeader.NOORDER);
                }
            }
        }
      }
}
