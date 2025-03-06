create table authors(
	id int auto_increment primary key,
	name varchar(65),
	description varchar(360),
	image_name uuid
);

insert into authors(name, description) values
('First', 'desc 1'), ('Second', 'desc 2'), ('Third', 'desc 3');