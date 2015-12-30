--
-- Name: ruleset; Type: TABLE; Schema: public; Owner: clinica; Tablespace: 
--

CREATE TABLE ruleset (
    ruleset_id serial NOT NULL,
    enabled boolean,
    event_crf_id numeric,
    owner_id numeric,
    date_created date,
    date_updated date,
    update_id numeric,
    status_id numeric
);


ALTER TABLE public.ruleset OWNER TO clinica;

--
-- Name: ruleset_ruleset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clinica
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('ruleset', 'ruleset_id'), 1, false);


--
-- Name: ruleset_id_pkey; Type: CONSTRAINT; Schema: public; Owner: clinica; Tablespace: 
--

ALTER TABLE ONLY ruleset
    ADD CONSTRAINT ruleset_id_pkey PRIMARY KEY (ruleset_id);


--
-- Name: rule; Type: TABLE; Schema: public; Owner: clinica; Tablespace: 
--

CREATE TABLE rule (
	rule_id serial NOT NULL,
	oid character varying(255) NOT NULL,
	name character varying(255),
	description character varying(255),
	enabled boolean, 
	expression_id numeric NOT NULL,	
    owner_id numeric,
    date_created date,
    date_updated date,
    update_id numeric,
    status_id numeric
);


ALTER TABLE public.rule OWNER TO clinica;

--
-- Name: rule_rule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clinica
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('rule', 'rule_id'), 1, false);



--
-- Name: rule_id_pkey; Type: CONSTRAINT; Schema: public; Owner: clinica; Tablespace: 
--

ALTER TABLE ONLY rule
    ADD CONSTRAINT rule_id_pkey PRIMARY KEY (rule_id);
    

--
-- Name: expression; Type: TABLE; Schema: public; Owner: clinica; Tablespace: 
--

CREATE TABLE expression (
	expression_id serial NOT NULL,
	context numeric NOT NULL,
	value character varying(255),
    owner_id numeric,
    date_created date,
    date_updated date,
    update_id numeric,
    status_id numeric
);


ALTER TABLE public.expression  OWNER TO clinica;

--
-- Name: expression_expression_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clinica
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('expression', 'expression_id'), 1, false);



--
-- Name: expression_id_pkey; Type: CONSTRAINT; Schema: public; Owner: clinica; Tablespace: 
--

ALTER TABLE ONLY rule
    ADD CONSTRAINT expression_id_pkey PRIMARY KEY (expression_id);
    

