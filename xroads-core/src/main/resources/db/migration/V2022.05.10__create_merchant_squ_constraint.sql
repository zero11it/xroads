ALTER TABLE public.model DROP CONSTRAINT model_sku_unique;
CREATE UNIQUE INDEX model_sku_unique ON public.model USING btree (sku) where merchant_code is NULL;
CREATE UNIQUE INDEX model_merchant_sku_unique ON public.model USING btree (sku, merchant_code) where merchant_code is NOT NULL;