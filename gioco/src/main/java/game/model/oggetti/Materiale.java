package game.model.oggetti;

import engine.model.BaseOggetto;

public class Materiale extends BaseOggetto {
    private int quantita;

    public Materiale(String id, String nome, String descrizione, String filename) {
        super(id, nome, descrizione, filename);
    }

    /** 
     * @return int
     */
    public int getQuantita() {
        return this.quantita;
    }

    /** 
     * @param quantita Quantità di oggetti
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
