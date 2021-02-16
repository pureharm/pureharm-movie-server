ALTER TABLE user_invitation
RENAME COLUMN registration TO invitation_token;

DROP INDEX registration_idx;
CREATE UNIQUE INDEX invitation_token_idx ON user_invitation (invitation_token);