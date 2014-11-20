package pl.exsio.ck.comparator;

import pl.exsio.ck.comparator.result.ComparisonResult;
import pl.exsio.ck.comparator.result.ComparisonResultImpl;
import java.io.File;
import java.util.Collection;
import pl.exsio.ck.comparator.retriever.found.FoundSerialsRetriever;
import pl.exsio.ck.comparator.retriever.notfound.NotFoundSerialsRetriever;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entries;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.reader.EntryReader;
import pl.exsio.ck.progress.presenter.ProgressHelper;
import pl.exsio.ck.progress.presenter.ProgressPresenter;

/**
 *
 * @author exsio
 */
public class EntryComparatorImpl implements EntryComparator {

    private LogPresenter log;

    protected FoundSerialsRetriever foundRetriever;

    protected NotFoundSerialsRetriever notFoundRetriever;

    private EntryReader reader;

    @Override
    public ComparisonResult compareFile(File file) {
        log.log("rozpoczęto porównywanie");
        String[] serials = this.getSerialNumbersFromFile(file);
        ProgressPresenter progress = ProgressHelper.showProgressBar("porównuję wczytane dane z danymi w bazie...", true);
        final String[] found = this.getFoundSerialNumbers(serials);
        final String[] notFound = this.getNotFoundSerialNumbers(serials, found);
        log.log("porównywanie zakończone");
        log.log("znaleziono: " + found.length);
        log.log("nieznaleziono: " + notFound.length);
        ProgressHelper.hideProgressBar(progress);
        return new ComparisonResultImpl(found, notFound);
    }

    private String[] getSerialNumbersFromFile(File file) {
        Collection<Entry> entries = this.reader.readEntries(file, "odczytywanie numerów seryjnych...", true);
        return Entries.getSerials(entries);
    }

    private String[] getFoundSerialNumbers(String[] serials) {
        return this.foundRetriever.getFoundSerialNumbers(serials);
    }

    private String[] getNotFoundSerialNumbers(String[] serials, String[] foundSerials) {
        return this.notFoundRetriever.getNotFoundSerialNumbers(serials, foundSerials);
    }

    public void setLog(LogPresenter log) {
        this.log = log;
    }

    public void setReader(EntryReader reader) {
        this.reader = reader;
    }

    public void setFoundRetriever(FoundSerialsRetriever foundRetriever) {
        this.foundRetriever = foundRetriever;
    }

    public void setNotFoundRetriever(NotFoundSerialsRetriever notFoundRetriever) {
        this.notFoundRetriever = notFoundRetriever;
    }

}
