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
package pl.exsio.ck.model.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.EntryImpl;
import pl.exsio.ck.progress.presenter.ProgressHelper;
import pl.exsio.ck.progress.presenter.ProgressPresenter;

/**
 *
 * @author exsio
 */
public class XlsxEntryReaderImpl implements EntryReader {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private LogPresenter log;

    @Override
    public Map<String, String> getAcceptedFormats() {
        return new LinkedHashMap() {
            {
                put("xlsx", "Arkusze MS Excel");
            }
        };
    }

    @Override
    public Collection<Entry> readEntries(File file, String progressName, boolean serialsOnly) {
        ProgressPresenter progress = ProgressHelper.showProgressBar(progressName, false);
        Row currentRow = null;
        Cell currentCell = null;
        ArrayList<Entry> entries = new ArrayList<>();
        try {
            XSSFSheet sheet = this.openSheet(file);

            Iterator<Row> rowIterator = sheet.iterator();
            int totalRowCount = sheet.getPhysicalNumberOfRows() - 1;
            int rowCounter = 0;
            while (rowIterator.hasNext()) {
                ProgressHelper.updateProgressBar(progress, (int) (rowCounter * 100 / totalRowCount));
                currentRow = rowIterator.next();
                if (currentRow.getRowNum() > 0) {
                    Entry e = new EntryImpl();
                    Iterator<Cell> cellIterator = currentRow.cellIterator();
                    while (cellIterator.hasNext()) {
                        currentCell = cellIterator.next();
                        if (!this.fillEntryField(currentCell, e, serialsOnly)) {
                            break;
                        }
                    }
                    if (e.getSerialNo() != null) {
                        entries.add(e);
                    }
                }
                rowCounter++;
            }
        } catch (IOException ex) {
            this.log.log("nieudana próba otwarcia pliku " + file.getAbsolutePath());
            this.log.log(ExceptionUtils.getMessage(ex));
        } catch (ParseException ex) {
            this.log.log("nieprawidłowy format daty w komórce " + currentRow.getRowNum()
                    + CellReference.convertNumToColString(currentCell.getColumnIndex())
                    + ". Akceptowalny format to 'yyyy-mm-dd'");
            this.log.log(ExceptionUtils.getMessage(ex));
        }
        System.gc();
        ProgressHelper.hideProgressBar(progress);
        return entries;
    }

    private XSSFSheet openSheet(File file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet;
    }

    private boolean fillEntryField(Cell currentCell, Entry e, boolean serialsOnly) throws ParseException {
        String value = this.getStringValue(currentCell);
        if (value != null && !value.equals("")) {
            if (serialsOnly) {
                if (currentCell.getColumnIndex() == 0) {
                    e.setSerialNo(value);
                }
            } else {
                switch (currentCell.getColumnIndex()) {
                    case 0:
                        e.setSerialNo(value);
                        break;
                    case 1:
                        e.setSupplier(value);
                        break;
                    case 2:
                        e.setSupplyDate(sdf.parse(value));
                        break;
                    case 3:
                        e.setBuyInvoiceNo(value);
                        break;
                    case 4:
                        e.setRecipient(value);
                        break;
                    case 5:
                        e.setSellDate(sdf.parse(value));
                        break;
                    case 6:
                        e.setSellInvoiceNo(value);
                        break;
                    default:
                        break;
                }
            }

            return true;
        } else {
            return false;
        }

    }

    private String getStringValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_STRING:
            default:
                return cell.getStringCellValue();
        }
    }

    public void setLog(LogPresenter log) {
        this.log = log;
    }

}
