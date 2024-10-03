package uz.pdp.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import uz.pdp.bot.TgUser;
import uz.pdp.entity.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface DB {
    ExecutorService executorService=Executors.newFixedThreadPool(10);
    List<TgUser> TG_USERS=new ArrayList<>();

    @SneakyThrows
    static List<User> loadUser(){
        HttpClient httpClient= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson=new Gson();
        return gson.fromJson(body,new TypeToken<List<User>>(){}.getType());
    }
    @SneakyThrows
    static List<Post> loadPost(){
        HttpClient httpClient= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson=new Gson();
        return gson.fromJson(body,new TypeToken<List<Post>>(){}.getType());
    }
    @SneakyThrows
    static List<Album> loadAlbum(){
        HttpClient httpClient= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/albums"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson=new Gson();
        return gson.fromJson(body,new TypeToken<List<Album>>(){}.getType());
    }
    @SneakyThrows
    static List<Todo> loadTodo(){
        HttpClient httpClient= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/todos"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson=new Gson();
        return gson.fromJson(body,new TypeToken<List<Todo>>(){}.getType());
    }

    @SneakyThrows
    static List<Comment> loadComment(){
        HttpClient httpClient= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/comments"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson=new Gson();
        return gson.fromJson(body,new TypeToken<List<Comment>>(){}.getType());
    }

    @SneakyThrows
    static List<Photo> loadPhotos() {
        HttpClient httpClient= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/photos"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson=new Gson();
        return gson.fromJson(body,new TypeToken<List<Photo>>(){}.getType());
    }

    @SneakyThrows
    static byte[] loadPhotosWithUrl(String url) {
        HttpClient httpClient= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(request,HttpResponse.BodyHandlers.ofByteArray());
        return response.body();
    }
}
