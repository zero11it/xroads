update product_revision set restrictions  = '{}'::jsonb where restrictions is null;
update product set restrictions  = '{}'::jsonb where restrictions is null;