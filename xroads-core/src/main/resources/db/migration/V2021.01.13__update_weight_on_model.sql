update model set weight = (select product.weight from product where model.product_source_id = product.source_id);

CREATE OR REPLACE FUNCTION public.trigger_on_product_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO product_revision (source_id, updated_at, data, version,
		    sku, brand, ean, name, supplier, virtual, cost, descriptions, names,  
		    images, tags)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.sku, old.brand, old.ean, old.name, old.supplier, old.virtual, old.cost, 
		    old.descriptions, old.names, old.images, old.tags);
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;

ALTER TABLE product DROP COLUMN weight;
ALTER TABLE product_revision DROP COLUMN weight;