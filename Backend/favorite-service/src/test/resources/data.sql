create table favorite_tracks(
	id uuid default random_uuid() primary key,
	track_id bigint,
	user_id int,
	like_date date default current_date()
);

insert into favorite_tracks(track_id, user_id) values
(1,1),
(1,2),
(2,1);