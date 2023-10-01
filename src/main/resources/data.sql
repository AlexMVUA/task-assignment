CREATE TABLE IF NOT EXISTS users (
    id           VARCHAR(36) PRIMARY KEY,
    firstName    VARCHAR(30) NOT NULL,
    lastName     VARCHAR(30) NOT NULL,
    address      VARCHAR(50) NOT NULL,
    birthday     DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS devices (
    uuid         VARCHAR(36)  PRIMARY KEY,
    serialNumber VARCHAR(50),
    model        VARCHAR(20),
    phoneNumber  VARCHAR(20),
    userId       VARCHAR(36) references users(id)
);

INSERT INTO users
    (id, firstName, lastName, address, birthday)
VALUES
    ('1059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'First', 'Detroit', '1978-12-31'),
    ('2059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Second', 'NY', '1978-12-31'),
    ('3059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Third', 'NJ', '1978-12-31'),
    ('4059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Fourth', 'Paris', '1978-12-31'),
    ('5059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Fifth', 'London', '1978-12-31'),
    ('6059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Sixth', 'Liverpool', '1978-12-31'),
    ('7059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Seventh', 'Berlin', '1978-12-31'),
    ('8059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Eighth', 'Hamburg', '1978-12-31'),
    ('9059b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Ninth', 'Stuttgart', '1978-12-31'),
    ('9159b6c1-0b85-4aad-9e3c-2afe06df58e0', 'User', 'Tenth', 'Munich', '1978-12-31');

INSERT INTO devices
    (uuid, serialNumber, model, phoneNumber, userId)
VALUES
    ('1051ad14-c482-4cc7-9fab-7d02717771b6', '100', '1A', 'Detroit', null),
    ('2328a03e-af8e-47f1-a68e-cbe43caafa61', '200', '2B', 'Detroit', '3059b6c1-0b85-4aad-9e3c-2afe06df58e0'),
    ('39e3ff63-7388-4d7e-865e-8f8d7d2e3343', '300', '3C', 'Detroit', null),
    ('49e3ff63-7388-4d7e-865e-8f8d7d2e3343', '400', '4D', 'Detroit', '3059b6c1-0b85-4aad-9e3c-2afe06df58e0'),
    ('59e3ff63-7388-4d7e-865e-8f8d7d2e3343', '500', '5E', 'Detroit', '4059b6c1-0b85-4aad-9e3c-2afe06df58e0'),
    ('69e3ff63-7388-4d7e-865e-8f8d7d2e3343', '600', '6F', 'Detroit', '4059b6c1-0b85-4aad-9e3c-2afe06df58e0'),
    ('79e3ff63-7388-4d7e-865e-8f8d7d2e3343', '700', '7G', 'Detroit', null),
    ('89e3ff63-7388-4d7e-865e-8f8d7d2e3343', '800', '8H', 'Detroit', '7059b6c1-0b85-4aad-9e3c-2afe06df58e0'),
    ('9903ff63-7388-4d7e-865e-8f8d7d2e3343', '900', '9I', 'Detroit', '7059b6c1-0b85-4aad-9e3c-2afe06df58e0'),
    ('99f3ff63-7388-4d7e-865e-8f8d7d2e3343', 'User', '10J', 'Detroit', '7059b6c1-0b85-4aad-9e3c-2afe06df58e0');