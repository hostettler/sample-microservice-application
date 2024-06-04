INSERT INTO users(id, user_id, first_name, last_name, gender) VALUES (1, 'john.doe@doe.com', 'John', 'Doe', 0);
INSERT INTO users(id, user_id, first_name, last_name, gender) VALUES (2, 'jane.doe@doe.com', 'Jane', 'Doe', 1);
ALTER SEQUENCE users_seq restart with 500;