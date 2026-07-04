package progetto.gioco.engine.model;

import java.util.Objects;

public abstract class BaseEntity implements Identifiable {
    protected String id;

    public BaseEntity(String id) {
        this.id = id;
    }

    /**
     * Ritorna l'id dell'entità
     * @return String
     */
    public String getId(){ return this.id; }

    /**
     * Confronta tutti gli attributi dei due oggetti.
     * Se il numero di attributi cresce, considerare un'altra soluzione
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(obj.getClass() != this.getClass())
            return false;

        final BaseEntity other = (BaseEntity) obj;

        return (this.id == null) ? (other.id == null) : this.id.equalsIgnoreCase(other.id);
    }
}
