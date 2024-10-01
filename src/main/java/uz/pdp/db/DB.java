package uz.pdp.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import uz.pdp.bot.TgUser;
import uz.pdp.entity.Comment;
import uz.pdp.entity.Post;
import uz.pdp.entity.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public interface DB {
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

}
