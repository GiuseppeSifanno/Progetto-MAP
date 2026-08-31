Va salvato in gioco/src/main/resources/zone/, stesso posto delle altre zone.

- condizioni: [] → l'interazione scatta sempre.
- messaggioBloccato va lasciato vuoto "" se condizioni è vuoto.
- effetti.tipo: AGGIUNGI_OGGETTO, RIMUOVI_OGGETTO, AVVIA_DIALOGO, PROSSIMO_ATTO.
- id interazione: convenzione int_<zona>_<oggetto>.
- I valori degli oggetti/flag sono gli ID del DB (es. o1, o2, ...), non i nomi descrittivi.
- I flag sono oggetti tecnici del DB e non devono essere mostrati come oggetti di gameplay dalla GUI.
- PROSSIMO_ATTO notifica GameManager e avanza nella sequenza a0 → a1 → a2 → a3 → a4 → a5.
