DROP TABLE IF EXISTS users, items, bookings, comments, requests;


CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );


CREATE TABLE IF NOT EXISTS ITEMS
(
    ITEM_ID      INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME         VARCHAR(500) not null,
    DESCRIPTION  VARCHAR(500),
    IS_AVAILABLE BOOLEAN           not null,
    OWNER_ID     INTEGER not null,
    REQUEST_ID   INTEGER,
    constraint ITEMS_PK
        primary key (ITEM_ID),
    constraint ITEMS_USERS_ID_FK
    foreign key (OWNER_ID) references USERS
);


CREATE TABLE IF NOT EXISTS BOOKINGS (
    ID         INTEGER auto_increment,
    START_DATE TIMESTAMP WITHOUT TIME ZONE,
    END_DATE   TIMESTAMP WITHOUT TIME ZONE,
    ITEM_ID    INTEGER not null,
    BOOKER_ID  INTEGER not null,
    STATUS     VARCHAR(50),
    constraint BOOKINGS_PK
    primary key (ID, BOOKER_ID),
    constraint BOOKINGS_ITEMS_ITEM_ID_FK
        foreign key (ITEM_ID) references ITEMS,
    constraint BOOKINGS_USERS_ID_FK
    foreign key (BOOKER_ID) references USERS
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    COMMENT_ID INTEGER auto_increment,
    TEXT       VARCHAR(600) not null,
    ITEM       INTEGER           not null,
    AUTHOR     INTEGER           not null,
    CREATED    TIMESTAMP         not null,
    constraint COMMENTS_PK
        primary key (COMMENT_ID),
    constraint COMMENTS_ITEMS_ITEM_ID_FK
        foreign key (ITEM) references ITEMS,
    constraint COMMENTS_USERS_ID_FK
        foreign key (AUTHOR) references USERS
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID   INTEGER auto_increment,
    REQUESTER_ID INTEGER                not null,
    DESCRIPTION  CHARACTER VARYING(500) not null,
    CREATED    TIMESTAMP         not null,
    constraint REQUESTS_PK
        primary key (ID),
    constraint REQUESTS_USERS_ID_FK
        foreign key (REQUESTER_ID) references USERS
);


