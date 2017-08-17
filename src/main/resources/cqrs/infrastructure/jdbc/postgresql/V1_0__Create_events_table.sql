CREATE TABLE IF NOT EXISTS events (
    aggregateId       char(36) NOT NULL,
    aggregateType     varchar(100) NOT NULL,
    eventType         varchar(100) NOT NULL,
    version           integer NOT NULL,
    data              TEXT NOT NULL,
    sequence          serial NOT NULL,
    CONSTRAINT ix_aggregate_events PRIMARY KEY (aggregateId, aggregateType, version)
);

CREATE INDEX idx_aggregate_id ON events (aggregateId);