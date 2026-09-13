package game.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import engine.loader.Loadable;
import game.dto.PassoQuestDTO;
import game.dto.QuestDTO;
import game.model.PassoQuest;
import game.model.Quest;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Carica i dati delle quest da un file JSON.
 */
public class QuestLoader implements Loadable<Map<String, Quest>, List<QuestDTO>> {
    @Override
    public Map<String, Quest> load(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(path)) {
            List<QuestDTO> dto = mapper.readValue(is, new TypeReference<List<QuestDTO>>() {});
            return convert(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Quest> convert(List<QuestDTO> dtoList) {
        Map<String, Quest> questMap = new LinkedHashMap<>();

        for (QuestDTO dto : dtoList) {
            List<PassoQuest> passi = new ArrayList<>();
            if (dto.passi != null) {
                for (PassoQuestDTO passoDTO : dto.passi) {
                    passi.add(new PassoQuest(passoDTO.idPasso, passoDTO.testo));
                }
            }
            questMap.put(dto.idQuest, new Quest(dto.idQuest, dto.nome, passi));
        }

        return questMap;
    }
}