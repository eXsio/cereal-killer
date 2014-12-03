/* 
 * The MIT License
 *
 * Copyright 2014 exsio.
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

import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import pl.exsio.ck.entrytable.view.AbstractEntryTablePanel;
import pl.exsio.ck.model.dao.EntryDao;

/**
 *
 * @author exsio
 */
public class EntryTablePresenterImpl implements EntryTablePresenter {

    private AbstractEntryTablePanel view;

    protected EntryDao dao;

    protected String query;

    protected Map<Integer, String> sortMap = new HashMap() {
        {
            put(0, "serial_no");
            put(1, "supplier");
            put(2, "supply_date");
            put(3, "buy_invoice_no");
            put(4, "recipient");
            put(5, "sell_date");
            put(6, "sell_invoice_no");
            put(7, "id");
        }
    };

    protected String sortDir = EntryDao.SORT_ASC;

    @Override
    public void setView(AbstractEntryTablePanel view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void filter(String query) {

        if (query == null || query.trim().length() == 0) {
            this.query = null;
        } else {
            this.query = query;
        }
        this.showData("id", sortDir, null);
        
    }

    @Override
    public void showEntries() {
        this.showEntries(null);
    }

    @Override
    public void showEntries(final String[] serials) {

        this.showData("id", sortDir, serials);

    }

    private void showData(final String orderBy, final String orderDir, final String[] serials) {

        final JTable table = view.getEntryTable();
        final EntryTableModel tm = new EntryTableModel(dao, orderBy, orderDir, query, serials);
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                table.setModel(tm);
                table.setRowSorter(new TableRowSorter<TableModel>(tm) {
                    @Override
                    public void toggleSortOrder(int column) {
                        if (sortMap.containsKey(column)) {
                            String dir;
                            if (sortDir.equals(EntryDao.SORT_ASC)) {
                                dir = EntryDao.SORT_DESC;
                            } else {
                                dir = EntryDao.SORT_ASC;
                            }
                            sortDir = dir;
                            showData(sortMap.get(column), dir, serials);
                        }
                    }
                });
                System.gc();
                return null;
            }
        };
        worker.execute();
    }

    @Override

    public Container getView() {
        return this.view;
    }

    public void setDao(EntryDao dao) {
        this.dao = dao;
    }

}
