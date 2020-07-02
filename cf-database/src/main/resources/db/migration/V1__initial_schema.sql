-- User
CREATE TABLE users (
  id                       SERIAL PRIMARY KEY,
  tg_username              VARCHAR(255) NOT NULL,
  reg_date                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  UNIQUE (tg_username)
);

CREATE TABLE user_codes (
  user_id                   INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  code                      VARCHAR(120) NOT NULL,
  creation_time             TIMESTAMP NOT NULL,
  UNIQUE (user_id)
);

-- Project

CREATE TABLE projects (
  id                       SERIAL PRIMARY KEY,
  name                     VARCHAR(255) NOT NULL,
  location                 point,
  description              TEXT,
  creation_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  owner_id                 INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  UNIQUE (name)
);

CREATE TABLE project_subscribers (
  project_id                   INT REFERENCES projects (id) ON DELETE CASCADE NOT NULL,
  user_id                   INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  UNIQUE (user_id, project_id)
);

CREATE TABLE activities (
  id                       SERIAL PRIMARY KEY,
  name                     VARCHAR(255) NOT NULL,
  description              TEXT,
  creation_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  start_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  end_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  project_id                 INT REFERENCES projects (id) ON DELETE CASCADE NOT NULL,
  UNIQUE (name)
);

CREATE TABLE activity_participants (
  activity_id                   INT REFERENCES activities (id) ON DELETE CASCADE NOT NULL,
   user_id                   INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
   UNIQUE (user_id, activity_id)
);