package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {

         User user=new User(name,mobile);
         users.add(user);

         userPlaylistMap.put(user,new ArrayList<>());

         return user;
    }

    public Artist createArtist(String name) {

        Artist artist=new Artist(name);
        artists.add(artist);


        artistAlbumMap.put(artist,new ArrayList<>());

        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album=new Album(title);

        albums.add(album);

        Artist artist=null;
        for(Artist artist1:artists){
            if(artist1.getName().equals(artistName)){
               artist=artist1;
                break;
            }
        }

        if(artist==null){
           artist= createArtist(artistName);
        }

        albumSongMap.put(album,new ArrayList<>());

        artistAlbumMap.get(artist).add(album);

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {

        Album album1=null;
        for(Album album:albums){
            if(album.getTitle().equals(albumName)){
              album1=album;
               break;
            }
        }
        if(album1==null){
            throw new Exception("album does not exist");
        }

        Song song=new Song(title,length);

        albumSongMap.get(album1).add(song);

        songs.add(song);

        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

          Playlist playlist=new Playlist(title);

          playlists.add(playlist);

          List<Song>allsong=new ArrayList<>();
          for(Song song:songs){
              if(song.getLength()==length){
                  allsong.add(song);
              }
          }

        playlistSongMap.put(playlist,allsong);
          User user1=null;
          for(User user:users){
              if(user.getMobile().equals(mobile)){
                  user1=user;
              }
          }
          if(user1==null){
              throw new Exception("user does not Exist");
          }

        userPlaylistMap.get(user1).add(playlist);
        creatorPlaylistMap.put(user1,playlist);

          List<User>adduser=new ArrayList<>();
          adduser.add(user1);
        playlistListenerMap.put(playlist,adduser);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        Playlist playlist=new Playlist(title);
        playlists.add(playlist);

        List<Song>allsong=new ArrayList<>();

        for(String str:songTitles){
            for(Song song:songs){
                if(song.getTitle().equals(str)){
                    allsong.add(song);
                }
            }
        }

        playlistSongMap.put(playlist,allsong);

        User user1=null;
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                user1=user;
            }
        }
        if(user1==null){
            throw new Exception("user does not Exist");
        }

        userPlaylistMap.get(user1).add(playlist);
        creatorPlaylistMap.put(user1,playlist);

        List<User>adduser=new ArrayList<>();
        adduser.add(user1);
        playlistListenerMap.put(playlist,adduser);

        return playlist;
     }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        Playlist playlist=null;
        for(Playlist play:playlists){
            if(play.getTitle().equals(playlistTitle)){
                playlist=play;
            }
        }

        if(playlist==null){
            throw new Exception("playlist doesn't exist");
        }

        User user=null;
        for(User usr:users){
            if(usr.getMobile().equals(mobile)){
                user=usr;
            }
        }

        if(user==null){
            throw new Exception("user doesn't exist");
        }

        if(creatorPlaylistMap.containsKey(user)){
           return playlist;
        }
        if(playlistListenerMap.get(playlist).contains(user)){
         return playlist;
        }

        List<User>res=playlistListenerMap.get(playlist);
        res.add(user);
        playlistListenerMap.put(playlist,res);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {

        Song song=null;
        for(Song sng:songs){
            if(sng.getTitle().equals(songTitle)){
                song=sng;
                break;
            }
        }

        if(song==null){
            throw new Exception("song doesn't exist");
        }

        User user=null;
        for(User usr:users){
            if(usr.getMobile().equals(mobile)){
                user=usr;
                break;
            }
        }

        if(user==null){
            throw new Exception("user doesn't exist");
        }

        if(songLikeMap.get(song).contains(user)){
            return song;
        }

        song.setLikes(song.getLikes()+1);
        songLikeMap.get(song).add(user);

        for(Album album: albumSongMap.keySet()){
            if( albumSongMap.get(album).contains(song)){
                for(Artist artist:artistAlbumMap.keySet()){
                    if(artistAlbumMap.get(artist).contains(album)){
                        artist.setLikes(artist.getLikes()+1);
                        break;
                    }
                }
                break;
            }
        }
        return song;
    }


    public String mostPopularArtist() {

        String ans="";
        int maxi=-1;

        for(Artist artist:artists){
            if(artist.getLikes()>maxi){
                ans=artist.getName();
                maxi= artist.getLikes();

            }
        }

        return ans;
    }
    
    public String mostPopularSong() {
        String ans="";
        int maxi=-1;

        for(Song song:songs){
            if(song.getLikes()>maxi){
                maxi= song.getLikes();
                ans=song.getTitle();
            }
        }

        return ans;
    }
}
