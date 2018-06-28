CREATE TABLE users (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  email varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  role varchar(255) NOT NULL,
  registration varchar(255) NOT NULL,
  passwordReset varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE authentications (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  userId int(11) NOT NULL,
  token varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE movies (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  date DATETIME NOT NULL,
  PRIMARY KEY (id)
)