-- 
-- OpenClinica Role & Database Creation Script
-- If the role already exists you will get : ERROR: role "clinica" already exists SQL state: 42710
-- If the database already exists you will get : ERROR: database "openclinica" already exists SQL state: 42P04
-- 
CREATE ROLE clinica LOGIN
  ENCRYPTED PASSWORD 'clinica'
  SUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;

CREATE DATABASE  openclinica
  WITH ENCODING='UTF8'
       OWNER=clinica;