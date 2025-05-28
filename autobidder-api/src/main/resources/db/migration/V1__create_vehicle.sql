CREATE TABLE IF NOT EXISTS vehicle_manufacturers (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicle_models (
    id UUID PRIMARY KEY,
    manufacturer_id UUID NOT NULL REFERENCES vehicle_manufacturers(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    segment VARCHAR(20),
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    UNIQUE(manufacturer_id, name)
);

CREATE TABLE IF NOT EXISTS vehicle_model_generations (
    id UUID PRIMARY KEY,
    vehicle_model_id UUID NOT NULL REFERENCES vehicle_models(id) ON DELETE CASCADE,
    generation_name VARCHAR(50),
    start_year INT NOT NULL,
    end_year INT,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    UNIQUE(vehicle_model_id, generation_name, start_year)
);

CREATE TABLE IF NOT EXISTS vehicle_engine_options (
    id UUID PRIMARY KEY,
    model_generation_id UUID NOT NULL REFERENCES vehicle_model_generations(id) ON DELETE CASCADE,
    cylinders INT,
    name VARCHAR(100),
    displacement INT,
    fuel_type VARCHAR(50),
    transmission VARCHAR(100),
    drivetrain VARCHAR(50),
    power_hp INT,
    torque_nm INT,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    UNIQUE(model_generation_id, name)
);
