package pl.exsio.ck.comparator.retriever.notfound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.util.ArrayUtil;

public class NotFoundSerialsRetrieverImpl implements NotFoundSerialsRetriever {

    protected LogPresenter log;

    @Override
    public String[] getNotFoundSerialNumbers(String[] serials, String[] foundSerials) {
        this.log.log("przetwarzam nieznalezione");
        List<String[]> serialsChunks = this.getAndLogSerialsChunks(serials);
        final List<String> notFoundList = new ArrayList();
        List<Thread> threads = this.start(foundSerials, serialsChunks, notFoundList);
        this.join(threads);
        return notFoundList.toArray(new String[notFoundList.size()]);
    }

    protected List<Thread> start(String[] foundSerials, List<String[]> serialsList, final List<String> notFoundList) {
        List<Thread> threads = new ArrayList<>();
        final List<String> foundList = Arrays.asList(foundSerials);
        for (final String[] serialsChunk : serialsList) {
            Thread t = this.createWorkerThread(serialsChunk, foundList, notFoundList);
            threads.add(t);
            t.start();
        }
        return threads;
    }

    private Thread createWorkerThread(final String[] serialsChunk, final List<String> foundList, final List<String> notFoundList) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                List<String> workerList = new ArrayList<>(Arrays.asList(serialsChunk));
                workerList.removeAll(Collections.singleton(null));
                workerList.removeAll(foundList);
                notFoundList.addAll(workerList);
            }
        });

        return t;
    }

    protected void join(List<Thread> threads) {
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                this.log.log("podczas porównywania wystąpił błąd");
                this.log.log(ExceptionUtils.getMessage(ex));
            }
        }
    }

    protected List<String[]> getAndLogSerialsChunks(String[] serials) {
        int chunkSize = this.computeChunkSize(serials);
        List<String[]> serialsList = ArrayUtil.splitArray(serials, chunkSize);
        this.log.log("rozpoczynam " + serialsList.size() + " wątki/ów, każdy po " + chunkSize + " elementów do przeanalizowania");
        return serialsList;
    }

    protected int computeChunkSize(String[] serials) {
        return (int) Math.ceil(serials.length / getAvailableCpusCount());
    }

    protected int getAvailableCpusCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public void setLog(LogPresenter log) {
        this.log = log;
    }

}
