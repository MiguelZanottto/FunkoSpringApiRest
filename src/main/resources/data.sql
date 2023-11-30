

INSERT INTO CATEGORIAS (nombre)
VALUES ('SERIE'),
       ('DISNEY'),
       ('SUPERHEROES'),
       ('PELICULAS'),
       ('OTROS');

INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Cristiano Ronaldo', 19.99, 50, 'https://via.placeholder.com/150', 1);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Vinicius Junior', 14.99, 75, 'https://via.placeholder.com/150', 1);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Mbappe', 16.99, 32, 'https://via.placeholder.com/150', 1);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Erling Haarland', 11.99, 29, 'https://via.placeholder.com/150', 1);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Neymar', 13.99, 12, 'https://via.placeholder.com/150', 1);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Messi', 25.99, 14, 'https://via.placeholder.com/150', 1);

-- Contraseña: Admin1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Admin', 'Admin Admin', 'admin', 'admin@prueba.net',
        '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2');

insert into USER_ROLES (user_id, roles)
values (1, 'USER');
insert into USER_ROLES (user_id, roles)
values (1, 'ADMIN');

-- Contraseña: User1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('User', 'User User', 'user', 'user@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.');
insert into USER_ROLES (user_id, roles)
values (2, 'USER');


-- Contraseña: Test1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Test', 'Test Test', 'test', 'test@prueba.net',
        '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu');
insert into USER_ROLES (user_id, roles)
values (3, 'USER');