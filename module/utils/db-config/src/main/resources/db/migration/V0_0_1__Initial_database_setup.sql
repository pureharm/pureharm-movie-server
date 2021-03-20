CREATE TYPE user_role AS ENUM(
    'Newbie',
    'Member',
    'Curator',
    'SuperAdmin'
);

CREATE TABLE users (
  id uuid PRIMARY KEY NOT NULL,
  email varchar(255) NOT NULL,
  password bytea NOT NULL,
  role user_role NOT NULL,
  registration_token varchar(255) NOT NULL,
  password_reset_token varchar(255)
);

CREATE TABLE user_invitations (
  email varchar(255) PRIMARY KEY NOT NULL,
  role user_role NOT NULL,
  invitation_token varchar(255) NOT NULL,
  expires_at timestamptz NOT NULL
);

CREATE UNIQUE INDEX idx_user_invitations_unique_token ON user_invitations (invitation_token);

CREATE TABLE user_authentications (
  token varchar(255) PRIMARY KEY NOT NULL,
  user_id uuid NOT NULL,
  expires_at timestamptz NOT NULL,
  CONSTRAINT fk_authentications_on_users_id
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE movies (
  id uuid PRIMARY KEY NOT NULL,
  name varchar(255) NOT NULL,
  release_date date NOT NULL
);