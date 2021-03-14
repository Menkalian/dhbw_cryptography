SELECT c.name as channelname, p1.name as participant01, p2.name as participant02
FROM channel c
         JOIN participants p1 ON p1.id = c.participant_01
         JOIN participants p2 ON p2.id = c.participant_02
ORDER BY c.name;