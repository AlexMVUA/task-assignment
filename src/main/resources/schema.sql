CREATE TABLE IF NOT EXISTS devices (
    uuid         VARCHAR(36)  PRIMARY KEY,
    serialNumber VARCHAR(50),
    model        VARCHAR(20),
    phoneNumber  VARCHAR(20)
);
