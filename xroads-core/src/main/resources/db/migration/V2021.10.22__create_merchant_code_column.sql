ALTER TABLE public.model_revision ADD merchant_code character varying(255);
ALTER TABLE public.model ADD merchant_code character varying(255);

CREATE OR REPLACE FUNCTION public.trigger_on_model_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO model_revision (source_id, product_source_id, updated_at, data, version,
		    sku, ean, additional_barcode, weight, name, availability_at, availability, tags, options, merchant_code)
		VALUES (old.source_id, old.product_source_id, old.updated_at, old.data, old.version,
		    old.sku, old.ean, old.additional_barcode, old.weight, old.name, old.availability_at, old.availability, old.tags, old.options, old.merchant_code);	    
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;