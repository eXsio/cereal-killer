/* 
 * The MIT License
 *
 * Copyright 2015 exsio.
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
