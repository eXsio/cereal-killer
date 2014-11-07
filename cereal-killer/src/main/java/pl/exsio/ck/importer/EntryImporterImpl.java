package pl.exsio.ck.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.progress.presenter.ProgressPresenter;

public class EntryImporterImpl implements EntryImporter {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private LogPresenter log;

    private ProgressPresenter progress;

    private EntryDao dao;

    @Override
    public void importFile(File file, boolean updateEnabled) {

        Row currentRow = null;
        Cell currentCell = null;
        try {
            this.log.log("rozpoczynam " + (updateEnabled ? "aktualizację" : "import"));
            this.showProgressBar(updateEnabled);
            XSSFSheet sheet = this.openSheet(file);
            Iterator<Row> rowIterator = sheet.iterator();
            this.updateProgressBar(0, sheet.getPhysicalNumberOfRows() - 1);
            int rowCounter = 0;
            ArrayList<Entry> entries = new ArrayList<>();
            while (rowIterator.hasNext()) {
                this.updateProgressBar(rowCounter, sheet.getPhysicalNumberOfRows() - 1);
                currentRow = rowIterator.next();
                if (currentRow.getRowNum() > 0) {
                    Entry e = new Entry();
                    Iterator<Cell> cellIterator = currentRow.cellIterator();
                    while (cellIterator.hasNext()) {
                        currentCell = cellIterator.next();
                        this.fillEntry(currentCell, e);
                    }
                    entries.add(e);
                }
                rowCounter++;
            }
            this.dao.save(entries, updateEnabled);

            this.log.log((updateEnabled ? "aktualizacja zakończona" : "import zakończony") + " powodzeniem");
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

    }

    private void showProgressBar(boolean updateEnabled) {

        this.progress.setProgressName((updateEnabled ? "aktualizacja" : "import") + " w toku...");
        this.progress.getView().setVisible(true);
    }

    private void updateProgressBar(int count, int max) {
        this.progress.setProgress((int) (count * 100 / max));
    }

    private void hideProgressBar() {
        this.progress.getView().setVisible(false);
    }

    private XSSFSheet openSheet(File file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet;
    }

    private void fillEntry(Cell currentCell, Entry e) throws ParseException {
        switch (currentCell.getColumnIndex()) {
            case 0:
                e.setSerialNo(getStringValue(currentCell));
                break;
            case 1:
                e.setSupplier(getStringValue(currentCell));
                break;
            case 2:
                e.setSupplyDate(sdf.parse(getStringValue(currentCell)));
                break;
            case 3:
                e.setBuyInvoiceNo(getStringValue(currentCell));
                break;
            case 4:
                e.setRecipient(getStringValue(currentCell));
                break;
            case 5:
                e.setSellDate(sdf.parse(getStringValue(currentCell)));
                break;
            case 6:
                e.setSellInvoiceNo(getStringValue(currentCell));
                break;
            default:
                break;
        }
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

    public void setLog(LogPresenter log) {
        this.log = log;
    }

    public void setProgress(ProgressPresenter progress) {
        this.progress = progress;
    }

    public void setDao(EntryDao dao) {
        this.dao = dao;
    }

}
