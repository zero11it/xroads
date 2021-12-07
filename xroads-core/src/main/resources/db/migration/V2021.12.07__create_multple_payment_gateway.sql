ALTER TABLE public.orders ADD payments jsonb;
update  public.orders set payments = '[]'::jsonb;