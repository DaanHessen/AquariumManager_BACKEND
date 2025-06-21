-- AquariumManager Database Schema
-- PostgreSQL DDL for complete aquarium management system

-- Drop tables in reverse dependency order (child first, parent last)
DROP TABLE IF EXISTS inhabitant_aquarium_history;
DROP TABLE IF EXISTS aquarium_state_history;
DROP TABLE IF EXISTS inhabitants;
DROP TABLE IF EXISTS accessories;
DROP TABLE IF EXISTS ornaments;
DROP TABLE IF EXISTS aquariums;
DROP TABLE IF EXISTS owners;
DROP TABLE IF EXISTS aquarium_managers;

-- Create sequences for auto-incrementing IDs
CREATE SEQUENCE IF NOT EXISTS aquarium_manager_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS owner_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS aquarium_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS accessory_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS ornament_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS inhabitant_id_seq START 1;

-- Aquarium Managers (Root aggregate)
CREATE TABLE aquarium_managers (
    id BIGINT PRIMARY KEY DEFAULT nextval('aquarium_manager_id_seq'),
    installation_date DATE NOT NULL,
    description TEXT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Owners (Users)
CREATE TABLE owners (
    id BIGINT PRIMARY KEY DEFAULT nextval('owner_id_seq'),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'OWNER' CHECK (role IN ('OWNER', 'ADMIN')),
    last_login TIMESTAMP,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    aquarium_manager_id BIGINT REFERENCES aquarium_managers(id) ON DELETE SET NULL
);

-- Aquariums
CREATE TABLE aquariums (
    id BIGINT PRIMARY KEY DEFAULT nextval('aquarium_id_seq'),
    name VARCHAR(255) NOT NULL,
    length DOUBLE PRECISION NOT NULL CHECK (length > 0),
    width DOUBLE PRECISION NOT NULL CHECK (width > 0),
    height DOUBLE PRECISION NOT NULL CHECK (height > 0),
    substrate VARCHAR(50) NOT NULL CHECK (substrate IN ('SAND', 'GRAVEL', 'PEBBLES', 'BARE_BOTTOM', 'PLANTED')),
    water_type VARCHAR(20) NOT NULL CHECK (water_type IN ('FRESHWATER', 'SALTWATER', 'BRACKISH')),
    temperature DOUBLE PRECISION DEFAULT 24.0 CHECK (temperature >= 0),
    state VARCHAR(20) NOT NULL DEFAULT 'SETUP' CHECK (state IN ('SETUP', 'RUNNING', 'MAINTENANCE', 'INACTIVE')),
    current_state_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    color VARCHAR(100),
    description TEXT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
    aquarium_manager_id BIGINT REFERENCES aquarium_managers(id) ON DELETE SET NULL
);

-- Accessories (Equipment)
CREATE TABLE accessories (
    id BIGINT PRIMARY KEY DEFAULT nextval('accessory_id_seq'),
    accessory_type VARCHAR(20) NOT NULL CHECK (accessory_type IN ('Filter', 'Lighting', 'Thermostat')),
    model VARCHAR(255) NOT NULL,
    serial_number VARCHAR(255),
    color VARCHAR(100),
    description TEXT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
    aquarium_id BIGINT REFERENCES aquariums(id) ON DELETE SET NULL,
    
    -- Filter-specific properties
    is_external BOOLEAN DEFAULT FALSE,
    capacity_liters DOUBLE PRECISION,
    
    -- Lighting-specific properties
    is_led BOOLEAN DEFAULT FALSE,
    time_on TIME,
    time_off TIME,
    
    -- Thermostat-specific properties
    min_temperature DOUBLE PRECISION,
    max_temperature DOUBLE PRECISION,
    current_temperature DOUBLE PRECISION
);

-- Ornaments (Decorations)
CREATE TABLE ornaments (
    id BIGINT PRIMARY KEY DEFAULT nextval('ornament_id_seq'),
    name VARCHAR(255) NOT NULL,
    material VARCHAR(100),
    size VARCHAR(50),
    color VARCHAR(100),
    description TEXT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
    aquarium_id BIGINT REFERENCES aquariums(id) ON DELETE SET NULL
);

-- Inhabitants (Fish, Snails, etc.)
CREATE TABLE inhabitants (
    id BIGINT PRIMARY KEY DEFAULT nextval('inhabitant_id_seq'),
    inhabitant_type VARCHAR(20) NOT NULL CHECK (inhabitant_type IN ('Fish', 'Snail', 'Shrimp', 'Crayfish', 'Plant', 'Coral')),
    species VARCHAR(255) NOT NULL,
    color VARCHAR(100),
    count INTEGER NOT NULL CHECK (count > 0),
    is_schooling BOOLEAN DEFAULT FALSE,
    water_type VARCHAR(20) NOT NULL CHECK (water_type IN ('FRESHWATER', 'SALTWATER', 'BRACKISH')),
    name VARCHAR(255),
    description TEXT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
    aquarium_id BIGINT REFERENCES aquariums(id) ON DELETE SET NULL,
    
    -- Type-specific properties (used by different species)
    is_aggressive_eater BOOLEAN DEFAULT FALSE,
    requires_special_food BOOLEAN DEFAULT FALSE,
    is_snail_eater BOOLEAN DEFAULT FALSE
);

-- Aquarium State History (for tracking state changes)
CREATE TABLE aquarium_state_history (
    id BIGSERIAL PRIMARY KEY,
    aquarium_id BIGINT NOT NULL REFERENCES aquariums(id) ON DELETE CASCADE,
    from_state VARCHAR(20) NOT NULL,
    to_state VARCHAR(20) NOT NULL,
    duration_minutes BIGINT,
    transition_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

-- Inhabitant Assignment History (for tracking aquarium assignments)
CREATE TABLE inhabitant_aquarium_history (
    id BIGSERIAL PRIMARY KEY,
    inhabitant_id BIGINT NOT NULL REFERENCES inhabitants(id) ON DELETE CASCADE,
    aquarium_id BIGINT REFERENCES aquariums(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    removed_at TIMESTAMP,
    reason VARCHAR(255)
);

-- Indexes for performance
CREATE INDEX idx_aquariums_owner_id ON aquariums(owner_id);
CREATE INDEX idx_aquariums_state ON aquariums(state);
CREATE INDEX idx_accessories_owner_id ON accessories(owner_id);
CREATE INDEX idx_accessories_aquarium_id ON accessories(aquarium_id);
CREATE INDEX idx_ornaments_owner_id ON ornaments(owner_id);
CREATE INDEX idx_ornaments_aquarium_id ON ornaments(aquarium_id);
CREATE INDEX idx_inhabitants_owner_id ON inhabitants(owner_id);
CREATE INDEX idx_inhabitants_aquarium_id ON inhabitants(aquarium_id);
CREATE INDEX idx_inhabitants_type ON inhabitants(inhabitant_type);
CREATE INDEX idx_owners_email ON owners(email);
CREATE INDEX idx_state_history_aquarium_id ON aquarium_state_history(aquarium_id);
CREATE INDEX idx_inhabitant_history_inhabitant_id ON inhabitant_aquarium_history(inhabitant_id);

-- Insert initial data
INSERT INTO aquarium_managers (installation_date, description) 
VALUES (CURRENT_DATE, 'Default AquariumManager installation');

-- Insert default admin user (password: admin123)
INSERT INTO owners (first_name, last_name, email, password, role, aquarium_manager_id)
VALUES ('Admin', 'User', 'admin@aquarium.com', '$2a$10$XQqPOVkDYFk.JKV5LKZGoe1sTVoDbMNkrNMHJ5OPZf2hxYg3ZW2Nu', 'ADMIN', 1);

-- Insert test owner (password: test123)
INSERT INTO owners (first_name, last_name, email, password, role, aquarium_manager_id)
VALUES ('Test', 'Owner', 'test@aquarium.com', '$2a$10$e8h4q2VfAzv3u.TgXm7hYOHkGX4hjI4fL.CaHu.WVqKYqUB8ys.q2', 'OWNER', 1);
