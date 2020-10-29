

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

CREATE FUNCTION public.trigger_on_customdata_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO customdata_revision (source_id, updated_at, data, version,
		    data_type, content)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.data_type, old.content);	    
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;

CREATE FUNCTION public.trigger_on_customer_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO customer_revision (source_id, updated_at, data, version,
		    username, email, firstname, lastname, company, vat_number, fiscal_code,
		    date_of_birth, language_code, phone, addresses, groups)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.username, old.email, old.firstname, old.lastname, old.company, old.vat_number, old.fiscal_code,
		    old.date_of_birth, old.language_code, old.phone, old.addresses, old.groups);	    
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;

CREATE FUNCTION public.trigger_on_model_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO model_revision (source_id, product_source_id, updated_at, data, version,
		    sku, ean, name, availability_at, availability, tags, options)
		VALUES (old.source_id, old.product_source_id, old.updated_at, old.data, old.version,
		    old.sku, old.ean, old.name, old.availability_at, old.availability, old.tags, old.options);	    
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;

CREATE FUNCTION public.trigger_on_product_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO product_revision (source_id, updated_at, data, version,
		    sku, brand, ean, name, supplier, virtual, cost, weight, descriptions, names,  
		    images, tags)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.sku, old.brand, old.ean, old.name, old.supplier, old.virtual, old.cost, 
		    old.weight, old.descriptions, old.names, old.images, old.tags);
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE public.cron (
    id bigint NOT NULL,
    completed_time timestamp without time zone,
    error text,
    execution_time timestamp without time zone,
    force_execution boolean,
    name character varying(255),
    node character varying(255),
    scheduled_time timestamp without time zone,
    status integer
);

CREATE SEQUENCE public.cron_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.cron_id_seq OWNED BY public.cron.id;

CREATE TABLE public.customdata (
    source_id character varying NOT NULL,
    product_source_id character varying(1024),
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    data_type character varying NOT NULL,
    content jsonb
);

CREATE TABLE public.customdata_revision (
    source_id character varying NOT NULL,
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    data_type character varying NOT NULL,
    content jsonb
);

CREATE TABLE public.customer (
    source_id character varying NOT NULL,
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    username character varying,
    email character varying,
    firstname character varying,
    lastname character varying,
    company character varying,
    vat_number character varying,
    fiscal_code character varying,
    date_of_birth date,
    language_code character varying,
    phone jsonb,
    addresses jsonb,
    groups jsonb
);

CREATE TABLE public.customer_revision (
    source_id character varying NOT NULL,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    username character varying,
    email character varying,
    firstname character varying,
    lastname character varying,
    company character varying,
    vat_number character varying,
    fiscal_code character varying,
    date_of_birth date,
    language_code character varying,
    phone jsonb,
    addresses jsonb,
    groups jsonb
);

CREATE TABLE public.model (
    source_id character varying NOT NULL,
    product_source_id character varying(1024),
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    sku character varying(1024),
    ean character varying(13),
    name character varying(1024),
    availability_at timestamp without time zone,
    availability integer,
    tags jsonb,
    options jsonb
);

CREATE TABLE public.model_revision (
    source_id character varying NOT NULL,
    product_source_id character varying(1024),
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    sku character varying(1024),
    ean character varying(13),
    name character varying(1024),
    availability_at timestamp without time zone,
    availability integer,
    tags jsonb,
    options jsonb
);

CREATE TABLE public.orders (
    source_id character varying NOT NULL,
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer,
    source character varying,
    status integer,
    customer_source_id character varying,
    payment_gateway character varying,
    currency character varying,
    order_date timestamp without time zone,
    invoice_address jsonb,
    shipping_address jsonb,
    line_items jsonb,
    totals jsonb,
    dispatch_taxable numeric,
    dispatch_vat numeric,
    dispatch_total numeric,
    total numeric,
    total_vat numeric,
    customer_email character varying,
    anagrafica jsonb
);

CREATE TABLE public.param (
    id integer NOT NULL,
    name character varying(255),
    value text
);

CREATE SEQUENCE public.param_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.param_id_seq OWNED BY public.param.id;

