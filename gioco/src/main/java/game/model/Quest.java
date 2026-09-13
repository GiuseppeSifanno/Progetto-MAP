package game.model;

import engine.model.BaseEntity;

import java.util.Collections;
import java.util.List;

public class Quest extends BaseEntity {
    private final String nome;
    private final List<PassoQuest> passi;

    /**
     * Crea una nuova istanza di Quest.
     * @param idQuest identificativo univoco della quest
     * @param nome nome della quest
     * @param passi lista dei passi della quest
     */
    public Quest(String idQuest, String nome, List<PassoQuest> passi) {
        super(idQuest);
        this.nome = nome;
        this.passi = passi;
    }

    /**
     * Restituisce il nome della quest.
     * @return nome della quest
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la lista dei passi della quest.
     * @return lista dei passi <b>non modificabile</b>
     */
    public List<PassoQuest> getPassi() {
        return Collections.unmodifiableList(passi);
    }

    public PassoQuest getPasso(String idPasso) {
        return passi.stream()
                .filter(p -> p.idPasso().equalsIgnoreCase(idPasso))
                .findFirst()
                .orElse(null);
    }
}