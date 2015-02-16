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
package pl.exsio.ck.serialtable.presenter;

import java.awt.Container;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import pl.exsio.ck.serialtable.view.AbstractSerialTablePanel;

/**
 *
 * @author exsio
 */
public class SerialTablePresenterImpl implements SerialTablePresenter {

    private AbstractSerialTablePanel view;

    private TableRowSorter<TableModel> sorter;

    @Override
    public void setView(AbstractSerialTablePanel view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void filter(String filterStr) {
        if (this.sorter != null) {
            if (filterStr.trim().length() == 0) {
                this.sorter.setRowFilter(null);
            } else {
                this.sorter.setRowFilter(RowFilter.regexFilter(filterStr.trim()));
            }
        }
    }

    @Override
    public void showSerials(final String[] serials) {

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                JTable table = view.getEntryTable();
                DefaultTableModel tm = this.createTableModel();
                this.fillTableData(tm);
                table.setModel(tm);
                sorter = new TableRowSorter<>(tm);
                table.setRowSorter(sorter);
                return null;
            }

            private void fillTableData(DefaultTableModel tm) {
                for (String s : serials) {
                    tm.addRow(new Object[]{s});
                }
                tm.setRowCount(serials.length);
            }

            private DefaultTableModel createTableModel() {
                DefaultTableModel tm = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                setTableHeaders(tm);
                return tm;
            }

            private void setTableHeaders(DefaultTableModel tm) {
                tm.setColumnIdentifiers(new String[]{
                    "Nr seryjny"
                });
            }

        };
        worker.execute();

    }

    @Override
    public Container getView() {
        return this.view;
    }

}
