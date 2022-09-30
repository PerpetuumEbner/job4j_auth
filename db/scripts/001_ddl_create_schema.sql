create table persons
(
    id       serial primary key not null,
    username varchar(2000),
    password varchar(2000),
    creation timestamp,
    enabled  boolean
)