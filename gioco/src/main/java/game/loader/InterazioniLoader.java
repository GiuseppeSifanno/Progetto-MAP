package game.loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import engine.loader.Loadable;
import engine.observer.Effetto;
import game.dto.InterazioneDTO;
import game.dto.ZonaDTO;
import game.model.Interazione;
import game.model.Zona;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterazioniLoader implements Loadable<Zona> {

    /**
     * @param path
     * @return Zona
     */
    public Zona load(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(path)) {
            ZonaDTO dto = mapper.readValue(is, ZonaDTO.class);
            return convert(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param dto
     * @return Zona
     */
    private Zona convert(ZonaDTO dto) {
        Map<String, Interazione> interazioniMap = new HashMap<>();

        for (InterazioneDTO i : dto.interazioni) {
            List<Effetto> effetti = i.effetti.stream()
                    .map(e -> new Effetto(e.tipo, e.valore))
                    .toList();

            Interazione interazione = new Interazione(
                    i.id, i.condizioni, i.messaggioBloccato, i.messaggioSbloccato, effetti
            );
            interazioniMap.put(i.id, interazione);
        }

        return new Zona(dto.idZona, interazioniMap);
    }
}