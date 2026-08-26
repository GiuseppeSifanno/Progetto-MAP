package game.model;

import engine.model.BaseEntity;

import java.util.Collections;
import java.util.List;

public class Quest extends BaseEntity {
    private final String nome;
    private final List<PassoQuest> passi;

    public Quest(String idQuest, String nome, List<PassoQuest> passi) {
        super(idQuest);
        this.nome = nome;
        this.passi = passi;
    }

    public String getNome() {
        return nome;
    }

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