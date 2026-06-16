package progetto.gioco.game.model;

import java.util.Map;

import progetto.gioco.engine.model.BaseAtto;

public class Atto extends BaseAtto{
    public Atto(String idAtto, String dialogoIniziale) {
        super(idAtto, dialogoIniziale);

    }

    @Override
    public Map<String, String> getDialoghi() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDialoghi'");
    }
}
