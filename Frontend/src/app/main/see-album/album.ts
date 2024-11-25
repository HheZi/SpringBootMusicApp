export interface Album{
    albumType: string;
    id: number;
    name: string;
    imageUrl: string;
    numberOfTrack: number,
    releaseDate: Date | null | string
    totalDuration: string
}