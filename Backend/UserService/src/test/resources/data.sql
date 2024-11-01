create table users(
	id int auto_increment primary key,
	username varchar(55),
	email varchar(45),
	password varchar(240)
);

insert into users(username,email, password) 
values ('test', 'test@gmail.com', '$2a$10$xU9DniH5EUSiyHi4bRTh8OqD47sRfMs9hjUIdiglDN0cBpLe.UXN.'),
('test2', 'user@gmail.com', '$2a$10$xU9DniH5EUSiyHi4bRTh8OqD47sRfMs9hjUIdiglDN0cBpLe.UXN.');