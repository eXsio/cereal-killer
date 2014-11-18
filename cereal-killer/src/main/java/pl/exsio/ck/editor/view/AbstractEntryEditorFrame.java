package pl.exsio.ck.editor.view;

import java.util.Map;
import javax.swing.JFrame;
import pl.exsio.ck.editor.presenter.EntryEditorPresenter;

/**
 *
 * @author exsio
 */
public abstract class AbstractEntryEditorFrame extends JFrame {

    protected EntryEditorPresenter presenter;

    public void setPresenter(EntryEditorPresenter presenter) {
        this.presenter = presenter;
    }
    
    public abstract Map<String, Object> getValues();

}
