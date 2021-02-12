ALTER TABLE users
DROP COLUMN registration;

CREATE TABLE user_invitation (
  email varchar(255) NOT NULL,
  role varchar(255) NOT NULL,
  registration varchar(255) NOT NULL UNIQUE,
  PRIMARY KEY (email)
);

CREATE UNIQUE INDEX registration_idx ON user_invitation (registration);
