SELECT 'CREATE DATABASE nombre_de_la_base_de_datos'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'tienda');

DROP TABLE IF EXISTS "funkos";
DROP SEQUENCE IF EXISTS funkos_id_seq;
DROP TABLE IF EXISTS "user_roles";
DROP TABLE IF EXISTS "usuarios";
DROP SEQUENCE IF EXISTS usuarios_id_seq;
DROP TABLE IF EXISTS "categorias";
DROP SEQUENCE IF EXISTS categorias_id_seq;


CREATE SEQUENCE funkos_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 7 CACHE 1;

CREATE TABLE "public"."funkos"
(
    "id"                        bigint                         DEFAULT nextval('funkos_id_seq') NOT NULL,
    "nombre"                    character varying(255),
    "precio"                    double precision               DEFAULT '0.0',
    "cantidad"                  integer                        DEFAULT '0',
    "imagen"                    text                           DEFAULT 'https://via.placeholder.com/150',
    "fecha_creacion"            timestamp                      DEFAULT CURRENT_TIMESTAMP           NOT NULL,
    "fecha_actualizacion"       timestamp                      DEFAULT CURRENT_TIMESTAMP           NOT NULL,
    "is_activo"    boolean                                     DEFAULT true,
    "categoria_id" bigint NOT NULL,
    CONSTRAINT "funkos_pkey" PRIMARY KEY ("id")
) WITH (oids = false);


CREATE SEQUENCE categorias_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 6 CACHE 1;

CREATE TABLE "public"."categorias"
(
    "is_activo" boolean             DEFAULT true,
    "fecha_creacion"                timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    "fecha_actualizacion"           timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    "id"         bigint             DEFAULT nextval('categorias_id_seq') NOT NULL,
    "nombre"     character varying(255)                                  NOT NULL,
    CONSTRAINT "categorias_nombre_key" UNIQUE ("nombre"),
    CONSTRAINT "categorias_pkey" PRIMARY KEY ("id")
) WITH (oids = false);



CREATE TABLE "public"."user_roles"
(
    "user_id" bigint NOT NULL,
    "roles"   character varying(255)
) WITH (oids = false);



CREATE SEQUENCE usuarios_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 4 CACHE 1;

CREATE TABLE "public"."usuarios"
(
    "is_deleted" boolean   DEFAULT false,
    "created_at" timestamp DEFAULT CURRENT_TIMESTAMP          NOT NULL,
    "id"         bigint    DEFAULT nextval('usuarios_id_seq') NOT NULL,
    "updated_at" timestamp DEFAULT CURRENT_TIMESTAMP          NOT NULL,
    "apellidos"  character varying(255)                       NOT NULL,
    "email"      character varying(255)                       NOT NULL,
    "nombre"     character varying(255)                       NOT NULL,
    "password"   character varying(255)                       NOT NULL,
    "username"   character varying(255)                       NOT NULL,
    CONSTRAINT "usuarios_email_key" UNIQUE ("email"),
    CONSTRAINT "usuarios_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "usuarios_username_key" UNIQUE ("username")
) WITH (oids = false);




INSERT INTO "categorias" ("is_activo", "fecha_creacion", "fecha_actualizacion", "id", "nombre")
VALUES ('t', '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 1,
        'SERIE'),
       ('t', '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 2,
        'DISNEY'),
       ('t', '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 3,
        'SUPERHEROES'),
       ('t', '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 4,
        'PELICULAS'),
       ('t', '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 5,
        'OTROS');


INSERT INTO "funkos" ("id", "nombre", "precio", "cantidad", "imagen", "fecha_creacion", "fecha_actualizacion", "is_activo", "categoria_id")
VALUES (1, 'Cristiano Ronaldo', 19.99, 50, 'https://via.placeholder.com/150' , '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 't', 1),
       (2, 'Vinicius Junior', 14.99, 75, 'https://via.placeholder.com/150' , '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 't', 2),
       (3, 'Mbappe', 16.99, 32, 'https://via.placeholder.com/150' , '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 't', 3),
       (4, 'Erling Haarland', 11.99, 29, 'https://via.placeholder.com/150' , '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 't', 4),
       (5, 'Neymar', 13.99, 12, 'https://via.placeholder.com/150' , '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 't', 5),
       (6, 'Messi', 25.99, 14, 'https://via.placeholder.com/150' , '2023-11-29 15:30:45.123456', '2023-11-29 15:30:45.123456', 't', 5);

INSERT INTO "usuarios" ("is_deleted", "created_at", "id", "updated_at", "apellidos", "email", "nombre", "password",
                        "username")
VALUES ('f', '2023-11-29 15:30:45.123456', 1, '2023-11-29 15:30:45.123456', 'Admin Admin', 'admin@prueba.net', 'Admin',
        '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2', 'admin'),
       ('f', '2023-11-29 15:30:45.123456', 2, '2023-11-29 15:30:45.123456', 'User User', 'user@prueba.net', 'User',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.', 'user'),
       ('f', '2023-11-29 15:30:45.123456', 3, '2023-11-29 15:30:45.123456', 'Test Test', 'test@prueba.net', 'Test',
        '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu', 'test');

INSERT INTO "user_roles" ("user_id", "roles")
VALUES (1, 'USER'),
       (1, 'ADMIN'),
       (2, 'USER'),
       (2, 'USER'),
       (3, 'USER');




ALTER TABLE ONLY "public"."funkos"
    ADD CONSTRAINT "fk2fwq10nwymfv7fumctxt9vpgb" FOREIGN KEY (categoria_id) REFERENCES categorias (id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."user_roles"
    ADD CONSTRAINT "fk2chxp26bnpqjibydrikgq4t9e" FOREIGN KEY (user_id) REFERENCES usuarios (id) NOT DEFERRABLE;