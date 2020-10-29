ALTER TABLE public.model ADD additional_barcode TEXT[];
ALTER TABLE public.model ADD weight NUMERIC;
ALTER TABLE public.model_revision ADD additional_barcode TEXT[];
ALTER TABLE public.model_revision ADD weight NUMERIC;

CREATE OR REPLACE FUNCTION public.trigger_on_model_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO model_revision (source_id, product_source_id, updated_at, data, version,
		    sku, ean, additional_barcode, weight, name, availability_at, availability, tags, options)
		VALUES (old.source_id, old.product_source_id, old.updated_at, old.data, old.version,
		    old.sku, old.ean, old.additional_barcode, old.weight, old.name, old.availability_at, old.availability, old.tags, old.options);	    
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;

update model set weight = (select product.weight from product where model.product_source_id = product.source_id);