ALTER TABLE ONLY public.invoices ADD CONSTRAINT invoices_pkey PRIMARY KEY (source_id);
DROP INDEX orders_source_idx;

update customer set data = replace(data::text,'"enabled": true', '"status": 2')::jsonb where (data ->> 'enabled')::bool = true;
update customer set data = replace(data::text,'"enabled": false', '"status": 3')::jsonb where (data ->> 'enabled')::bool = false;