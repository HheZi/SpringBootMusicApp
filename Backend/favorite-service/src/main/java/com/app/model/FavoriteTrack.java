package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("favorite_tracks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteTrack {
	
	@Id
	private UUID id;
		
	@Column("track_id")
	private Long trackId;
	
	@Column("user_id")
	private Integer userId;
	
	@Column("like_date")
	private LocalDate likeDate;
	
	public FavoriteTrack(Long trackId, Integer userId) {
		this.trackId = trackId;
		this.userId = userId;
	}
}
