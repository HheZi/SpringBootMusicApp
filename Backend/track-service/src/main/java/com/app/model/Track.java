package com.app.model;


import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {

	@Id
	private Long id;
	
	private String title;

	@Column("author_id")
	private Integer authorId;
	
	@Column("album_id")
	private Integer albumId;
	
	@Column("audio_name")
	private UUID audioName;

	@Column("created_by")
	private Integer createdBy;
	
	private String duration;
	
}
