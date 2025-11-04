sudo su postgres
psql
create user url with password 'url';
create database url;
\c url
grant all on schema public to url;