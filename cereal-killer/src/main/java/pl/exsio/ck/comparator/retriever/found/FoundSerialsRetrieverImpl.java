package pl.exsio.ck.comparator.retriever.found;

import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.dao.EntryDao;

public class FoundSerialsRetrieverImpl implements FoundSerialsRetriever {

    private LogPresenter log;

    private EntryDao dao;

    @Override
    public String[] getFoundSerialNumbers(String[] serials) {
        this.log.log("przetwarzam znalezione");
        return this.dao.matchSerials(serials);
    }

    public void setLog(LogPresenter log) {
        this.log = log;
    }

    public void setDao(EntryDao dao) {
        this.dao = dao;
    }

}
