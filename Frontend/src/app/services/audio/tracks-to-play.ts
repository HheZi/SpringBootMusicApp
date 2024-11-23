import { Track } from "../../main/track-list/track";

export interface TracksToPlay{
    tracks: Track[],
    indexOfCurrentTrack: number
}