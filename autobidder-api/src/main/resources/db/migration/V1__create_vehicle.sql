CREATE TABLE IF NOT EXISTS vehicle_manufacturers (
    id UUID PRIMARY KEY,
    url VARCHAR(2048) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicle_models (
    id UUID PRIMARY KEY,
    manufacturer_id UUID NOT NULL REFERENCES vehicle_manufacturers(id) ON DELETE CASCADE,
    url VARCHAR(2048) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    segment VARCHAR(20),
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    UNIQUE(manufacturer_id, name)
);

CREATE TABLE IF NOT EXISTS vehicle_model_generations (
    id UUID PRIMARY KEY,
    vehicle_model_id UUID NOT NULL REFERENCES vehicle_models(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    start_year INT NOT NULL,
    end_year INT,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicle_engine_options (
    id UUID PRIMARY KEY,
    url VARCHAR(2048) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    vehicle_model_generation_id UUID NOT NULL REFERENCES vehicle_model_generations(id) ON DELETE CASCADE,
    fuel_type VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP
);
