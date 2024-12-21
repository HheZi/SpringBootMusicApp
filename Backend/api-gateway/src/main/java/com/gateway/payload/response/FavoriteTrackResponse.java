package com.gateway.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoriteTrackResponse {

	private Long trackId;
	
	private Boolean inFavorites;
	
}
