package game.dto;

import java.util.List;

public class InterazioneDTO {
    public String id;
    public String messaggioBloccato;
    public String messaggioSbloccato;
    public List<String> condizioni;
    public List<EffettoDTO> effetti;
}