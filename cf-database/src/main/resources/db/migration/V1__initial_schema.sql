-- User
CREATE TABLE users (
  id                       SERIAL PRIMARY KEY,
  name                     VARCHAR(255) NOT NULL,
  tg_id                    INT NOT NULL,
  reg_date                 TIMESTAMP NOT NULL,
  UNIQUE (tg_id)
);

-- Project

CREATE TABLE projects (
  id                       SERIAL PRIMARY KEY,
  name                     VARCHAR(255) NOT NULL,
  location                 point,
  description              TEXT,
  creation_time            TIMESTAMP NOT NULL,
  owner_id                 INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  UNIQUE (name)
);

CREATE TABLE project_subscribers (
  project_id                INT REFERENCES projects (id) ON DELETE CASCADE NOT NULL,
  user_id                   INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  UNIQUE (user_id, project_id)
);

CREATE TABLE activities (
  id                       SERIAL PRIMARY KEY,
  name                     VARCHAR(255) NOT NULL,
  description              TEXT,
  creation_time            TIMESTAMP NOT NULL,
  start_time               TIMESTAMP  NOT NULL,
  end_time                 TIMESTAMP NOT NULL,
  project_id               INT REFERENCES projects (id) ON DELETE CASCADE NOT NULL,
  UNIQUE (name)
);

CREATE TABLE activity_participants (
   activity_id             INT REFERENCES activities (id) ON DELETE CASCADE NOT NULL,
   user_id                 INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
   UNIQUE (user_id, activity_id)
);

CREATE TABLE goals (
  id                       SERIAL PRIMARY KEY,
  name                     VARCHAR(255) NOT NULL,
  description              TEXT,
  creation_time            TIMESTAMP NOT NULL,
  project_id               INT REFERENCES projects (id) ON DELETE CASCADE NOT NULL,
  progress_bar             INT DEFAULT 0
);

CREATE TABLE trackable_item (
 id                       SERIAL PRIMARY KEY,
 name                     VARCHAR(255) NOT NULL,
 activity_id              INT REFERENCES activities (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE trackable_item_participants (
   trackable_item_id          INT REFERENCES trackable_item (id) ON DELETE CASCADE NOT NULL,
   user_id                 INT REFERENCES users (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE trackable_item_event_prototype (
 id                       SERIAL PRIMARY KEY,
 message                  VARCHAR(255) NOT NULL,
 start_time               TIMESTAMP  NOT NULL,
 recurring                VARCHAR(255) NOT NULL,
 trackable_item_id        INT REFERENCES trackable_item (id) ON DELETE CASCADE NOT NULL,
 participants_number        INT NOT NULL
);

CREATE TABLE trackable_item_event (
 id                       SERIAL PRIMARY KEY,
 message                  VARCHAR(255) NOT NULL,
 trackable_item_id        INT REFERENCES trackable_item (id) ON DELETE CASCADE NOT NULL,
 trackable_item_event_prototype_id        INT REFERENCES trackable_item_event_prototype (id) ON DELETE CASCADE NOT NULL,
 event_time               TIMESTAMP  NOT NULL,
 participants_number        INT NOT NULL
);

CREATE TABLE trackable_item_event_participants (
 id                       SERIAL PRIMARY KEY,
 trackable_item_event_id  INT REFERENCES trackable_item_event (id) ON DELETE CASCADE NOT NULL,
 user_id                  INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
 creation_time            TIMESTAMP  NOT NULL,
 last_update_time         TIMESTAMP  NOT NULL,
 confirmed                INT DEFAULT 0 NOT NULL
);
