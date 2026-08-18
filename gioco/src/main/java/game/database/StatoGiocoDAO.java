package game.database;

import java.sql.*;
import java.time.Instant;
import java.util.List;

import engine.database.DBManager;
import engine.database.SalvataggioDAO;
import game.model.StatoGioco;
import game.model.oggetti.Materiale;

public class StatoGiocoDAO extends BaseDAO<StatoGioco> implements SalvataggioDAO<StatoGioco> {
    private MaterialeDAO materialeDAO;
    private OggettoDAO oggettoDAO;

    public StatoGiocoDAO(DBManager dbManager, MaterialeDAO materialeDAO, OggettoDAO oggettoDAO) {
        super(dbManager);
        this.materialeDAO = materialeDAO;
        this.oggettoDAO = oggettoDAO;
    }

    @Override
    public void salva(StatoGioco stato, int idSlot) throws SQLException {
        Connection conn = dbManager.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Riga principale del salvataggio
            String sqlSalvataggio = "MERGE INTO PUBLIC.SALVATAGGIO (ID_SLOT, ID_ATTO_CORRENTE, DATA_SALVATAGGIO) " +
                    "KEY (ID_SLOT) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSalvataggio)) {
                stmt.setInt(1, idSlot);
                stmt.setString(2, stato.getIdAttoCorrente());
                stmt.setTimestamp(3, Timestamp.from(Instant.now()));
                stmt.executeUpdate();
            }

            // 2. Materiali: svuota lo slot, poi reinserisce quelli attuali
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PUBLIC.SALVATAGGIOINVENTARIOMATERIALE WHERE ID_SLOT = ?")) {
                del.setInt(1, idSlot);
                del.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO PUBLIC.SALVATAGGIOINVENTARIOMATERIALE (ID_SLOT, ID_MATERIALE, QUANTITA) VALUES (?, ?, ?)")) {
                for (var o : stato.getInventario().oggetti()) {
                    if (o instanceof Materiale materiale) {
                        stmt.setInt(1, idSlot);
                        stmt.setString(2, materiale.getId());
                        stmt.setInt(3, materiale.getQuantita());
                        stmt.executeUpdate();
                    }
                }
            }

            // 3. Oggetti (non materiali): stessa strategia
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PUBLIC.SALVATAGGIOINVENTARIOOGGETTO WHERE ID_SLOT = ?")) {
                del.setInt(1, idSlot);
                del.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO PUBLIC.SALVATAGGIOINVENTARIOOGGETTO (ID_SLOT, ID_OGGETTO) VALUES (?, ?)")) {
                for (var o : stato.getInventario().oggetti()) {
                    if (!(o instanceof Materiale)) {
                        stmt.setInt(1, idSlot);
                        stmt.setString(2, o.getId());
                        stmt.executeUpdate();
                    }
                }
            }

            // 4. Puzzle risolti
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PUBLIC.SALVATAGGIOPUZZLERISOLTI WHERE ID_SLOT = ?")) {
                del.setInt(1, idSlot);
                del.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO PUBLIC.SALVATAGGIOPUZZLERISOLTI (ID_SLOT, ID_PUZZLE) VALUES (?, ?)")) {
                for (String idPuzzle : stato.getPuzzleRisolti()) {
                    stmt.setInt(1, idSlot);
                    stmt.setString(2, idPuzzle);
                    stmt.executeUpdate();
                }
            }

            // 5. Scelte effettuate, in ordine
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PUBLIC.SALVATAGGIOSCELTEEFFETTUATE WHERE ID_SLOT = ?")) {
                del.setInt(1, idSlot);
                del.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO PUBLIC.SALVATAGGIOSCELTEEFFETTUATE (ID_SLOT, ID_SCELTA, ID_DIALOGO, ORDINE) VALUES (?, ?, ?, ?)")) {
                var scelte = stato.getScelteEffettuate();
                for (int i = 0; i < scelte.size(); i++) {
                    stmt.setInt(1, idSlot);
                    stmt.setString(2, scelte.get(i).idScelta());
                    stmt.setString(3, scelte.get(i).idDialogo());
                    stmt.setInt(4, i);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    @Override
    public StatoGioco carica(int idSlot) {
        return null;
    }

    @Override
    public List<Integer> listaSlotDisponibili() {
        return null;
    }

    @Override
    public void elimina(int idSlot) {

    }

    @Override
    protected StatoGioco mapRow(ResultSet rs) throws SQLException {
        return null;
    }
}