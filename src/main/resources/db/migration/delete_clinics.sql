--liquibase formatted sql
--changeset Nedobezhkin.M.I.:delete_clinics_with_users_again
delete from users
where id in (select user_id
             from user_clinic_map
             where clinic_id in (select id
                                 from clinics
                                 where name_ = 'МЦДХ')) ;

delete from user_clinic_map
where clinic_id in (select id
                    from clinics
                    where name_ = 'МЦДХ');

delete from clinics
where name_ = 'МЦДХ';
--rollback ;
