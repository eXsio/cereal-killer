
package pl.exsio.ck.comparator;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTabbedPane;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.exsio.ck.browser.view.BrowserFrame;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenter;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenterImpl;
import pl.exsio.ck.entrytable.view.EntryTablePanel;
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.serialtable.presenter.SerialTablePresenter;
import pl.exsio.ck.serialtable.presenter.SerialTablePresenterImpl;
import pl.exsio.ck.serialtable.view.SerialTablePanel;

public class EntryComparatorImpl implements EntryComparator {

    @Override
    public void compareFile(File file) {
        List<String> serials = getSerialNumbersFromFile(file);
        final Collection<Entry> entries = App.getEntryDao().findBySerialNos(serials.toArray(new String[serials.size()]));
        final List<String> notFound = this.getNotFoundSerialNumbers(serials, entries);
        this.showCompareWindow(entries, notFound);
    }

    private void showCompareWindow(final Collection<Entry> entries, final List<String> notFound) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BrowserFrame browser = new BrowserFrame();
                JTabbedPane tabs = new JTabbedPane();

                EntryTablePanel foundPanel = new EntryTablePanel();
                EntryTablePresenter entryPresenter = new EntryTablePresenterImpl(foundPanel);
                foundPanel.setPresenter(entryPresenter);
                foundPanel.showEntries(entries);

                SerialTablePanel notFoundPanel = new SerialTablePanel();
                SerialTablePresenter serialPresenter = new SerialTablePresenterImpl(notFoundPanel);
                notFoundPanel.setPresenter(serialPresenter);
                notFoundPanel.showSerials(notFound);

                browser.setLayout(new BorderLayout());
                tabs.add("Znalezione", foundPanel);
                tabs.add("Nieznalezione", notFoundPanel);
                browser.add(tabs);
                browser.setTitle("Porównaj wpisy");
                browser.pack();
                browser.showOnScreen(0);
                browser.setVisible(true);
            }
        });
    }

    private List<String> getSerialNumbersFromFile(File file) {
        List<String> serials = new LinkedList<>();
        Row currentRow = null;
        Cell currentCell = null;
        try {
            XSSFSheet sheet = this.openSheet(file);
            Iterator<Row> rowIterator = sheet.iterator();

            int rowCounter = 0;
            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                if (currentRow.getRowNum() > 0) {
                    Entry e = new Entry();
                    Iterator<Cell> cellIterator = currentRow.cellIterator();
                    while (cellIterator.hasNext()) {
                        currentCell = cellIterator.next();
                        switch (currentCell.getColumnIndex()) {
                            case 0:
                                serials.add(getStringValue(currentCell));
                                break;
                            default:
                                break;
                        }
                    }
                }
                rowCounter++;
            }
        } catch (IOException ex) {
            App.log("nieudana próba otwarcia pliku " + file.getAbsolutePath());
            App.log(ExceptionUtils.getMessage(ex));
        }
        return serials;
    }

    private XSSFSheet openSheet(File file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet;
    }

    private static String getStringValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_STRING:
            default:
                return cell.getStringCellValue();
        }
    }

    private List<String> getNotFoundSerialNumbers(List<String> serials, Collection<Entry> entries) {
        List<String> notFound = new LinkedList<>();
        for (String serial : serials) {
            boolean found = false;
            for (Entry e : entries) {
                if (e.getSerialNo().equals(serial)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                notFound.add(serial);
            }
        }
        return notFound;
    }

}
