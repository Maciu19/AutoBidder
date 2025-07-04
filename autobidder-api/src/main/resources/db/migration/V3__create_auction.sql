CREATE TABLE auction (
  id UUID PRIMARY KEY,

  seller_id UUID NOT NULL,
  vehicle_engine_option_id UUID NOT NULL,
  winning_user_id UUID,

  vin VARCHAR(255) NOT NULL UNIQUE,
  location VARCHAR(255),
  make_year SMALLINT,
  mileage INTEGER,
  exterior_color VARCHAR(255),
  interior_color VARCHAR(255),
  steering_wheel_side VARCHAR(50),
  has_warranty BOOLEAN,
  no_crash_registered BOOLEAN,

  title VARCHAR(255),
  starting_price NUMERIC(19, 2),
  current_price NUMERIC(19, 2),
  start_time TIMESTAMP WITHOUT TIME ZONE,
  end_time TIMESTAMP WITHOUT TIME ZONE,
  status VARCHAR(50),

  description TEXT,
  modifications TEXT,
  known_flaws TEXT,
  recent_service_history TEXT,
  other_items_included TEXT,
  ownership_history TEXT,

  created_date TIMESTAMP WITH TIME ZONE,
  last_modified_date TIMESTAMP WITH TIME ZONE,

  CONSTRAINT fk_auction_seller FOREIGN KEY (seller_id) REFERENCES users(id),
  CONSTRAINT fk_auction_winner FOREIGN KEY (winning_user_id) REFERENCES users(id),
  CONSTRAINT fk_auction_vehicle_engine_option FOREIGN KEY (vehicle_engine_option_id) REFERENCES vehicle_engine_options (id)
);

CREATE TABLE media_assets (
  id BIGSERIAL PRIMARY KEY,
  auction_id UUID NOT NULL,
  file_url VARCHAR(2048) NOT NULL,
  file_type VARCHAR(50) NOT NULL,
  title VARCHAR(255),
  display_order INTEGER DEFAULT 0,
  created_date TIMESTAMP WITH TIME ZONE,
  CONSTRAINT fk_media_asset_auction FOREIGN KEY (auction_id) REFERENCES auction (id) ON DELETE CASCADE
);

CREATE TABLE auction_features (
  auction_id UUID NOT NULL,
  feature VARCHAR(255) NOT NULL,

  PRIMARY KEY (auction_id, feature),
  CONSTRAINT fk_feature_auction FOREIGN KEY (auction_id) REFERENCES auction (id) ON DELETE CASCADE
);