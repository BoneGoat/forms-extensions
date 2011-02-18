/*
 * @(#)FormsColumnHeader.java  v1.0.1 November 20th 2010
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.net.URL;

import javax.swing.JLabel;

public class FormsColumnHeader extends JLabel {
    public static final int UNSORTED = 0;
    public static final int DESCENDING = 1;
    public static final int ASCENDING = 2;
    public static final int NOORDER = 0;

    private int iSortMode = UNSORTED;
    private int iSortOrder = NOORDER;
    private Image imgDesc = null;
    private Image imgAsc = null;

    private Color clrGrey = new Color(200, 200, 200);
    private Color clrBlue = new Color(137, 181, 221);
    private AlphaComposite myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private boolean bMouseOver = false;
    private boolean bMonitored = false;

    public FormsColumnHeader(String sName) {
        super();
        setName(sName);
        addMouseListener(new MouseListener() {
                public void mousePressed(MouseEvent e) {}

                public void mouseReleased(MouseEvent e) {}

                public void mouseEntered(MouseEvent e) {
                    bMouseOver = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    bMouseOver = false;
                    repaint();
                }

                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        switch (iSortMode) {
                        case FormsColumnHeader.UNSORTED:
                            setSortMode(FormsColumnHeader.ASCENDING);
                            break;
                        case FormsColumnHeader.DESCENDING:
                            setSortMode(FormsColumnHeader.ASCENDING);
                            break;
                        case FormsColumnHeader.ASCENDING:
                            setSortMode(FormsColumnHeader.DESCENDING);
                            break;
                        default:
                            setSortMode(FormsColumnHeader.UNSORTED);
                            break;
                        }

                        if (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)
                            firePropertyChange("ClickedWithMask", true, false); // CTRL mask was found
                        else
                            firePropertyChange("SortMode", UNSORTED, getSortMode());
                    }
                }
            });

        URL imageURL = getClass().getResource("/com/bincsoft/forms/columnsort/sort_desc.png");
        if (imageURL != null) {
            imgDesc = Toolkit.getDefaultToolkit().getImage(imageURL);
        }
        imageURL = getClass().getResource("/com/bincsoft/forms/columnsort/sort_asc.png");
        if (imageURL != null) {
            imgAsc = Toolkit.getDefaultToolkit().getImage(imageURL);
        }

        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(imgDesc, 0);
        tracker.addImage(imgAsc, 1);
        try {
            tracker.waitForAll();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void firePropertyChangeClickedProgrammatically() {
        firePropertyChange("SortModeProgrammatically", false, true);
    }

    public void firePropertyChangeClickedProgrammaticallyWithMask() {
        firePropertyChange("ClickedProgrammaticallyWithMask", false, true);
    }

    public String getBlockName() {
        return getName().substring(0, getName().indexOf("."));
    }

    public void setSortMode(int i) {
        iSortMode = i;
        repaint();
    }

    public int getSortMode() {
        return iSortMode;
    }
    
    public void setSortOrder(int i) {
      iSortOrder = i;
      repaint();
    }
    
    public int getSortOrder() {
      return iSortOrder;
    }

    public String getSortModeAsString() {
        String sSortMode = "";
        switch (getSortMode()) {
        case DESCENDING:
            sSortMode = "DESC";
            break;
        case ASCENDING:
            sSortMode = "ASC";
            break;
        default:
            sSortMode = "UNSORTED";
            break;
        }
        return sSortMode;
    }

    public void setMonitored(boolean b) {
        bMonitored = b;
    }

    public boolean isMonitored() {
        return bMonitored;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();

        // Paint background if mouse is hovering
        if (bMouseOver) {
            g2d.setPaint(clrBlue);
            g2d.setComposite(myAlpha);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setPaint(clrGrey);
            g2d.fillRect(0, getHeight() - 1, getWidth(), 1);
        }

        // Paint arrow
        switch (iSortMode) {
        case DESCENDING:
            if (imgDesc != null) {
                g.drawImage(imgDesc, getWidth() / 2 - 3, 1, null);
            }
            break;
        case ASCENDING:
            if (imgAsc != null) {
                g.drawImage(imgAsc, getWidth() / 2 - 3, 1, null);
            }
            break;
        default:
            // Do nothing
            break;
        }

        // Paint separator line
        g2d.setPaint(clrGrey);
        g2d.fillRect(getWidth() - 1, 0, 1, getHeight());

        // Paint sort order digit
        if (getSortMode() != UNSORTED && getSortOrder() != NOORDER) {
            g2d.setPaint(Color.WHITE);
            Font font = new Font("Arial", Font.PLAIN, 8);
            g2d.setFont(font);
            g2d.drawString(new Integer(getSortOrder()).toString(), getWidth() / 2 - 12, 6);
        }

        g2d.dispose();
    }

}
