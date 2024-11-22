package com.app.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("playlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Playlist {

	@Id
	private Integer id;
	
	private String name;
	
	@Column("created_by")
	private Integer createdBy;
	
	@Column("image_name")
	private UUID imageName;
	
	private String description;
	
	
}
