package com.app.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.model.enums.PlaylistType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {

	@Id
	private Integer id;
	
	private String name;
	
	@Column("image_name")
	private String imageName;
	
	@Column("playlist_type")
	private PlaylistType playlistType;
	
	@Column("created_by")
	private Integer createdBy;
	
}
