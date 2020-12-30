create database grabber;

create table post(
                     id serial primary key,
                     name varchar(50),
                     text text,
                     link text unique,
                     created timestamp
);