CREATE TABLE public.price (
    source_id character varying NOT NULL,
    product_source_id character varying(512) NOT NULL,
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    country character varying,
    listing_group character varying,
    min_quantity integer,
    customer_source_id character varying,
    discounted_price numeric,
    retail_price numeric,
    sell_price numeric,
    suggested_price numeric,
    buy_price numeric
);

CREATE TABLE public.product (
    source_id character varying NOT NULL,
    updated_at timestamp without time zone,
    external_references jsonb,
    data jsonb,
    version integer NOT NULL,
    sku character varying(1024) NOT NULL,
    brand character varying,
    ean character varying,
    name character varying,
    supplier character varying,
    virtual boolean,
    cost numeric,
    weight real,
    descriptions jsonb,
    names jsonb,
    images jsonb,
    tags jsonb,
    online boolean
);

CREATE TABLE public.product_revision (
    source_id character varying NOT NULL,
    updated_at timestamp without time zone,
    data jsonb,
    version integer NOT NULL,
    sku character varying(1024) NOT NULL,
    brand character varying,
    ean character varying,
    name character varying,
    supplier character varying,
    virtual boolean,
    cost numeric,
    weight real,
    descriptions jsonb,
    names jsonb,
    images jsonb,
    tags jsonb
);

CREATE TABLE public.stock (
    source_id character varying NOT NULL,
    model_source_id character varying(512),
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    availability integer,
    supplier character varying,
    warehouse character varying
);

CREATE SEQUENCE public.xroads_id_generator
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.cron ALTER COLUMN id SET DEFAULT nextval('public.cron_id_seq'::regclass);

ALTER TABLE ONLY public.param ALTER COLUMN id SET DEFAULT nextval('public.param_id_seq'::regclass);

ALTER TABLE ONLY public.cron
    ADD CONSTRAINT cron_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.customdata
    ADD CONSTRAINT customdata_pkey PRIMARY KEY (source_id, data_type);

ALTER TABLE ONLY public.customdata_revision
    ADD CONSTRAINT customdata_revision_pkey PRIMARY KEY (source_id, data_type, version);

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (source_id);

ALTER TABLE ONLY public.customer_revision
    ADD CONSTRAINT customer_revision_pkey PRIMARY KEY (source_id, version);

ALTER TABLE ONLY public.model
    ADD CONSTRAINT model_pkey PRIMARY KEY (source_id);

ALTER TABLE ONLY public.model_revision
    ADD CONSTRAINT model_revision_pkey PRIMARY KEY (source_id, version);

ALTER TABLE ONLY public.model
    ADD CONSTRAINT model_sku_unique UNIQUE (sku);

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (source_id);

ALTER TABLE ONLY public.param
    ADD CONSTRAINT param_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.price
    ADD CONSTRAINT price_pkey PRIMARY KEY (source_id);

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (source_id);

ALTER TABLE ONLY public.product_revision
    ADD CONSTRAINT product_revision_pkey PRIMARY KEY (source_id, version);

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_sku_unique UNIQUE (sku);

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_pkey PRIMARY KEY (source_id);

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_sku_unique UNIQUE (model_source_id);

ALTER TABLE ONLY public.param
    ADD CONSTRAINT uk_mbytl9lbga1vxx4iox4pc99w6 UNIQUE (name);

CREATE INDEX customdata_datatype_idx ON public.customdata USING btree (data_type);

CREATE INDEX orders_source_idx ON public.orders USING btree (source_id);

CREATE INDEX price_product_source_id_idx ON public.price USING btree (product_source_id);

CREATE TRIGGER trigger_customdata_revision BEFORE UPDATE ON public.customdata FOR EACH ROW EXECUTE PROCEDURE public.trigger_on_customdata_revision();

CREATE TRIGGER trigger_customer_revision BEFORE UPDATE ON public.customer FOR EACH ROW EXECUTE PROCEDURE public.trigger_on_customer_revision();

CREATE TRIGGER trigger_model_revision BEFORE UPDATE ON public.model FOR EACH ROW EXECUTE PROCEDURE public.trigger_on_model_revision();

CREATE TRIGGER trigger_product_revision BEFORE UPDATE ON public.product FOR EACH ROW EXECUTE PROCEDURE public.trigger_on_product_revision();

