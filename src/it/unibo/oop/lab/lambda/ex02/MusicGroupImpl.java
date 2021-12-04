package it.unibo.oop.lab.lambda.ex02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;
import javafx.util.Pair;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(s -> s.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
    	final List<String> albumNames = new ArrayList<>();
    	this.albums.forEach((n, y) -> albumNames.add(n));
    	return albumNames.stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
    	final List<String> albumsInYear = new ArrayList<>();
    	this.albums.forEach((n, y) -> {
    		if (y.equals(year)) {
    			albumsInYear.add(n);
    		}
    	});
        return albumsInYear.stream();
    }

    @Override
    public int countSongs(final String albumName) {
    	final Set<Song> copy = new HashSet<>(this.songs);
    	copy.removeIf(s -> !s.albumName.isPresent());
    	return (int)copy.stream().filter(s -> s.albumName.get().equals(albumName)).count();
    }

    @Override
    public int countSongsInNoAlbum() {
    	final Set<Song> copy = new HashSet<>(this.songs);
    	copy.removeIf(s -> s.albumName.isPresent());
    	return (int)copy.stream().count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
    	final Set<Song> copy = new HashSet<>(this.songs);
    	copy.removeIf(s -> !s.albumName.isPresent());
    	return copy.stream().filter(s -> s.getAlbumName().get().equals(albumName)).mapToDouble(s -> s.duration).average();
    }

    @Override
    public Optional<String> longestSong() {
    	OptionalDouble max = this.songs.stream().mapToDouble(s -> s.getDuration()).max();
    	return this.songs.stream().filter(s -> Double.compare(s.getDuration(), max.orElse(0)) == 0).map(s -> s.getSongName()).findFirst();
    }

    @Override
    public Optional<String> longestAlbum() {
    	final List<Pair<String, Double>> longest = new ArrayList<>();
    	longest.add(new Pair<>(null, 0.0));
    	this.albums.forEach((album, year) -> {
    		final Set<Song> copy = new HashSet<>(this.songs);
    		copy.removeIf(s -> !s.getAlbumName().isPresent());
    		double sum = copy.stream().filter(s -> s.getAlbumName().get().equals(album)).mapToDouble(s -> s.getDuration()).sum();
    		if (sum > longest.get(0).getValue()) {
    			longest.set(0, new Pair<>(album, sum));
    		}
    	});
        return Optional.of(longest.get(0).getKey());
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
