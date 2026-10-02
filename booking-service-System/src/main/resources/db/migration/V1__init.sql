CREATE TABLE users (
                       id            BIGSERIAL PRIMARY KEY,
                       full_name     VARCHAR(100) NOT NULL,
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(100) NOT NULL,
                       role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE centres (
                         id       BIGSERIAL PRIMARY KEY,
                         name     VARCHAR(150) NOT NULL,
                         location VARCHAR(255) NOT NULL,
                         UNIQUE (name, location)
);

CREATE TABLE diagnostic_tests (
                                  id          BIGSERIAL PRIMARY KEY,
                                  name        VARCHAR(150) NOT NULL UNIQUE,
                                  description TEXT
);

CREATE TABLE centre_tests (
                              id        BIGSERIAL PRIMARY KEY,
                              centre_id BIGINT NOT NULL REFERENCES centres(id),
                              test_id   BIGINT NOT NULL REFERENCES diagnostic_tests(id),
                              price     NUMERIC(10,2) NOT NULL CHECK (price > 0),
                              UNIQUE (centre_id, test_id)
);

CREATE TABLE bookings (
                          id             BIGSERIAL PRIMARY KEY,
                          user_id        BIGINT NOT NULL REFERENCES users(id),
                          centre_test_id BIGINT NOT NULL REFERENCES centre_tests(id),
                          appointment_at TIMESTAMPTZ NOT NULL,
                          amount         NUMERIC(10,2) NOT NULL CHECK (amount > 0),
                          status         VARCHAR(20) NOT NULL
                              CHECK (status IN ('PENDING','CONFIRMED','FAILED','CANCELLED')),
                          version        BIGINT NOT NULL DEFAULT 0,
                          created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_bookings_user_id ON bookings(user_id);

CREATE TABLE payments (
                          id         BIGSERIAL PRIMARY KEY,
                          booking_id BIGINT NOT NULL REFERENCES bookings(id),
                          amount     NUMERIC(10,2) NOT NULL CHECK (amount > 0),
                          status     VARCHAR(20) NOT NULL CHECK (status IN ('SUCCESS','FAILED')),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_payments_booking_id ON payments(booking_id);

CREATE TABLE webhook_events (
                                id          BIGSERIAL PRIMARY KEY,
                                event_id    VARCHAR(100) NOT NULL UNIQUE,
                                booking_id  BIGINT NOT NULL REFERENCES bookings(id),
                                status      VARCHAR(20) NOT NULL,
                                received_at TIMESTAMPTZ NOT NULL DEFAULT now()
);