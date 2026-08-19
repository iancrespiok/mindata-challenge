CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
               'hotel-search-analyze',
               '*/5 * * * *',
               $$
                   DO $do$
    BEGIN
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'hotel_search') THEN
            EXECUTE 'ANALYZE hotel_search';
END IF;
END
    $do$;
    $$
);