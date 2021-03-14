-- Each user may have multiple identities
CREATE TABLE user_identities
(
    identity_type VARCHAR(255) NOT NULL,
    identity_id   VARCHAR(255) NOT NULL,
    user_id       INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (identity_type, identity_id)
);

-- No need direct tg_id in the users table
ALTER TABLE users
DROP COLUMN IF EXISTS tg_id CASCADE

