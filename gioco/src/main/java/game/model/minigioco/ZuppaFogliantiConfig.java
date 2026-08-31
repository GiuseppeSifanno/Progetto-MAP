package game.model.minigioco;

import java.util.List;

/**
 * Configurazione del minigioco.
 * @param erbeDisponibili
 * @param erbeCorretteRichieste
 * @param zonaVerdeMin
 * @param zonaVerdeMax
 * @param colpiRichiesti
 * @param velocitaIndicatoreMs
 * @param idOggettoRichiestoFaseCapitano
 * @param idOggettoRisultato
 */
public record ZuppaFogliantiConfig(
        List<Erba> erbeDisponibili,       // tutte le erbe mostrate, corrette e no
        int erbeCorretteRichieste,        // quante erbe giuste servono per superare la fase
        int zonaVerdeMin,                 // 0-100
        int zonaVerdeMax,                 // 0-100
        int colpiRichiesti,               // Quante pressioni riuscite servono
        int velocitaIndicatoreMs,         // intervallo di refresh del thread (es. 16ms)
        String idOggettoRichiestoFaseCapitano, // es. "o19" (tazza da tè)
        String idOggettoRisultato         // es. "o12" (zuppa, oggetto vero nel DB)
) {}