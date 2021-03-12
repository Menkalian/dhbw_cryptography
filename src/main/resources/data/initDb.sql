DROP TABLE messages;
DROP TABLE channel;
DROP TABLE participants;
DROP TABLE types;
DROP TABLE algorithms;
COMMIT;

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

COMMIT;

-- Fill in algorithms
INSERT INTO algorithms
    (name)
VALUES
    ('shift'),
    ('rsa');

--Fill in participant types
INSERT INTO types
    (name)
VALUES
    ('normal'),
    ('intruder');

-- Fill in predefined participants
INSERT INTO participants
    (name, type_id)
VALUES
    ('branch_hkg', 1),
    ('branch_cpt', 1),
    ('branch_sfo', 1),
    ('branch_syd', 1),
    ('branch_wuh', 1),
    ('msa', 2);

-- Fill in predefined channels
INSERT INTO channel
    (name, participant_01, participant_02)
VALUES
    ('hkg_wuh', 1, 5),
    ('hkg_cpt', 1, 2),
    ('cpt_syd', 2, 4),
    ('syd_sfo', 4, 3);