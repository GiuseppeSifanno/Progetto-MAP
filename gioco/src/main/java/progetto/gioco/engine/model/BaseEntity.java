package progetto.gioco.engine.model;

public abstract class BaseEntity {
    protected String id;

    public BaseEntity(String id) {
        this.id = id;
    }

    /** 
     * @return String
     */
    public String getId(){
        return this.id;
    }
}
