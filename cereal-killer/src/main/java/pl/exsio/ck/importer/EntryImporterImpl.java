package pl.exsio.ck.importer;

import java.io.File;
import java.util.Collection;
import pl.exsio.ck.logging.presenter.LogPresenter;
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
        Collection<Entry> entries = this.reader.readEntries(file, (updateEnabled ? "aktualizacja" : "import") + " w toku...");
        if (!entries.isEmpty()) {
            this.dao.save(entries, updateEnabled);
            this.log.log("Zaimportowano "+entries.size()+" wpisów");
        }
        this.log.log((updateEnabled ? "aktualizacja zakończona" : "import zakończony"));

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
