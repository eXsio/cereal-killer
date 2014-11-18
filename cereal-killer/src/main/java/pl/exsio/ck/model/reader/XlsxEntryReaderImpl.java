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
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.EntryImpl;
import pl.exsio.ck.progress.presenter.ProgressPresenter;

public class XlsxEntryReaderImpl implements EntryReader {

    private ProgressPresenter progress;

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
    public Collection<Entry> readEntries(File file, String progressName) {
        this.showProgressBar(progressName);
        Row currentRow = null;
        Cell currentCell = null;
        ArrayList<Entry> entries = new ArrayList<>();
        try {
            XSSFSheet sheet = this.openSheet(file);

            Iterator<Row> rowIterator = sheet.iterator();
            this.updateProgressBar(0, sheet.getPhysicalNumberOfRows() - 1);
            int rowCounter = 0;

            while (rowIterator.hasNext()) {
                this.updateProgressBar(rowCounter, sheet.getPhysicalNumberOfRows() - 1);
                currentRow = rowIterator.next();
                if (currentRow.getRowNum() > 0) {
                    Entry e = new EntryImpl();
                    Iterator<Cell> cellIterator = currentRow.cellIterator();
                    while (cellIterator.hasNext()) {
                        currentCell = cellIterator.next();
                        if (!this.fillEntryField(currentCell, e)) {
                            break;
                        }
                    }
                    entries.add(e);
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
        this.hideProgressBar();
        return entries;
    }

    private XSSFSheet openSheet(File file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet;
    }

    private boolean fillEntryField(Cell currentCell, Entry e) throws ParseException {
        String value = this.getStringValue(currentCell);
        if (value != null && !value.equals("")) {
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
            return true;
        } else {
            return false;
        }

    }

    private void showProgressBar(String progressName) {
        if (this.progress != null) {
            this.progress.hide();
        }
        this.progress = (ProgressPresenter) App.getContext().getBean("progressPresenter");
        this.progress.setProgressName(progressName);
        this.progress.show();
    }

    private void updateProgressBar(int count, int max) {
        this.progress.setProgress((int) (count * 100 / max));
    }

    private void hideProgressBar() {
        this.progress.hide();
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

    public void setProgress(ProgressPresenter progress) {
        this.progress = progress;
    }

}
