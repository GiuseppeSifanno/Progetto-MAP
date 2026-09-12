    -- ============================================================
    -- OGGETTI
    -- ============================================================

    CREATE TABLE IF NOT EXISTS PUBLIC.Oggetto (
        id_oggetto VARCHAR(3) PRIMARY KEY,
        nome VARCHAR(255) NOT NULL,
        descrizione TEXT,
        image_name VARCHAR(255), -- Nome del file dell'immagine
        combinabile BOOLEAN NOT NULL DEFAULT FALSE
    );

    -- ============================================================
    -- RICETTE (crafting)
    -- ============================================================

    DROP TABLE IF EXISTS PUBLIC.Ricetta;
    CREATE TABLE IF NOT EXISTS PUBLIC.Ricetta (
        id_ricetta   VARCHAR(3) PRIMARY KEY,
        id_risultato VARCHAR(3) NOT NULL,
        FOREIGN KEY (id_risultato) REFERENCES PUBLIC.Oggetto(id_oggetto)
    );

    CREATE TABLE IF NOT EXISTS PUBLIC.Ricetta_Ingrediente (
        id_ricetta     VARCHAR(3) NOT NULL,
        id_ingrediente VARCHAR(3) NOT NULL,
        PRIMARY KEY (id_ricetta, id_ingrediente),
        FOREIGN KEY (id_ricetta) REFERENCES PUBLIC.Ricetta(id_ricetta) ON DELETE CASCADE,
        FOREIGN KEY (id_ingrediente) REFERENCES PUBLIC.Oggetto(id_oggetto)
    );

    -- ============================================================
    -- SALVATAGGI
    -- ============================================================

    DROP TABLE IF EXISTS PUBLIC.SalvataggioScelteEffettuate;
    DROP TABLE IF EXISTS PUBLIC.SalvataggioInventarioOggetto;
    DROP TABLE IF EXISTS PUBLIC.SalvataggioQuestPassiCompletati;
    DROP TABLE IF EXISTS PUBLIC.Salvataggio;

    CREATE TABLE PUBLIC.Salvataggio (
        id_slot INT PRIMARY KEY,
        id_atto_corrente VARCHAR(3) NOT NULL,
        id_dialogo_corrente VARCHAR(3),
        data_salvataggio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE PUBLIC.SalvataggioQuestPassiCompletati (
        id_slot INT NOT NULL,
        id_quest VARCHAR(255) NOT NULL,
        id_passo VARCHAR(255) NOT NULL,
        CONSTRAINT PK_SALVATAGGIO_QUEST_PASSI
            PRIMARY KEY (id_slot, id_quest, id_passo),
        CONSTRAINT FK_SALVATAGGIO_QUEST_PASSI_SLOT
            FOREIGN KEY (id_slot)
                REFERENCES PUBLIC.Salvataggio(id_slot)
                ON DELETE CASCADE
    );

    CREATE TABLE PUBLIC.SalvataggioInventarioOggetto (
        id_slot INT NOT NULL,
        id_oggetto VARCHAR(3) NOT NULL,
        PRIMARY KEY (id_slot, id_oggetto),
        FOREIGN KEY (id_slot)
            REFERENCES PUBLIC.Salvataggio(id_slot)
            ON DELETE CASCADE,
        FOREIGN KEY (id_oggetto)
            REFERENCES PUBLIC.Oggetto(id_oggetto)
    );

    CREATE TABLE PUBLIC.SalvataggioScelteEffettuate (
        id_slot INT NOT NULL,
        id_scelta VARCHAR(3) NOT NULL,
        id_dialogo VARCHAR(3) NOT NULL,
        ordine INT NOT NULL,
        PRIMARY KEY (id_slot),
        FOREIGN KEY (id_slot)
            REFERENCES PUBLIC.Salvataggio(id_slot)
            ON DELETE CASCADE
    );