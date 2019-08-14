create table if not exists "user"
(
  user_id serial not null
    constraint user_pk
    primary key,
  name varchar(20) not null,
  password varchar(20) not null
);

alter table "user" owner to postgres;

create unique index if not exists user_name_uindex
  on "user" (name);

create unique index if not exists user_user_id_uindex
  on "user" (user_id);

create table if not exists role
(
  role_id serial not null
    constraint role_pk
    primary key,
  name varchar(20) not null
);

alter table role owner to postgres;

create unique index if not exists role_name_uindex
  on role (name);

create unique index if not exists role_role_id_uindex
  on role (role_id);

create table if not exists user_role
(
  user_id integer not null
    constraint user_role_user_user_id_fk
    references "user",
  role_id integer not null
    constraint user_role_role_role_id_fk
    references role,
  user_role_id serial not null
    constraint user_role_pk
    primary key
);

alter table user_role owner to postgres;

create unique index if not exists user_role_user_role_id_uindex
  on user_role (user_role_id);

create table if not exists product
(
  product_id serial not null
    constraint product_pk
    primary key,
  name varchar(40) not null,
  organization varchar(40) not null,
  volume integer not null
);

alter table product owner to postgres;

create unique index if not exists product_product_id_uindex
  on product (product_id);

create unique index if not exists product_name_uindex
  on product (name);

