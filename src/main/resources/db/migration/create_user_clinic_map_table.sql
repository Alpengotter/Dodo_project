--liquibase formatted sql
--changeset Nedobezhkin.M.I.:create_user_clinic_map_table
create table if not exists user_clinic_map
(
    id int primary key generated ALWAYS AS IDENTITY,
    user_id int,
    clinic_id int
    );
--rollback drop table user_clinic_map;