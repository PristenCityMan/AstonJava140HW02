--liquibase formatted sql

--changeset timonovs:1
CREATE TABLE public.users
(
    id bigserial NOT NULL,
    name character varying(100),
    email character varying(100),
    age integer,
    created_at timestamp with time zone,
    PRIMARY KEY (id)
);
