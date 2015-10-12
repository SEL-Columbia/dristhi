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
-- Name: user_masters_new; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE user_masters_new (
    id integer NOT NULL,
    user_role character varying(200) NOT NULL,
    user_id character varying(200) NOT NULL,
    name character varying(200) NOT NULL,
    password character varying(200) NOT NULL,
    confirm_password character varying(200) NOT NULL,
    phone_number character varying(20) NOT NULL,
    email character varying(200) NOT NULL,
    subcenter integer DEFAULT 0,
    villages character varying(200) NOT NULL,
    lastname character varying(200) NOT NULL,
    active boolean NOT NULL,
    hospital integer DEFAULT 0,
    county integer,
    district integer,
    subdistrict integer,
    country integer
);


ALTER TABLE user_masters_new OWNER TO dhanush;

--
-- Name: user_masters_new_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE user_masters_new_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_masters_new_id_seq OWNER TO dhanush;

--
-- Name: user_masters_new_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE user_masters_new_id_seq OWNED BY user_masters_new.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY user_masters_new ALTER COLUMN id SET DEFAULT nextval('user_masters_new_id_seq'::regclass);


--
-- Name: user_masters_new_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_pkey PRIMARY KEY (id);


--
-- Name: user_masters_new_user_id_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_user_id_key UNIQUE (user_id);


--
-- Name: user_masters_new_hospital; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX user_masters_new_hospital ON user_masters_new USING btree (hospital);


--
-- Name: user_masters_new_subcenter; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX user_masters_new_subcenter ON user_masters_new USING btree (subcenter);


--
-- Name: user_masters_new_user_id_like; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX user_masters_new_user_id_like ON user_masters_new USING btree (user_id varchar_pattern_ops);


--
-- Name: user_masters_new_country_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_country_fkey FOREIGN KEY (country) REFERENCES country_tb(id);


--
-- Name: user_masters_new_county_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_county_fkey FOREIGN KEY (county) REFERENCES county_tb(id);


--
-- Name: user_masters_new_district_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_district_fkey FOREIGN KEY (district) REFERENCES district_new(id);


--
-- Name: user_masters_new_hospital_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_hospital_fkey FOREIGN KEY (hospital) REFERENCES health_centers_new(id);


--
-- Name: user_masters_new_subcenter_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_subcenter_fkey FOREIGN KEY (subcenter) REFERENCES health_centers_new(id);


--
-- Name: user_masters_new_subdistrict_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY user_masters_new
    ADD CONSTRAINT user_masters_new_subdistrict_fkey FOREIGN KEY (subdistrict) REFERENCES subdistrict_new(id);


--
-- PostgreSQL database dump complete
--

