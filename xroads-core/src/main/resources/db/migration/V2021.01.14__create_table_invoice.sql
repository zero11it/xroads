CREATE TABLE public.invoices (
    source_id character varying NOT NULL,
    external_references jsonb,
    data jsonb,
    updated_at timestamp without time zone,
    version integer NOT NULL,
    document_type character varying,
    customer_source_id character varying,
    invoice_number character varying,
    vat_number character varying,
    year integer,
    line_items jsonb,
    totals jsonb
);