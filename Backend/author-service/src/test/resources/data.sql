create table authors(
	id int auto_increment primary key,
	name varchar(65),
	image_name uuid default random_uuid()
);

insert into authors(name) values
('First'), ('Second'), ('Third');