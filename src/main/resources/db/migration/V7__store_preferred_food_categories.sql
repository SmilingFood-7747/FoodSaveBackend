CREATE TABLE notification_preferred_categories (
    account_id BIGINT NOT NULL REFERENCES notification_preferences(account_id) ON DELETE CASCADE,
    category VARCHAR(40) NOT NULL,
    PRIMARY KEY (account_id, category)
);
