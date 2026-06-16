package progetto.gioco.engine.manager;

import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.model.BaseDialogo;
import progetto.gioco.engine.model.BaseScelta;

public abstract class BaseDialogManager extends BaseManager {
    protected BaseDialogo dialogoCorrente;
    protected BaseAtto atto;

    public abstract void setAtto(BaseAtto atto);

    public abstract void startDialogo(String idDialogo);

    public abstract BaseDialogo getDialogo();

    public abstract BaseScelta scegliOpzione(int scelta);
}
