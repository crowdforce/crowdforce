CREATE TABLE goals (
  id                       SERIAL PRIMARY KEY,
  name                     VARCHAR(255) NOT NULL,
  description              TEXT,
  creation_time            TIMESTAMP NOT NULL,
  project_id               INT REFERENCES projects (id) ON DELETE CASCADE NOT NULL,
  progress_bar             INT DEFAULT 0
);