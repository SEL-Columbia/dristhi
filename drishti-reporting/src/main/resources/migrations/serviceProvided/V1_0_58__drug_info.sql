--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = report, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: drug_info; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE drug_info (
    id integer NOT NULL,
    drug_name character varying(100) NOT NULL,
    frequency integer,
    dosage integer,
    direction integer,
    active boolean NOT NULL,
    anc_conditions character varying(200) DEFAULT NULL::character varying,
    pnc_conditions character varying(200) DEFAULT NULL::character varying,
    child_illness character varying(200) DEFAULT NULL::character varying
);


ALTER TABLE drug_info OWNER TO dhanush;

--
-- Name: drug_info_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE drug_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE drug_info_id_seq OWNER TO dhanush;

--
-- Name: drug_info_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE drug_info_id_seq OWNED BY drug_info.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY drug_info ALTER COLUMN id SET DEFAULT nextval('drug_info_id_seq'::regclass);


--
-- Name: drug_info_drug_name_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY drug_info
    ADD CONSTRAINT drug_info_drug_name_key UNIQUE (drug_name);


--
-- Name: drug_info_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY drug_info
    ADD CONSTRAINT drug_info_pkey PRIMARY KEY (id);


--
-- Name: drug_info_direction; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX drug_info_direction ON drug_info USING btree (direction);


--
-- Name: drug_info_dosage; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX drug_info_dosage ON drug_info USING btree (dosage);


--
-- Name: drug_info_frequency; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX drug_info_frequency ON drug_info USING btree (frequency);


--
-- Name: drug_info_direction_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY drug_info
    ADD CONSTRAINT drug_info_direction_fkey FOREIGN KEY (direction) REFERENCES directions(id) ON DELETE SET NULL;


--
-- Name: drug_info_dosage_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY drug_info
    ADD CONSTRAINT drug_info_dosage_fkey FOREIGN KEY (dosage) REFERENCES dosage(id) ON DELETE SET NULL;


--
-- Name: drug_info_frequency_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY drug_info
    ADD CONSTRAINT drug_info_frequency_fkey FOREIGN KEY (frequency) REFERENCES frequency(id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

