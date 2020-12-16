ALTER TABLE public.product_revision ADD restrictions jsonb;
ALTER TABLE public.product ADD restrictions jsonb;

CREATE OR REPLACE FUNCTION public.trigger_on_product_revision()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO product_revision (source_id, updated_at, data, version,
		    sku, brand, ean, name, supplier, virtual, cost, weight, descriptions, names,  
		    images, tags, restrictions)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.sku, old.brand, old.ean, old.name, old.supplier, old.virtual, old.cost, 
		    old.weight, old.descriptions, old.names, old.images, old.tags, old.restrictions);
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $function$
;
