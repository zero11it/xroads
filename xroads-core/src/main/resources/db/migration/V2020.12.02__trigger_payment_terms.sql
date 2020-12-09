CREATE OR REPLACE FUNCTION public.trigger_on_customer_revision() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF old.version <> new.version THEN
		INSERT INTO customer_revision (source_id, updated_at, data, version,
		    username, email, firstname, lastname, company, vat_number, fiscal_code,
		    date_of_birth, language_code, phone, addresses, groups, payment_terms)
		VALUES (old.source_id, old.updated_at, old.data, old.version,
		    old.username, old.email, old.firstname, old.lastname, old.company, old.vat_number, old.fiscal_code,
		    old.date_of_birth, old.language_code, old.phone, old.addresses, old.groups, old.payment_terms);	    
    END IF;
    
    -- Return the `NEW` record so that update can carry on as usual
    return new;
END; $$;