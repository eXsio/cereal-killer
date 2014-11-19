/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.exsio.ck.entrytable.presenter;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import static pl.exsio.ck.entrytable.presenter.EntryTableModel.PAGE_SIZE;
import pl.exsio.ck.model.dao.EntryDao;

/**
 *
 * @author exsio
 */
public class EntryTableModel extends DefaultTableModel {

    protected static final int PAGE_SIZE = 100;
    
    protected static int i = 0;

    protected final EntryDao dao;

    protected final String[] serials;

    protected final String query;

    protected final String orderBy;

    protected final String orderDesc;

    protected ArrayList<Object[]> cache;

    protected Integer rowCount;

    protected int startPosition = 0;

    public EntryTableModel(EntryDao dao) {
        this(dao, null, null, null, null);
    }

    public EntryTableModel(EntryDao dao, String orderBy) {
        this(dao, orderBy, null, null, null);
    }

    public EntryTableModel(EntryDao dao, String orderBy, String orderDesc, String query, String[] serials) {
        this.dao = dao;
        this.serials = serials;
        this.query = query;
        this.orderDesc = orderDesc;
        this.orderBy = orderBy;
        this.getItems(startPosition, startPosition + PAGE_SIZE);
        this.setColumnIdentifiers(new String[]{
            "Nr seryjny", "Dostawca", "Data dostawy",
            "Nr faktury zakupu", "Odbiorca", "Data sprzedaży",
            "Nr faktury sprzedaży", "Id"
        });
    }

    protected final void getItems(int from, int to) {
        System.out.println("loading items "+from+" - "+to);
        i++;
        System.out.println("loads: "+ i);
        this.cache = this.dao.fetchTableRows(to - from, from, this.query, this.orderBy, this.orderDesc, this.serials);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

            if ((rowIndex < startPosition) || (rowIndex >= (startPosition + PAGE_SIZE))) {
                this.getItems(rowIndex, rowIndex + PAGE_SIZE);
                this.startPosition = rowIndex;
            }
            return this.cache.get(rowIndex - startPosition)[columnIndex];
        
    }

    @Override
    public int getRowCount() {
        if (this.dao == null) {
            return 0;
        }
        if (this.rowCount == null) {
            this.rowCount = this.dao.count(this.query, this.serials);
        }
        return this.rowCount;
    }

}
