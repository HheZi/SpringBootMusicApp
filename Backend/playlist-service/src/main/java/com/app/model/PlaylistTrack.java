package com.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("playlists_tracks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaylistTrack {
	
	@Id
	private Integer id;

	@Column("playlist_id")
	private Integer playlistId;

	@Column("track_id")
	private Long trackId;
}
