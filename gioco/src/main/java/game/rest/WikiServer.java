package game.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import engine.database.DBManager;
import game.database.MaterialeDAO;
import game.database.OggettoDAO;
import game.database.RicettaDAO;
import game.model.Ricetta;
import game.model.oggetti.Oggetto;
import game.model.oggetti.Materiale;

/**
 * Server REST/wiki in sola lettura che espone i contenuti del gioco (oggetti,
 * materiali, ricette) sia come JSON ({@code /api/...}) sia come pagine HTML
 * minimali ({@code /wiki/...}). Riusa i DAO già esistenti: non duplica
 * l'accesso al database, si limita a leggerne l'output.
 */
public class WikiServer {
    private static final String BOOTSTRAP_CDN =
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css";
    private static final String LINK_REPOSITORY =
            "https://github.com/GiuseppeSifanno/Progetto-MAP";
    private static final String LINK_REPOSITORY_DIALOGUE_EDITOR =
            "https://github.com/GiuseppeSifanno/dialogue-editor";

    private final int porta;
    private final OggettoDAO oggettoDAO;
    private final MaterialeDAO materialeDAO;
    private final RicettaDAO ricettaDAO;
    private final ObjectMapper mapper = new ObjectMapper();

    private HttpServer server;

    public WikiServer(DBManager dbManager, int porta) {
        this.porta = porta;
        this.oggettoDAO = new OggettoDAO(dbManager);
        this.materialeDAO = new MaterialeDAO(dbManager);
        this.ricettaDAO = new RicettaDAO(dbManager);
    }

    public void avvia() {
        try {
            server = HttpServer.create(new InetSocketAddress(porta), 0);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile avviare il server wiki sulla porta " + porta, e);
        }

        registraIndice();

        // API JSON (dati grezzi)
        registraJson("/api/oggetti", oggettoDAO::findAll);
        registraJson("/api/materiali", materialeDAO::findAll);
        registraJson("/api/ricette", ricettaDAO::findAll);

        // Pagine wiki (HTML leggibile)
        registraPagina("/wiki/oggetti", this::paginaOggetti);
        registraPagina("/wiki/materiali", this::paginaMateriali);
        registraPagina("/wiki/ricette", this::paginaRicette);

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("Wiki REST avviata su http://localhost:" + porta + "/");
    }

    public void ferma() {
        if (server != null) {
            server.stop(0);
        }
    }

    // ==================== registrazione endpoint ====================

    private void registraJson(String path, Supplier<? extends List<?>> datiSupplier) {
        server.createContext(path, exchange -> {
            try {
                gestisciJson(exchange, datiSupplier.get());
            } catch (Exception e) {
                inviaErrore(exchange, e);
            }
        });
    }

    private void registraPagina(String path, Supplier<String> htmlSupplier) {
        server.createContext(path, exchange -> {
            try {
                inviaHtml(exchange, htmlSupplier.get());
            } catch (Exception e) {
                inviaErrore(exchange, e);
            }
        });
    }

    private void inviaErrore(HttpExchange exchange, Exception e) throws IOException {
        e.printStackTrace(); // resta visibile anche in console per il debug
        String html = "<html><body><h2>Errore 500</h2><pre>"
                + escapeHtml(e.toString()) + "</pre></body></html>";
        byte[] corpo = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(500, corpo.length);
        scriviCorpo(exchange, corpo);
    }


