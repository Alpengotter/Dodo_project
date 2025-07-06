--liquibase formatted sql
--changeset Nedobezhkin.M.I.:add_extensions
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE TEXT SEARCH CONFIGURATION ru_optimized (COPY = russian);
ALTER TEXT SEARCH CONFIGURATION ru_optimized
    ALTER MAPPING FOR word WITH unaccent, russian_stem;
--rollback ;

--changeset Nedobezhkin.M.I.:create_index
CREATE INDEX users_fullname_tsvector_idx ON users
    USING gin(to_tsvector('ru_optimized', first_name || ' ' || last_name));

CREATE INDEX users_firstname_tsvector_idx ON users
    USING gin(to_tsvector('ru_optimized', first_name));

CREATE INDEX users_lastname_tsvector_idx ON users
    USING gin(to_tsvector('ru_optimized', last_name));
--rollback ;

--changeset Nedobezhkin.M.I.:create_index_for_mistakes
CREATE INDEX users_name_trgm_idx ON users
    USING gin((first_name || ' ' || last_name) gin_trgm_ops);
--rollback ;

--changeset Nedobezhkin.M.I.:create_index_for_mistakes_2
CREATE INDEX users_firstname_trgm_idx ON users
    USING gin((first_name) gin_trgm_ops);
CREATE INDEX users_lastname_trgm_idx ON users
    USING gin((last_name) gin_trgm_ops);
--rollback ;