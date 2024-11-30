export interface Album{
    albumType: string;
    id: number;
    name: string;
    imageUrl: string;
    numberOfTracks: number,
    releaseDate: Date | null | string
    totalDuration: string
    authorName: string;
    authorImageUrl: string;
    authorId: number;
}