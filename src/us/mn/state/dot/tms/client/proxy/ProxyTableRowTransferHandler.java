/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016  California Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.client.proxy;

import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.sonar.client.ProxyListener;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

/**
 * Handles drag & drop row reordering for ProxyTables.
 *
 * @author Dan Rossiter
 */
public class ProxyTableRowTransferHandler extends TransferHandler {

    /** What type of data is coming from dragged row */
    private static final DataFlavor localObjectFlavor = new ActivationDataFlavor(
        Integer.class,
        DataFlavor.javaJVMLocalObjectMimeType,
        "Row Index");

    /**
     * listener allows us to synchronize against received updates from the server
     * if we continue ordering before the previous re-order is complete things break
     */
    private final ProxyListener<SonarObject> listener = new ProxyListener<SonarObject>() {
        @Override
        public void proxyAdded(SonarObject proxy) { }

        @Override
        public void enumerationComplete() { }

        @Override
        public void proxyRemoved(SonarObject proxy) { }

        /** We wait for one re-order change to complete before moving on to the next re-order. */
        @Override
        public void proxyChanged(SonarObject proxy, String a) {
            synchronized (ProxyTableRowTransferHandler.this) {
                // once we get our changes back from the server we can move on
                if (proxy.getName().equals(currentName) && model.getManualSort(proxy) == targetOrder)
                    ProxyTableRowTransferHandler.this.notifyAll();
            }
        }
    };

    /** The table being operated on */
    private final JTable table;

    /** The table model being operated on */
    private final ProxyTableModel<SonarObject> model;

    /** The current proxy name whose order is being updated. */
    private String currentName;

    /** The current proxy target order value. */
    private int targetOrder = -1;

    /** Construct new ProxyTableRowTransferHandler */
    public ProxyTableRowTransferHandler(final JTable table) {
        this.table = table;
        model = (ProxyTableModel<SonarObject>)table.getModel();
    }

    /**
     * @param c  the component holding the data to be transferred;
     *              provided to enable sharing of <code>TransferHandler</code>s
     * @return  the representation of the data to be transferred, or
     *  <code>null</code> if the property associated with <code>c</code>
     *  is <code>null</code>
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        assert (c == table);
        return new DataHandler(table.getSelectedRow(), localObjectFlavor.getMimeType());
    }

    /**
     * @param support the object containing the details of
     *        the transfer, not <code>null</code>.
     * @return <code>true</code> if the import can happen,
     *         <code>false</code> otherwise
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean b = support.getComponent() == table &&
                    support.isDrop() &&
                    support.isDataFlavorSupported(localObjectFlavor);
        table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return b;
    }

    /**
     * @param c  the component holding the data to be transferred;
     *           provided to enable sharing of <code>TransferHandler</code>s
     * @return Always {@code COPY_OR_MOVE}.
     */
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    /**
     * Causes a transfer to occur from a clipboard or a drag and
     * drop operation.
     *
     * @param support the object containing the details of
     *        the transfer, not <code>null</code>.
     * @return true if the data was inserted into the component, false otherwise
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        JTable target = (JTable) support.getComponent();
        JTable.DropLocation dl =
                (JTable.DropLocation) support.getDropLocation();
        int rowTo = dl.getRow();
        int max = model.getRowCount() - 1;
        if (rowTo < 0 || rowTo > max)
            rowTo = max;

        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try {
            Integer rowFrom = (Integer) support.getTransferable().getTransferData(localObjectFlavor);
            if (rowTo > rowFrom)
                rowTo--;

            if (rowFrom != -1 && rowFrom != rowTo) {
                // Only rows between the from & to location will be considered as others will not change
                // rows < the target row index will have index decremented
                // rows > the target row index will have index incremented
                final SonarObject[] proxies = model.getRowProxies(new SonarObject[model.getRowCount()]);

                // to avoid temporary violation of unique order DB constraint, move target
                // proxy out of the way while surrounding proxies are first updated
                synchronized (this) {
                    model.cache.addProxyListener(listener);
                    updateProxyOrder(proxies[rowFrom], proxies.length);
                    int i = nextI(rowFrom, rowFrom, rowTo);
                    for (; checkLoopCondition(i, rowFrom, rowTo); i = nextI(i, rowFrom, rowTo))
                        updateProxyOrder(proxies[i], nextI(i, rowTo, rowFrom));
                    updateProxyOrder(proxies[rowFrom], rowTo);
                    model.cache.removeProxyListener(listener);
                }

                // reselect the just-reordered row in its new location
                target.getSelectionModel().addSelectionInterval(rowTo, rowTo);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Update the requested proxy order and don't return until the updated proxy is returned from server */
    private void updateProxyOrder(SonarObject proxy, int order) {
        currentName = proxy.getName();
        targetOrder = order;
        model.setManualSort(proxy, order);
        while (model.getManualSort(proxy) != targetOrder) {
            try {
                wait(5000);
            } catch (InterruptedException e) { }
        }
    }

    /** Increment or decrement the importData i based on whether we're moving a row up or down. */
    private static int nextI(int i, int rowFrom, int rowTo) {
        return rowFrom < rowTo ? i + 1 : i - 1;
    }

    /** Check the importData loop condition based on whether we're moving a row up or down */
    private static boolean checkLoopCondition(int i, int rowFrom, int rowTo) {
        return (rowFrom < rowTo && i <= rowTo) || (rowFrom > rowTo && i >= rowTo);
    }

    /**
     * Invoked after data has been exported.  This method should remove
     * the data that was transferred if the action was <code>MOVE</code>.
     *
     * @param source the component that was the source of the data
     * @param data   The data that was transferred or possibly null
     *               if the action is <code>NONE</code>.
     * @param action the actual action that was performed
     */
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == TransferHandler.MOVE || action == TransferHandler.NONE)
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
