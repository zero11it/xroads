ALTER TABLE public.product_revision ADD option1 jsonb;
ALTER TABLE public.product_revision ADD option2 jsonb;
ALTER TABLE public.product_revision ADD option3 jsonb;
ALTER TABLE public.product ADD option1 jsonb;
ALTER TABLE public.product ADD option2 jsonb;
ALTER TABLE public.product ADD option3 jsonb;

update public.product_revision set option1 = '{}' where option1 IS NULL;
update public.product_revision set option2 = '{}' where option2 IS NULL;
update public.product_revision set option3 = '{}' where option3 IS NULL;
update public.product set option1 = '{}' where option1 IS NULL;
update public.product set option2 = '{}' where option2 IS NULL;
update public.product set option3 = '{}' where option3 IS NULL;

CREATE OR REPLACE FUNCTION public.trigger_on_product_revision()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO product_revision (source_id, updated_at, data, version,
		    sku, brand, ean, name, supplier, virtual, cost, descriptions, names,  
		    images, tags, restrictions, urlkeys, option1, option2, option3)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.sku, old.brand, old.ean, old.name, old.supplier, old.virtual, old.cost, 
		    old.descriptions, old.names, old.images, old.tags, old.restrictions, old.urlkeys,
		    old.option1, old.option2, old.option3);
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $function$
;