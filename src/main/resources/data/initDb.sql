CREATE TABLE IF NOT EXISTS algorithms
(
    id   TINYINT     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1),
    name VARCHAR(10) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS types
(
    id   TINYINT     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1),
    name VARCHAR(10) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS participants
(
    id      TINYINT     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1),
    name    VARCHAR(50) NOT NULL,
    type_id TINYINT     NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name),
    FOREIGN KEY (type_id) REFERENCES types (id)
);

CREATE TABLE IF NOT EXISTS channel
(
    name           VARCHAR(25) NOT NULL,
    participant_01 TINYINT     NOT NULL,
    participant_02 TINYINT     NOT NULL,

    PRIMARY KEY (name),
    FOREIGN KEY (participant_01) REFERENCES participants (id),
    FOREIGN KEY (participant_02) REFERENCES participants (id)
);

CREATE TABLE IF NOT EXISTS messages
(
    id                  TINYINT     NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1),
    participant_from_id TINYINT     NOT NULL,
    participant_to_id   TINYINT     NOT NULL,
    plain_message       VARCHAR(50) NOT NULL,
    algorithm_id        TINYINT     NOT NULL,
    encrypted_message   VARCHAR(50) NOT NULL,
    keyfile             VARCHAR(20) NOT NULL,
    timestamp           INTEGER,

    PRIMARY KEY (id),
    FOREIGN KEY (participant_from_id) REFERENCES participants (id),
    FOREIGN KEY (participant_to_id) REFERENCES participants (id),
    FOREIGN KEY (algorithm_id) REFERENCES algorithms (id)
);