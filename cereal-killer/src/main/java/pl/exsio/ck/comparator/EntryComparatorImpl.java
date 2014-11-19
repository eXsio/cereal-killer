package pl.exsio.ck.comparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.model.Entries;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.model.reader.EntryReader;
import pl.exsio.ck.progress.presenter.ProgressPresenter;
import pl.exsio.ck.util.ArrayUtil;

public class EntryComparatorImpl implements EntryComparator {

    private LogPresenter log;

    private EntryDao dao;

    private EntryReader reader;

    protected ProgressPresenter progress;

    @Override
    public ComparisonResult compareFile(File file) {
        log.log("rozpoczęto porównywanie");
        String[] serials = this.getSerialNumbersFromFile(file);
        this.showProgressBar("porównuję wczytane dane z danymi w bazie...");
        final String[] found = this.getFoundSerialNumbers(serials);
        final String[] notFound = this.getNotFoundSerialNumbers(serials, found);
        log.log("porównywanie zakończone");
        log.log("znaleziono: " + found.length);
        log.log("nieznaleziono: " + notFound.length);
        this.hideProgressBar();
        return new ComparisonResultImpl(found, notFound);
    }

    private String[] getSerialNumbersFromFile(File file) {
        Collection<Entry> entries = this.reader.readEntries(file, "odczytywanie numerów seryjnych...", true);
        return Entries.getSerials(entries);
    }

    private String[] getFoundSerialNumbers(String[] serials) {
        this.log.log("przetwarzam znalezione");
        return this.dao.matchSerials(serials);
    }

    private String[] getNotFoundSerialNumbers(String[] serials, String[] foundSerials) {
        this.log.log("przetwarzam nieznalezione");
        int processors = Runtime.getRuntime().availableProcessors();
        int chunkSize = (int) Math.ceil(serials.length / processors);
        List<String[]> serialsList = ArrayUtil.splitArray(serials, chunkSize);
        final List<String> notFoundList = new ArrayList();
        List<Thread> threads = new ArrayList<>();
        final List<String> foundList = Arrays.asList(foundSerials);
        this.log.log("rozpoczynam " + processors + " wątki/ów, każdy po " + chunkSize + " elementów do przeanalizowania");
        for (final String[] serialsChunk : serialsList) {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    List<String> workerList = new ArrayList<>(Arrays.asList(serialsChunk));
                    workerList.removeAll(Collections.singleton(null));
                    workerList.removeAll(foundList);
                    notFoundList.addAll(workerList);
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                this.log.log("podczas porównywania wystąpił błąd");
                this.log.log(ExceptionUtils.getMessage(ex));
            }
        }

        return notFoundList.toArray(new String[notFoundList.size()]);
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

    protected void showProgressBar(final String progressName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (progress != null) {
                    progress.hide();
                }
                progress = (ProgressPresenter) App.getContext().getBean("progressPresenter");
                progress.setProgressName(progressName);
                progress.show(true);
            }
        }).start();

    }

    protected void hideProgressBar() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                progress.hide();
            }
        }).start();
    }

}
