package com.app.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Track {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	@Column(name = "playlist_id")
	private Long playlistId;
	
	@Column(name = "created_at")
	@CreatedDate
	private Instant createdAt;
	
	@Column(name = "audio_name")
	private String audioName;
	
	@Column(name = "update_at")
	@LastModifiedDate
	private Instant updateAt;
}
