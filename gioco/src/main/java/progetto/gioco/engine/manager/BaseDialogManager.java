package progetto.gioco.engine.manager;

import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.model.BaseDialogo;
import progetto.gioco.engine.model.BaseScelta;

public abstract class BaseDialogManager extends BaseManager {
    protected BaseDialogo dialogoCorrente;

    public abstract void startDialogo(BaseAtto atto);

    public abstract BaseDialogo getDialogo();

    public abstract BaseScelta scegliOpzione(int scelta);
}
