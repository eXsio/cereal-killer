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
package pl.exsio.ck.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pl.exsio.ck.editor.presenter.EntryEditorPresenter;
import pl.exsio.ck.editor.presenter.EntryEditorPresenter.CancelListener;
import pl.exsio.ck.editor.presenter.EntryEditorPresenter.SaveListener;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.model.reader.EntryReader;

/**
 *
 * @author exsio
 */
public class EntryImporterImpl implements EntryImporter {

    private LogPresenter log;

    private EntryDao dao;

    private EntryReader reader;

    @Override
    public void importFile(File file, boolean updateEnabled) {

        this.log.log("rozpoczynam " + (updateEnabled ? "aktualizację" : "import"));
        Collection<Entry> entries = this.reader.readEntries(file, (updateEnabled ? "aktualizacja" : "import") + " w toku...", false);
        if (entries != null && !entries.isEmpty()) {
            this.verifyAndSaveEntries(entries, updateEnabled);
        }

    }

    protected void verifyAndSaveEntries(Collection<Entry> entries, final boolean updateEnabled) {
        final EntryVerificationResult result = this.verifyEntries(entries);
        if (result.getEmpty().length == 0) {
            this.saveEntries(entries, updateEnabled);
        } else {
            this.showEntryEditor(updateEnabled, result);
        }
    }

    protected EntryVerificationResult verifyEntries(Collection<Entry> entries) {
        List<String> empty = new ArrayList<>();
        List<Entry> filled = new ArrayList<>();
        entries.stream().forEach((e) -> {
            if (!e.isDataFilled()) {
                empty.add(e.getSerialNo());
            } else {
                filled.add(e);
            }
        });
        return new EntryVerificationResult(empty.toArray(new String[empty.size()]), filled);
    }

    private void saveEntries(Collection<Entry> entries, final boolean updateEnabled) {
        this.dao.save(entries, updateEnabled);
        this.log.log((updateEnabled ? "aktualizacja zakończona" : "import zakończony"));
    }

    private void showEntryEditor(final boolean updateEnabled, final EntryVerificationResult result) {
        EntryEditorPresenter editor = this.getEntryEditor();
        editor.show(result.getEmpty(), (String[] serials, Entry dataPattern) -> {
            dao.saveSerials(serials, dataPattern, updateEnabled);
            Collection<Entry> filled = result.getFilled();
            if (!filled.isEmpty()) {
                dao.save(filled, updateEnabled);
            }
            log.log((updateEnabled ? "aktualizacja zakończona" : "import zakończony"));
        }, () -> {
            log.log((updateEnabled ? "aktualizacja anulowana" : "import anulowany") + " przez użytkownika");
        });
    }

    protected EntryEditorPresenter getEntryEditor() {
        return (EntryEditorPresenter) App.getContext().getBean("entryEditorPresenter");
    }

    protected class EntryVerificationResult {

        private final String[] empty;

        private final Collection<Entry> filled;

        public EntryVerificationResult(String[] empty, Collection<Entry> filled) {
            this.empty = empty;
            this.filled = filled;
        }

        public String[] getEmpty() {
            return empty;
        }

        public Collection<Entry> getFilled() {
            return filled;
        }

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
