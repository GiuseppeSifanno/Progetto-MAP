# Contratto Engine → Game

## Scopo

Questo documento descrive come il package `progetto.gioco.engine` è pensato
per essere un motore riutilizzabile per **avventure grafiche generiche**
(dialoghi, scelte, inventario, interazioni, salvataggi), e come il package
`progetto.gioco.game` ne rappresenta una singola istanza concreta: il gioco
della ciurma di pirati naufraghi.

Un altro team, per creare un gioco diverso con lo stesso engine, dovrebbe
solo scrivere il proprio package `game` seguendo le stesse regole descritte
qui sotto — senza toccare `engine`.

## Regola generale

`engine` non deve mai dipendere da classi concrete di `game`. Ogni classe in
`engine` è astratta o generica; le classi in `game` estendono o implementano
i contratti dell'engine con la logica specifica del proprio gioco.

## Tabella di conformità

| Contratto engine (astratto/interfaccia) | Implementazione nel gioco pirata |
|---|---|
| `BaseEntity`, `Identifiable` | Base di tutte le entità (`Oggetto`, dialoghi, ecc.) |
| `BaseDialogo` | `Dialogo` |
| `BaseScelta` | `Scelta` |
| `BaseAtto<D extends BaseDialogo>` | `Atto extends BaseAtto<Dialogo>` |
| `BaseOggetto` | `Oggetto` |
| `BaseInterazione` | `Interazione` |
| `BaseZona<I extends BaseInterazione>` | `Zona extends BaseZona<Interazione>` |
| `BaseDialogManager<D extends BaseDialogo>` | `DialogManager extends BaseDialogManager<Dialogo>` |
| `BaseInventarioManager` | `InventarioManager` |
| `BaseInterazioneObserver` | `InterazioneObserver` |
| `BaseSaveManager` | `SaveManager` |
| `BaseGameManager` | `GameManager` |
| `BasePanel` | Pannelli concreti in `game.gui` (`GamePanel`, `InventarioPanel`, `PausaPanel`, `QuestPanel`, ecc.) |
| `Loadable<T, D>` | `DialogLoader` (`Loadable<Atto, AttoDTO>`), `InterazioniLoader` (`Loadable<Zona, ZonaDTO>`), `QuestLoader` (`Loadable<Quest, QuestDTO>`) |
| `GameObserver` / `GameObservable` | `GUIObserver`, manager concreti (`DialogManager`, `InventarioManager`, `InterazioneObserver`) |
| `DBManager` (configurabile via `config.properties`) | Usato così com'è, ogni gioco fornisce il proprio `config.properties` |

Elementi **fuori da questo schema**, perché specifici del gioco pirata e non
del genere adventure in generale:

- `game.minigioco` (`MontacarichiManager`, `ZuppaFogliantiManager`) e
  `game.model.minigioco` (`Erba`, `ZuppaFogliantiConfig`) — i minigiochi sono
  contenuto specifico di questo gioco, non un concetto dell'engine.
- `Quest`, `PassoQuest`, `PassoQuestCompletato`, `QuestLoader` — il sistema di
  quest è una scelta narrativa del gioco pirata, non un contratto dell'engine.
- `game.rest.WikiServer` — l'esposizione REST/wiki degli oggetti è una
  funzionalità aggiuntiva del gioco concreto.
- `Ricetta` e il crafting in `InventarioManager` — meccanica di gioco
  specifica, non richiesta dall'engine.
- Contenuto di `schema.sql`, dei JSON in `resources/dialogs` e `resources/zone`,
  e di `config.properties` — dati, non codice.
- `PersonaggioDTO` — è un DTO usato da `DialogLoader` per leggere i personaggi
  dichiarati in `AttoDTO.personaggi` (id + nome) e associarli alle `Battuta`
  dei dialoghi; è dato di caricamento, non un contratto dell'engine.

## Come creare un nuovo gioco sullo stesso engine

1. Creare le proprie classi concrete `Dialogo`, `Scelta`, `Atto`, `Oggetto`,
   `Interazione`, `Zona` estendendo le rispettive `Base*` dell'engine.
2. Creare i propri manager concreti (`DialogManager`, `InventarioManager`,
   `SaveManager`, un `*InterazioneObserver`) estendendo i `Base*` dell'engine.
3. Creare un proprio `GameManager extends BaseGameManager` che assembla i
   manager sopra e istanzia `DBManager` passando il proprio
   `config.properties`.
4. Fornire il proprio `config.properties` e `schema.sql` con i nomi delle
   tabelle e i path desiderati.
5. (Opzionale) Implementare i propri `GameObserver` concreti (es. un
   `GUIObserver`) per collegare GUI, salvataggi, ecc. agli eventi
   (`TipoEvento`) notificati dai manager observable.
6. (Opzionale) Estendere `BasePanel` in `engine.GUI` per i propri pannelli
   Swing, secondo lo stesso pattern di `game.gui`.

Nessuna modifica al package `engine` è richiesta per i punti sopra: è
esattamente questo che rende l'engine riutilizzabile.