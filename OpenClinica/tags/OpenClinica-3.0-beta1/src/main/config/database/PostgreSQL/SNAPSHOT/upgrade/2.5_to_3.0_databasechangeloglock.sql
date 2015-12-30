--
-- PostgreSQL database dump
--

-- Started on 2009-03-09 12:51:04

SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1430 (class 1259 OID 77566)
-- Dependencies: 4
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: clinica; Tablespace: 
--

CREATE TABLE databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp with time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO clinica;

--
-- TOC entry 1896 (class 0 OID 77566)
-- Dependencies: 1430
-- Data for Name: databasechangeloglock; Type: TABLE DATA; Schema: public; Owner: clinica
--

INSERT INTO databasechangeloglock (id, locked, lockgranted, lockedby) VALUES (1, false, NULL, NULL);


--
-- TOC entry 1895 (class 2606 OID 77569)
-- Dependencies: 1430 1430
-- Name: pk_databasechangeloglock; Type: CONSTRAINT; Schema: public; Owner: clinica; Tablespace: 
--

ALTER TABLE ONLY databasechangeloglock
    ADD CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id);


-- Completed on 2009-03-09 12:51:05

--
-- PostgreSQL database dump complete
--

