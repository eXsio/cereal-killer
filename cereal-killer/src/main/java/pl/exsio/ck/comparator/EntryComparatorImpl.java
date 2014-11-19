package pl.exsio.ck.comparator;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entries;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.model.reader.EntryReader;

public class EntryComparatorImpl implements EntryComparator {

    private LogPresenter log;

    private EntryDao dao;

    private EntryReader reader;

    @Override
    public ComparisonResult compareFile(File file) {
        log.log("rozpoczęto porównywanie");
        String[] serials = this.getSerialNumbersFromFile(file);
        final String[] found = this.getFoundSerialNumbers(serials);
        final String[] notFound = this.getNotFoundSerialNumbers(serials, found);
        log.log("porównywanie zakończone");
        log.log("znaleziono: " + found.length);
        log.log("nieznaleziono: " + notFound.length);
        return new ComparisonResultImpl(found, notFound);
    }

    private String[] getSerialNumbersFromFile(File file) {
        List<String> serials = new LinkedList<>();
        Collection<Entry> entries = this.reader.readEntries(file, "odczytywanie numerów seryjnych...", true);
        return Entries.getSerials(entries);
    }

    private String[] getFoundSerialNumbers(String[] serials) {
        return this.dao.findExistingSerialsBy(serials);
    }

    private String[] getNotFoundSerialNumbers(String[] serials, String[] foundSerials) {
        List<String> notFound = new LinkedList<>();
        for (String serial : serials) {
            boolean found = false;
            for (String foundSerial : foundSerials) {
                if (foundSerial.equals(serial)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                notFound.add(serial);
            }
        }
        return notFound.toArray(new String[notFound.size()]);
    }

    public void setLog(LogPresenter log) {
        this.log = log;
    }

    public void setDao(EntryDao dao) {
        this.dao = dao;
    }

    public void setReader(EntryReader reader) {
        this.reader = reader;
    }

}
