package progetto.gioco.game.loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import progetto.gioco.engine.loader.Loadable;
import progetto.gioco.game.dto.AttoDTO;
import progetto.gioco.game.dto.DialogoDTO;
import progetto.gioco.game.dto.SceltaDTO;
import progetto.gioco.game.model.Atto;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.Scelta;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogLoader implements Loadable<Atto> {
    /** 
     * @param path
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
     * @param dto
     * @return Atto
     */
    private Atto convert(AttoDTO dto) {
        Map<String, Dialogo> dialoghiMap = new HashMap<>();

        for (DialogoDTO d : dto.dialoghi) {
            List<Scelta> scelte = new ArrayList<>();

            if (d.scelte != null) {
                for (SceltaDTO s : d.scelte) {
                    scelte.add(new Scelta(s.id, s.testo, s.next));
                }
            }

            Dialogo dialogo = new Dialogo(d.id, d.testo, scelte);
            dialoghiMap.put(d.id, dialogo);
        }

        return new Atto(dto.idAtto, dialoghiMap, dto.dialogoIniziale);
    }
}