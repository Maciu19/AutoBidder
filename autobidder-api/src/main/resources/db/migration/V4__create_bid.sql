CREATE TABLE bids (
    id UUID PRIMARY KEY,
    auction_id UUID NOT NULL,
    user_id UUID NOT NULL,
    bid_amount NUMERIC(19, 2) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE,
    last_modified_date TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_bid_auction FOREIGN KEY (auction_id) REFERENCES auction(id) ON DELETE CASCADE,
    CONSTRAINT fk_bid_user FOREIGN KEY (user_id) REFERENCES users(id)
);