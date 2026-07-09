# Contratto Engine → Game

## Scopo

Questo documento descrive come il package `progetto.gioco.engine` è pensato
per essere un motore riutilizzabile per **avventure grafiche generiche**
(dialoghi, scelte, inventario, puzzle, salvataggi), e come il package
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
| `BaseEntity`, `Identifiable` | Base di tutte le entità (`Giocatore`, `BaseNPC`, ecc.) |
| `BaseDialogo` | `Dialogo` |
| `BaseScelta` | `Scelta` |
| `BaseAtto<D extends BaseDialogo>` | `Atto extends BaseAtto<Dialogo>` |
| `BaseOggetto` | `Oggetto`, `Materiale` |
| `BasePuzzle` | `Puzzle` |
| `BaseDialogManager<D extends BaseDialogo>` | `DialogManager extends BaseDialogManager<Dialogo>` |
| `BaseInventarioManager` | `InventarioManager` |
| `BasePuzzleManager` | `PuzzleManager` |
| `BaseSaveManager` | `SaveManager` |
| `BaseGameManager` | `GameManager` |
| `BaseLoader<T>` / `Loadable<T>` | `DialogLoader` (e futuri `OggettoLoader`, `PuzzleLoader`) |
| `GameObserver` / `GameObservable` | `GUIObserver`, `SaveObserver` (da UML) |
| `DBManager` (configurabile via `config.properties`) | Usato così com'è, ogni gioco fornisce il proprio `config.properties` |

Elementi **fuori da questo schema**, perché specifici del gioco pirata e non
del genere adventure in generale:

- `BaseNPC`, `NPC_Parlante`, `NPC_Raccoglitore` — la logica di raccolta
  materiali è concettualmente legata a questo gioco, non all'engine.
- Contenuto di `schema.sql`, dei JSON in `resources/dialogs`, e di
  `config.properties` — dati, non codice.

## Come creare un nuovo gioco sullo stesso engine

1. Creare le proprie classi concrete `Dialogo`, `Scelta`, `Atto`, `Oggetto`,
   `Puzzle` estendendo le rispettive `Base*` dell'engine.
2. Creare i propri manager concreti (`DialogManager`, `InventarioManager`,
   `PuzzleManager`, `SaveManager`) estendendo i `Base*Manager`.
3. Creare un proprio `GameManager extends BaseGameManager` che assembla i
   manager sopra e istanzia `DBManager` passando il proprio
   `config.properties`.
4. Fornire il proprio `config.properties` e `schema.sql` con i nomi delle
   tabelle e i path desiderati.
5. (Opzionale) Implementare i propri `GameObserver` concreti per collegare
   GUI, salvataggi, ecc. agli eventi (`TipoEvento`) notificati dai manager
   observable.

Nessuna modifica al package `engine` è richiesta per i punti sopra: è
esattamente questo che rende l'engine riutilizzabile.
