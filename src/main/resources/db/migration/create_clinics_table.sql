--liquibase formatted sql
--changeset Nedobezhkin.M.I.:create_clinics_table
create table if not exists clinics
(
    id int primary key,
    name_ varchar(100),
    currency_ bigint default 0
    );
--rollback drop table clinics;