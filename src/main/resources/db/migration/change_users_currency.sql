--liquibase formatted sql
--changeset Nedobezhkin.M.I.:set_diamonds
update users
set diamonds = 0
where diamonds is null;
--rollback ;

--changeset Nedobezhkin.M.I.:make_diamonds_default
alter table users
alter diamonds set default 0;
--rollback ;