    private void gestisciJson(HttpExchange exchange, Object dati) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        byte[] corpo;
        int status = 200;
        try {
            corpo = mapper.writeValueAsBytes(dati);
        } catch (Exception e) {
            corpo = ("{\"errore\":\"" + e.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8);
            status = 500;
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, corpo.length);
        scriviCorpo(exchange, corpo);
    }

    private void inviaHtml(HttpExchange exchange, String html) throws IOException {
        byte[] corpo = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, corpo.length);
        scriviCorpo(exchange, corpo);
    }

    private void scriviCorpo(HttpExchange exchange, byte[] corpo) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(corpo);
        }
    }

    // ==================== pagina indice ====================

    private void registraIndice() {
        server.createContext("/", exchange -> {
            String html = """
                    <html>
                    <head>
                      <meta charset="utf-8">
                      <title>Wiki - Naufraghi all'Isola Misteriosa</title>
                      <link href="%s" rel="stylesheet">
                    </head>
                    <body class="p-4">
                      <div class="container">
                        <h1 class="mb-4">Wiki - Naufraghi all'Isola Misteriosa</h1>

                        <h5>Pagine wiki</h5>
                        <ul>
                          <li><a href="/wiki/oggetti">Oggetti</a></li>
                          <li><a href="/wiki/materiali">Materiali</a></li>
                          <li><a href="/wiki/ricette">Ricette</a></li>
                        </ul>

                        <h5>API JSON</h5>
                        <ul>
                          <li><a href="/api/oggetti">/api/oggetti</a></li>
                          <li><a href="/api/materiali">/api/materiali</a></li>
                          <li><a href="/api/ricette">/api/ricette</a></li>
                        </ul>

                        <h5>Link esterni</h5>
                        <ul>
                          <li><a href="%s" target="_blank">Repository GitHub del progetto</a></li>
                          <li><a href="%s" target="_blank">Repository GitHub dell'editor di dialoghi</a></li>
                        </ul>
                      </div>
                    </body>
                    </html>
                    """.formatted(BOOTSTRAP_CDN, LINK_REPOSITORY, LINK_REPOSITORY_DIALOGUE_EDITOR);
            inviaHtml(exchange, html);
        });
    }

    // ==================== pagine tabella ====================

    private String paginaOggetti() {
        List<Oggetto> oggetti = oggettoDAO.findAll();
        return generaPaginaTabella("Oggetti",
                List.of("ID", "Nome", "Descrizione", "Immagine"),
                oggetti,
                o -> Arrays.asList(o.getId(), o.getNome(), o.getDescrizione(), o.getFilename()));
    }

    private String paginaMateriali() {
        List<Materiale> materiali = materialeDAO.findAll();
        return generaPaginaTabella("Materiali",
                List.of("ID", "Nome", "Descrizione", "Immagine"),
                materiali,
                m -> Arrays.asList(m.getId(), m.getNome(), m.getDescrizione(), m.getFilename()));
    }

    private String paginaRicette() {
        List<Ricetta> ricette = ricettaDAO.findAll();
        return generaPaginaTabella("Ricette",
                List.of("ID", "Ingrediente 1", "Ingrediente 2", "Risultato"),
                ricette,
                r -> Arrays.asList(r.getIdRicetta(), r.getIdIngrediente1(), r.getIdIngrediente2(), r.getIdRisultato()));
    }

    /**
     * Genera una pagina HTML con una tabella Bootstrap a partire da una lista
     * generica di elementi, ognuno trasformato in riga tramite {@code mappatore}.
     */
    private <T> String generaPaginaTabella(String titolo, List<String> intestazioni,
                                           List<T> righe, Function<T, List<String>> mappatore) {
        String intestazioniHtml = intestazioni.stream()
                .map(h -> "<th>" + escapeHtml(h) + "</th>")
                .collect(Collectors.joining());

        String righeHtml = righe.stream()
                .map(elemento -> "<tr>" +
                        mappatore.apply(elemento).stream()
                                .map(cella -> "<td>" + escapeHtml(cella) + "</td>")
                                .collect(Collectors.joining())
                        + "</tr>")
                .collect(Collectors.joining("\n"));

        return """
                <html>
                <head>
                  <meta charset="utf-8">
                  <title>%s - Wiki</title>
                  <link href="%s" rel="stylesheet">
                </head>
                <body class="p-4">
                  <div class="container">
                    <a href="/" class="btn btn-secondary btn-sm mb-3">&larr; Torna all'indice</a>
                    <h1 class="mb-4">%s</h1>
                    <table class="table table-striped table-bordered">
                      <thead><tr>%s</tr></thead>
                      <tbody>
                      %s
                      </tbody>
                    </table>
                  </div>
                </body>
                </html>
                """.formatted(titolo, BOOTSTRAP_CDN, titolo, intestazioniHtml, righeHtml);
    }

    private String escapeHtml(String testo) {
        if (testo == null) return "";
        return testo.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}