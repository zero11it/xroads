ALTER TABLE public.product_revision ADD hidden_from_catalog boolean;
ALTER TABLE public.product ADD hidden_from_catalog boolean;

CREATE OR REPLACE FUNCTION public.trigger_on_product_revision()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO product_revision (source_id, updated_at, data, version,
		    sku, brand, ean, name, supplier, virtual, cost, descriptions, names,  
		    images, tags, restrictions, urlkeys, option1, option2, option3, hidden_from_catalog)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.sku, old.brand, old.ean, old.name, old.supplier, old.virtual, old.cost, 
		    old.descriptions, old.names, old.images, old.tags, old.restrictions, old.urlkeys,
		    old.option1, old.option2, old.option3, old.hidden_from_catalog);
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $function$
;