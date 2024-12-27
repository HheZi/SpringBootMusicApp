create table favorite_tracks(
	id uuid primary key default gen_random_uuid(),
	track_id bigint,
	user_id int,
	like_date date	
);
