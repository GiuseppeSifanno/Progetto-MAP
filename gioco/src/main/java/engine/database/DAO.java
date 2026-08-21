package engine.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DAO <T, ID>{
    T findById(ID id);
    List<T> findAll();
    void save(T entity);
    void delete(ID id);
}

