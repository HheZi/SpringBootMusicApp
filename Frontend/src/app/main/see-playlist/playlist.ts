export interface Playlist{
    id: number,
    name: string,
    imageUrl: string,
    description: string,
    trackIds: number[],
    numberOfTracks: number,
    totalDuration: string
}