SET search_path = 'finalhttpserver.public';
DROP TABLE public.statistics;
CREATE TABLE IF NOT EXISTS public.statistics
(
    id SERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    method VARCHAR(10) NOT NULL,
    uri VARCHAR(256) NOT NULL,
    protocol VARCHAR(12) NOT NULL,
    remote_address VARCHAR(64),
    accept VARCHAR(256),
    content_type VARCHAR(48),
    billed BOOLEAN NOT NULL DEFAULT FALSE
);