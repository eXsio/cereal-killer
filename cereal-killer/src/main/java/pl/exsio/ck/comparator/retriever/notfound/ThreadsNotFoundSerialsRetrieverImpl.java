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
package pl.exsio.ck.comparator.retriever.notfound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.util.ArrayUtil;

public class ThreadsNotFoundSerialsRetrieverImpl implements NotFoundSerialsRetriever {

    protected LogPresenter log;

    @Override
    public String[] getNotFoundSerialNumbers(String[] serials, String[] foundSerials) {
        this.log.log("przetwarzam nieznalezione");
        List<String[]> serialsChunks = this.getAndLogSerialsChunks(serials);
        final List<String> notFoundList = new ArrayList();
        List<Thread> threads = this.createAndStartWorkerThreads(foundSerials, serialsChunks, notFoundList);
        this.joinWorkerThreads(threads);
        return notFoundList.toArray(new String[notFoundList.size()]);
    }

    protected List<Thread> createAndStartWorkerThreads(String[] foundSerials, List<String[]> serialsList, final List<String> notFoundList) {
        List<Thread> threads = new ArrayList<>();
        final List<String> foundList = Arrays.asList(foundSerials);
        serialsList.stream().map((serialsChunk) -> this.createWorkerThread(serialsChunk, foundList, notFoundList)).map((t) -> {
            threads.add(t);
            return t;
        }).forEach((t) -> {
            t.start();
        });
        return threads;
    }

    protected Thread createWorkerThread(final String[] serialsChunk, final List<String> foundList, final List<String> notFoundList) {
        Thread t = new Thread(() -> {
            List<String> workerList = new ArrayList<>(Arrays.asList(serialsChunk));
            workerList.removeAll(Collections.singleton(null));
            workerList.removeAll(foundList);
            notFoundList.addAll(workerList);
        });

        return t;
    }

    protected void joinWorkerThreads(List<Thread> threads) {
        threads.stream().forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException ex) {
                this.log.log("podczas porównywania wystąpił błąd");
                this.log.log(ExceptionUtils.getMessage(ex));
            }
        });
    }

    protected List<String[]> getAndLogSerialsChunks(String[] serials) {
        int chunkSize = this.computeChunkSize(serials);
        List<String[]> serialsList = ArrayUtil.splitArray(serials, chunkSize);
        this.log.log("rozpoczynam " + serialsList.size() + " wątki/ów, każdy po " + chunkSize + " elementów do przeanalizowania");
        return serialsList;
    }

    protected int computeChunkSize(String[] serials) {
        return (int) Math.ceil(serials.length / this.getAvailableCpusCount());
    }

    protected int getAvailableCpusCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public void setLog(LogPresenter log) {
        this.log = log;
    }

}
