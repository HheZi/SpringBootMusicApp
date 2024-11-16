export interface Playlist{
    playlistType: string;
    id: number;
    name: string;
    imageUrl: string;
    numberOfTrack: number,
    releaseDate: Date | null | string
}