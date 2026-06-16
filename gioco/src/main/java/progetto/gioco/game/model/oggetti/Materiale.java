package progetto.gioco.game.model.oggetti;

import progetto.gioco.engine.model.BaseOggetto;

public class Materiale extends BaseOggetto{
    private int quantita;

    public Materiale(String id, String nome) {
        super(id, nome);
    }

    /** 
     * @return int
     */
    public int getQuantita() {
        return this.quantita;
    }

    /** 
     * @param quantita
     */
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    @Override
    public void usa() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'usa'");
    }
    
}
