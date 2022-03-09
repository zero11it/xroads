ALTER TABLE public.orders add column properties jsonb;
update  public.orders set properties = '[]'::jsonb;