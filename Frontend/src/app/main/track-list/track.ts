export interface Track {
   id: number,
   title: string,
   albumName: string,
   albumId: number,
   audioUrl: string ,
   authorId: number,
   author: string,
   imageUrl: string,
   isNowPlaying: boolean 
   duration: string;
   isEditing: boolean;
   inFavorites: boolean;
}
