package pl.exsio.ck.comparator;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.model.reader.EntryReader;
import pl.exsio.ck.util.ArrayUtil;

public class EntryComparatorImpl implements EntryComparator {

    private LogPresenter log;

    private EntryDao dao;

    private EntryReader reader;

    private final static int LOOKUP_PAGE_SIZE = 200;

    @Override
    public ComparisonResult compareFile(File file) {
        log.log("rozpoczęto porównywanie");
        List<String> serials = this.getSerialNumbersFromFile(file);
        Collection<Entry> entries = this.lookupEntries(serials);
        final List<String> notFound = this.getNotFoundSerialNumbers(serials, entries);
        log.log("porównywanie zakończone");
        log.log("znaleziono: "+entries.size());
        log.log("nieznaleziono: "+notFound.size());
        return new ComparisonResultImpl(entries, notFound);
    }

    private List<String> getSerialNumbersFromFile(File file) {
        List<String> serials = new LinkedList<>();
        Collection<Entry> entries = this.reader.readEntries(file, "odczytywanie numerów seryjnych...");
        if (!entries.isEmpty()) {
            for (Entry entry : entries) {
                serials.add(entry.getSerialNo());
            }
        }
        return serials;
    }

    private Collection<Entry> lookupEntries(List<String> serials) {
        String[] serialsArr = serials.toArray(new String[serials.size()]);
        LinkedHashSet<Entry> entries = new LinkedHashSet<>();
        for (String[] chunk : ArrayUtil.splitArray(serialsArr, LOOKUP_PAGE_SIZE)) {
            entries.addAll(this.dao.findBySerialNos(chunk));
        }
        return entries;
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
