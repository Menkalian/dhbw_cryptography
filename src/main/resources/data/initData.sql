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
