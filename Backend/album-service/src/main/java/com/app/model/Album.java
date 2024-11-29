package com.app.model;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.model.enums.AlbumType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

	@Id
	private Integer id;
	
	private String name;
	
	@Column("image_name")
	private UUID imageName;
	
	@Column("album_type")
	private AlbumType albumType;
	
	@Column("created_by")
	private Integer createdBy;
	
	@Column("author_id")
	private Integer authorId;
	
	@Column("release_date")
	private LocalDate releaseDate;
}
