package game.model.minigioco;
import java.util.List;

public record ZuppaFogliantiConfig(
        List<Erba> erbeDisponibili,       // tutte le erbe mostrate, corrette e no
        int erbeCorretteRichieste,        // quante erbe giuste servono per superare la fase
        int zonaVerdeMin,                 // 0-100
        int zonaVerdeMax,                 // 0-100
        int colpiRichiesti,               // quante pressioni riuscite servono (es. 3, come da narrativa "tre nodi" — qui "tre radici pestate")
        int velocitaIndicatoreMs,         // intervallo di refresh del thread (es. 16ms)
        String idOggettoRichiestoFaseCapitano, // "o_tazza_te" o equivalente
        String idFlagCompletamento        // "flag_zuppa_pronta"
) {}

