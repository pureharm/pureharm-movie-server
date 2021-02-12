CREATE TABLE users (
  id serial NOT NULL,
  email varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  role varchar(255) NOT NULL,
  registration varchar(255) NOT NULL,
  passwordReset varchar(255),
  PRIMARY KEY (id)
);

CREATE TABLE authentications (
  id serial NOT NULL,
  userId integer NOT NULL,
  token varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE movies (
  id serial NOT NULL,
  name varchar(255) NOT NULL,
  date timestamp NOT NULL,
  PRIMARY KEY (id)
)