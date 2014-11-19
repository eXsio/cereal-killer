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
        String[] empty = this.getEmptyEntries(entries);
        if (empty.length == 0) {
            doSaveEntries(entries, updateEnabled);
        } else {
            EntryEditorPresenter editor = this.getEntryEditor();
            editor.show(empty, new SaveListener() {

                @Override
                public void saveEntries(Collection<Entry> entries) {
                    doSaveEntries(entries, updateEnabled);
                }
            }, new CancelListener() {

                @Override
                public void cancelEdition() {
                    log.log((updateEnabled ? "aktualizacja anulowana" : "import anulowany") + " przez użytkownika");
                }
            });
        }
    }

    protected void doSaveEntries(Collection<Entry> entries, final boolean updateEnabled) {
        this.dao.save(entries, updateEnabled);
        this.log.log((updateEnabled ? "aktualizacja zakończona" : "import zakończony"));
    }

    protected EntryEditorPresenter getEntryEditor() {
        return (EntryEditorPresenter) App.getContext().getBean("entryEditorPresenter");
    }

    protected String[] getEmptyEntries(Collection<Entry> entries) {
        List<String> empty = new ArrayList<>();
        for (Entry e : entries) {
            if (!e.isDataFilled()) {
                empty.add(e.getSerialNo());
            }
        }
        return empty.toArray(new String[empty.size()]);
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
