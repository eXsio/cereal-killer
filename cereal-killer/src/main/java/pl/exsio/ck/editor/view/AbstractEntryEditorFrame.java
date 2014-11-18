package pl.exsio.ck.editor.view;

import java.util.Map;
import pl.exsio.ck.editor.presenter.EntryEditorPresenter;
import pl.exsio.ck.view.AbstractFrame;

/**
 *
 * @author exsio
 */
public abstract class AbstractEntryEditorFrame extends AbstractFrame {

    protected EntryEditorPresenter presenter;

    public void setPresenter(EntryEditorPresenter presenter) {
        this.presenter = presenter;
    }
    
    public abstract Map<String, Object> getValues();

}
