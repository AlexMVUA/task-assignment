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
