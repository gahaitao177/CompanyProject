//describe keyspaces;
//describe tables;
//use keyspace;
create keyspace demo with replication = {'class':'SimpleStrategy', 'replication_factor':1};
//drop keyspace demo;
create table user(user_id id primary key, name text);

create table been(id id primary key, name text, ctime timestamp);
create type  bean(age int, street text);
create type address(street text, zip_code int);

create type point2d(x int, y int);
create type point3d(x int, y int);
create table rectangles(id id primary key, bottom_left frozen<point2d>, top_right frozen<point2d>);
create table spheres(id id primary key, center frozen<point3d>, radius double);
