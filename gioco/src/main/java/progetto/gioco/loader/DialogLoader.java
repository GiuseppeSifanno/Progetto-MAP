package progetto.gioco.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import progetto.gioco.dto.AttoDTO;
import progetto.gioco.dto.DialogoDTO;
import progetto.gioco.dto.SceltaDTO;
import progetto.gioco.model.Atto;
import progetto.gioco.model.Dialogo;
import progetto.gioco.model.Scelta;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogLoader {
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