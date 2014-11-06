
package pl.exsio.ck.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.model.Entry;

public class EntryImporterImpl implements EntryImporter {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public void importFile(File file, boolean updateEnabled) {
        
        Row currentRow = null;
        Cell currentCell = null;
        try {
            App.log("rozpoczynam " + (updateEnabled ? "aktualizację" : "import"));
            this.showProgressBar(updateEnabled);
            XSSFSheet sheet = this.openSheet(file);
            Iterator<Row> rowIterator = sheet.iterator();
            List<Entry> entries = new LinkedList<>();
            int rowCounter = 0;
            while (rowIterator.hasNext()) {
                this.updateProgressBar(rowCounter, sheet.getPhysicalNumberOfRows()-1);
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
            App.getEntryDao().save(entries, updateEnabled);
            App.log((updateEnabled ? "aktualizacja zakończona" : "import zakończony") + " powodzeniem");
        } catch (IOException ex) {
            App.log("nieudana próba otwarcia pliku " + file.getAbsolutePath());
            App.log(ExceptionUtils.getMessage(ex));
        } catch (ParseException ex) {
            App.log("nieprawidłowy format daty w komórce " + currentRow.getRowNum()
                    + CellReference.convertNumToColString(currentCell.getColumnIndex())
                    + ". Akceptowalny format to 'yyyy-mm-dd'");
            App.log(ExceptionUtils.getMessage(ex));
        }
        this.hideProgressBar();
        
    }
    
    private void showProgressBar(boolean updateEnabled) {
        
        App.getProgress().setProgressName((updateEnabled ? "aktualizacja" : "import") + " w toku...");
        App.getProgress().setVisible(true);
    }
    
    private void updateProgressBar(int count, int max) {
        App.getProgress().setProgress((int)(count*100/max));
    }
    
    private void hideProgressBar() {
        App.getProgress().setVisible(false);
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
    
}