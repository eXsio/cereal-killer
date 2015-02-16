/* 
 * The MIT License
 *
 * Copyright 2015 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.ck.entrytable.presenter;

import java.util.TreeMap;
import javax.swing.table.DefaultTableModel;
import static pl.exsio.ck.entrytable.presenter.EntryTableModel.PAGE_SIZE;
import pl.exsio.ck.model.dao.EntryDao;

/**
 *
 * @author exsio
 */
public class EntryTableModel extends DefaultTableModel {

    protected static final int PAGE_SIZE = 100;

    protected static final int CACHE_SIZE = 2000;

    protected final EntryDao dao;

    protected final String[] serials;

    protected final String query;

    protected final String orderBy;

    protected final String orderDesc;

    protected final TreeMap<Integer, Object[]> cache;

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
        this.cache = new TreeMap<>();
        this.orderBy = orderBy;
        this.getItems(startPosition, startPosition + PAGE_SIZE);
        this.setColumnIdentifiers(new String[]{
            "Nr seryjny", "Dostawca", "Data dostawy",
            "Nr faktury zakupu", "Odbiorca", "Data sprzedaży",
            "Nr faktury sprzedaży", "Id"
        });
    }

    protected final void getItems(int from, int to) {

        int rowCounter = from;
        if (this.cache.size() > CACHE_SIZE) {
            this.cache.clear();
        }
        if (!this.inCache(from, to)) {
            for (Object[] row : this.dao.fetchTableRows(to - from, from, this.query, this.orderBy, this.orderDesc, this.serials)) {
                this.cache.put(rowCounter, row);
                rowCounter++;
            }
        }
    }

    private boolean inCache(int from, int to) {
        for (int i = from; i <= to; i++) {
            if (!cache.containsKey(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (!this.cache.containsKey(rowIndex)) {
            this.getItems(rowIndex, rowIndex + PAGE_SIZE);
        }
        Object[] row = this.cache.get(rowIndex);
        if(row != null) {
            return row[columnIndex];
        } else {
            return "";
        }
        

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
