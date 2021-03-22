CREATE TYPE user_role AS ENUM(
    'Newbie',
    'Member',
    'Curator',
    'SuperAdmin'
);

CREATE TABLE users (
  id uuid PRIMARY KEY NOT NULL,
  email varchar(128) NOT NULL,
  role user_role NOT NULL,
  bcrypt_password_hash bytea NOT NULL,
  password_reset_token varchar(96)
);

CREATE TABLE user_invitations (
  email varchar(128) PRIMARY KEY NOT NULL,
  role user_role NOT NULL,
  invitation_token varchar(96) NOT NULL,
  expires_at timestamptz NOT NULL
);

CREATE UNIQUE INDEX idx_user_invitations_unique_token ON user_invitations (invitation_token);

CREATE TABLE user_authentications (
  token varchar(96) PRIMARY KEY NOT NULL,
  user_id uuid NOT NULL,
  expires_at timestamptz NOT NULL,
  CONSTRAINT fk_authentications_on_users_id
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE movies (
  id uuid PRIMARY KEY NOT NULL,
  title varchar NOT NULL,
  release_date date
);