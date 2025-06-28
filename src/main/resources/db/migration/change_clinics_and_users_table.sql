--liquibase formatted sql

--changeset Nedobezhkin.M.I.:add_auto_generation_id
ALTER TABLE users
alter id add generated ALWAYS AS IDENTITY;
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

ALTER TABLE clinics
alter id add generated ALWAYS AS IDENTITY;
SELECT setval('clinics_id_seq', (SELECT MAX(id) FROM clinics));
--rollback ;

--changeset Nedobezhkin.M.I.:add_default
ALTER TABLE users
alter lemons set default 0;

ALTER TABLE clinics
alter currency_ set default 0;
--rollback ;


