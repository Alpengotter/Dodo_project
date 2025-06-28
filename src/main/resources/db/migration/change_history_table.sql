--liquibase formatted sql
--changeset Nedobezhkin.M.I.:add_clinic_id
alter table history
add column clinic_id int references clinics(id);
--rollback ;

