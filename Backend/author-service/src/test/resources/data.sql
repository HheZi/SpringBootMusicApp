create table authors(
	id int auto_increment primary key,
	name varchar(65),
	description varchar(360),
	created_by int,
	image_name uuid
);

insert into authors(name, description, created_by) values
('First', 'desc 1', 1), ('Second', 'desc 2', 1 ), ('Third', 'desc 3', 1);