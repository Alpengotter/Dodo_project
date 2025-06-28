--liquibase formatted sql
--changeset Nedobezhkin.M.I.:add_surname
alter table users
add column surname varchar(255);
--rollback ;

-- --changeset Nedobezhkin.M.I.:add_clinics_id
-- alter table users
-- add column clinics_id int;
--
-- alter table users
-- add constraint fk_clinics FOREIGN KEY (clinics_id) REFERENCES clinics(id);
-- --rollback ;
