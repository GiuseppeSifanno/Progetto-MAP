Va salvato in gioco/src/main/resources/zone/, stesso posto di spiaggia.json.

- condizioni: [] → l'interazione scatta sempre (nessun requisito d'inventario).
- messaggioBloccato va lasciato vuoto "" se condizioni è vuoto (non verrà mai usato). 
- effetti.tipo accetta solo AGGIUNGI_OGGETTO, RIMUOVI_OGGETTO, AVVIA_DIALOGO. 
- id interazione: convenzione int_<zona>_<oggetto>. 
- Per i flag di stato (non oggetti fisici): flag_<descrizione_breve>, aggiunti via AGGIUNGI_OGGETTO come qualunque altro oggetto.
Ogni interazione può avere più effetti in sequenza (applicati solo se le condizioni sono soddisfatte).