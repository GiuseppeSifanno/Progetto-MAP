package game.database;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import engine.database.DBManager;
import engine.database.SalvataggioDAO;
import engine.model.Inventario;
import game.model.PassoQuestCompletato;
import game.model.SceltaEffettuata;
import game.model.StatoGioco;

public class StatoGiocoDAO extends BaseDAO<StatoGioco> implements SalvataggioDAO<StatoGioco> {
    private final OggettoDAO oggettoDAO;

    public StatoGiocoDAO(DBManager dbManager, OggettoDAO oggettoDAO) {
        super(dbManager);
        this.oggettoDAO = oggettoDAO;
    }

    @Override
    public void salva(StatoGioco stato, int idSlot) throws SQLException {
        Connection conn = dbManager.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Riga principale del salvataggio
            String sqlSalvataggio = "MERGE INTO PUBLIC.SALVATAGGIO (ID_SLOT, ID_ATTO_CORRENTE, DATA_SALVATAGGIO, ID_DIALOGO_CORRENTE) " +
                    "KEY (ID_SLOT) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSalvataggio)) {
                stmt.setInt(1, idSlot);
                stmt.setString(2, stato.getIdAttoCorrente());
                stmt.setTimestamp(3, Timestamp.from(Instant.now()));
                stmt.setString(4, stato.getIdDialogoCorrente());
                stmt.executeUpdate();
            }

            // 2. Oggetti: stessa strategia
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PUBLIC.SALVATAGGIOINVENTARIOOGGETTO WHERE ID_SLOT = ?")) {
                del.setInt(1, idSlot);
                del.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO PUBLIC.SALVATAGGIOINVENTARIOOGGETTO (ID_SLOT, ID_OGGETTO) VALUES (?, ?)")) {
                for (var o : stato.getInventario().oggetti()) {
                    stmt.setInt(1, idSlot);
                    stmt.setString(2, o.getId());
                    stmt.executeUpdate();
                }
            }

            // 3. Scelte effettuate, in ordine
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

            //4. Quest completate
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PUBLIC.SALVATAGGIOQUESTPASSICOMPLETATI WHERE ID_SLOT = ?")) {
                del.setInt(1, idSlot);
                del.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO PUBLIC.SALVATAGGIOQUESTPASSICOMPLETATI (ID_SLOT, ID_QUEST, ID_PASSO) VALUES ( ?, ?, ? )")) {
                var quest = stato.getPassiQuestCompletati();
                for (PassoQuestCompletato passoQuestCompletato : quest) {
                    stmt.setInt(1, idSlot);
                    stmt.setString(2, passoQuestCompletato.idQuest());
                    stmt.setString(3, passoQuestCompletato.idPasso());
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
    public StatoGioco carica(int idSlot) throws SQLException {
        String idAttoCorrente, idDialogoCorrente;
        List<SceltaEffettuata> scelteEffettuate = new ArrayList<>();
        List<PassoQuestCompletato> passiQuestCompletati = new ArrayList<>();
        List<String> puzzleRisolti =  new ArrayList<>();
        Inventario inventario = new Inventario();

        try (Connection conn = dbManager.getConnection()) {
            String sql = "SELECT ID_ATTO_CORRENTE, ID_DIALOGO_CORRENTE FROM SALVATAGGIO WHERE ID_SLOT = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idSlot);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idAttoCorrente = rs.getString("ID_ATTO_CORRENTE");
                        idDialogoCorrente = rs.getString("ID_DIALOGO_CORRENTE");
                    } else {
                        return null;
                    }
                }
            }

            // SCELTE EFFETTUATE
            sql = "SELECT ID_SCELTA, ID_DIALOGO FROM SALVATAGGIOSCELTEEFFETTUATE WHERE ID_SLOT = ? ORDER BY ORDINE";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idSlot);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        scelteEffettuate.add(new SceltaEffettuata(
                                rs.getString("ID_DIALOGO"),
                                rs.getString("ID_SCELTA")
                        ));
                    }
                }
            }

            // PASSI QUEST COMPLETATI
            sql = "SELECT ID_QUEST, ID_PASSO FROM SALVATAGGIOQUESTPASSICOMPLETATI WHERE ID_SLOT = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idSlot);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        passiQuestCompletati.add(new PassoQuestCompletato(
                                rs.getString("ID_QUEST"),
                                rs.getString("ID_PASSO")
                        ));
                    }
                }
            }

            // INVENTARIO
            sql = "SELECT ID_OGGETTO FROM SALVATAGGIOINVENTARIOOGGETTO WHERE ID_SLOT = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idSlot);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        inventario.aggiungi(
                                this.oggettoDAO.findById(rs.getString("ID_OGGETTO"))
                        );
                    }
                }
            }
        }
        return new StatoGioco(idAttoCorrente, idDialogoCorrente, scelteEffettuate, passiQuestCompletati, inventario, puzzleRisolti);
    }

    @Override
    public List<Integer> listaSlotDisponibili() {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT ID_SLOT FROM SALVATAGGIO";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            try {
                ResultSet rs = stmt.executeQuery();
                while (rs.next())
                    lista.add(rs.getInt("ID_SLOT"));
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            finally {
                conn.close();
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return lista;
    }

    @Override
    public void elimina(int idSlot) {
        String sql = "DELETE FROM SALVATAGGIO WHERE ID_SLOT = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);){
            stmt.setInt(1, idSlot);
            try {
                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            finally {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected StatoGioco mapRow(ResultSet rs) throws SQLException {
        throw new SQLException(new UnsupportedOperationException("Not supported yet."));
    }
}