package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {
	
	@Id
	private Integer id;
	
	private String name;
	
	@Column("image_name")
	private UUID imageName;
	
	@Column("created_by")
	private Integer createdBy;
	
	private String description;
	
}
