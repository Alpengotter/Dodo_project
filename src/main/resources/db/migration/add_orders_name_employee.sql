--liquibase formatted sql
--changeset Nedobezhkin.M.I.:add_employee_name
alter table orders
add column employee_name varchar(255);
--rollback ;
