package game.loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import engine.loader.Loadable;
import engine.model.BaseDialogo;
import engine.model.Battuta;
import engine.model.Personaggio;
import game.dto.*;
import game.model.Atto;
import game.model.Dialogo;
import game.model.Scelta;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogLoader implements Loadable<Atto> {
    /** 
     * @param path Percorso relativo al file che contiene l'atto
     * @return Atto
     */
    public Atto load(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(path)){
            AttoDTO dto = mapper.readValue(is, AttoDTO.class);
            return convert(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 
     * @param dto struttura dati che funge da schema
     * @return Atto
     */
    private Atto convert(AttoDTO dto) {
        String idAtto = dto.meta.idAtto;
        String idDialogoIniziale = dto.meta.dialogoIniziale;
        Map<String, Dialogo> dialoghiMap = new HashMap<>();
        Map<String, Personaggio> personaggi = new HashMap<>();

        // Conversione per i personaggi
        for (PersonaggioDTO p: dto.personaggi ) {
            personaggi.put(p.id, new Personaggio(p.id, p.nome));
        }

        for (DialogoDTO d : dto.dialoghi) {
            // 1. converto le battute DTO in Battuta (record engine)
            List<Battuta> battute = new ArrayList<>();
            for (BattutaDTO b : d.battute) {
                battute.add(new Battuta(b.personaggioId, b.testo));
            }

            // 2. converto le scelte, esattamente come facevi già
            List<Scelta> scelte = new ArrayList<>();
            if (d.scelte != null) {
                for (SceltaDTO s : d.scelte) {
                    scelte.add(new Scelta(s.id, s.testo, s.next));
                }
            }

            // 3. costruisco il Dialogo con battute al posto di testo
            Dialogo dialogo = new Dialogo(d.id, battute, scelte, d.nextId);
            dialoghiMap.put(d.id, dialogo);
        }
        return new Atto(idAtto, idDialogoIniziale, personaggi, dialoghiMap);
    }
}