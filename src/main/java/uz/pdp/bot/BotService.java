package uz.pdp.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.SneakyThrows;
import uz.pdp.db.DB;
import uz.pdp.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BotService {
    public static TelegramBot telegramBot = new TelegramBot("7393380287:AAHYmQ0Sa9vScE-Jz36X_wlwH9wEJfZ-1T8");


    public static TgUser getOrCreateTgUser(Long chatId) {
        for (TgUser tgUser : DB.TG_USERS) {
            if (tgUser.getChatId().equals(chatId)){
                return tgUser;
            }
        }
        TgUser tgUser = new TgUser(chatId, TgState.START);
        DB.TG_USERS.add(tgUser);
        return tgUser;
    }

    public static void acceptStartAndAskUser(TgUser tgUser) {
        SendMessage sendMessage=new SendMessage(tgUser.getChatId(),"users:");
        sendMessage.replyMarkup(generateUsersButtons());
        telegramBot.execute(sendMessage);
        tgUser.setState(TgState.SELECT_USER);
    }
    public static void acceptUserIdAndAskPost(TgUser tgUser, String dataUSerId) {
        if (dataUSerId.equals("orqaga")){
            acceptStartAndAskUser(tgUser);
            return;
        }
        String info = dataUSerId.split("/")[0];
        String userId = dataUSerId.split("/")[1];
        if (info.equals("userIdForPost")){
            SendMessage sendMessage=new SendMessage(tgUser.getChatId(),"posts:");
            sendMessage.replyMarkup(generatePostsForUserButtons(Integer.parseInt(userId)));
            telegramBot.execute(sendMessage);
            tgUser.setState(TgState.SELECT_POST);
        } else if (info.equals("userIdForAlbum")){
            SendMessage sendMessage=new SendMessage(tgUser.getChatId(),"albums:");
            sendMessage.replyMarkup(generateAlbumsForUserButtons(Integer.parseInt(userId)));
            telegramBot.execute(sendMessage);
            tgUser.setState(TgState.SELECT_ALBUM);
        } else if (info.equals("userIdForTodo")){
            SendMessage sendMessage=new SendMessage(tgUser.getChatId(),"todos:");
            sendMessage.replyMarkup(generateTodosForUserButtons(Integer.parseInt(userId)));
            telegramBot.execute(sendMessage);
            tgUser.setState(TgState.SELECT_TODO);
        }

    }

    private static InlineKeyboardMarkup generateTodosForUserButtons(Integer userId) {
        List<Todo> todos = DB.loadTodo().stream().filter(todo -> todo.getUserId().equals(userId)).toList();
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        for (Todo todo : todos) {
            inlineKeyboardMarkup.addRow(
              new InlineKeyboardButton((todo.getCompleted()?"✅ ":"❌ ")+todo.getTitle()).callbackData(todo.getId().toString())
            );
        }
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("back").callbackData("orqaga"));
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardMarkup generateAlbumsForUserButtons(Integer userId) {
        List<Album> albums = DB.loadAlbum().stream().filter(post->post.getUserId().equals(userId)).toList();
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        for (Album album : albums) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(album.getTitle()).callbackData(album.getId().toString()),
                    new InlineKeyboardButton("photos").callbackData("albumId/"+album.getId().toString())
            );
        }
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("back").callbackData("orqaga"));
        return inlineKeyboardMarkup;
    }
    private static InlineKeyboardMarkup generatePostsForUserButtons(Integer userId){
        List<Post> posts = DB.loadPost().stream().filter(post -> post.getUserId().equals(userId)).toList();
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        for (Post post : posts) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(post.getTitle()).callbackData(post.getId().toString()),
                    new InlineKeyboardButton("Comments").callbackData("postId/"+post.getId().toString())
            );
        }
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("back").callbackData("orqaga"));
        return inlineKeyboardMarkup;
    }
    public static void acceptAlbumAndSendingPhotos(TgUser tgUser, String dataAlbumId) {
        if (dataAlbumId.equals("orqaga")){
            acceptStartAndAskUser(tgUser);
            return;
        }
        String albumId = dataAlbumId.split("/")[1];
        sendingAllPhotosForAlbum(tgUser,albumId);
    }



    public static void acceptPostIdAndAskComment(TgUser tgUser, String dataPostId) {
        if (dataPostId.equals("orqaga")){
            acceptStartAndAskUser(tgUser);
            return;
        }
        String postId = dataPostId.split("/")[1];
        sendingAllCommentsForPost(tgUser,postId);
    }


    private static void sendingAllCommentsForPost(TgUser tgUser, String postId) {
        Optional<Post> optionalPost = DB.loadPost().stream().filter(post1 -> post1.getId().equals(Integer.parseInt(postId))).findFirst();
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            SendMessage sendPostTitle=new SendMessage(tgUser.getChatId(),"post title: "+post.getTitle()+"\n\n");
            telegramBot.execute(sendPostTitle);
            List<Comment> comments = DB.loadComment().stream().filter(comment -> comment.getPostId().equals(Integer.parseInt(postId))).toList();
            for (Comment comment : comments) {
                String str ="-owner comment: "+ comment.getEmail() + "\n\n" +
                        "-comment's title: \n"+comment.getName() + "\n\n" +
                        "-comment's body: \n"+comment.getBody();
                SendMessage sendMessage=new SendMessage(tgUser.getChatId(), str);
                telegramBot.execute(sendMessage);
            }
            SendMessage sendBackButton=new SendMessage(tgUser.getChatId(),"to back");
            sendBackButton.replyMarkup(generateBackButton());
            telegramBot.execute(sendBackButton);
            tgUser.setState(TgState.SELECT_COMMENT);
        }
    }
    @SneakyThrows
    private static void sendingAllPhotosForAlbum(TgUser tgUser, String albumId) {
        ExecutorService executorService= Executors.newFixedThreadPool(10);
        List<Photo> photos=DB.loadPhotos().stream().filter(photo -> photo.getAlbumId().equals(Integer.parseInt(albumId))).toList();
        AtomicInteger i= new AtomicInteger();
        for (Photo photo : photos) {
            executorService.execute(()->{
                byte[] photoByte=DB.loadPhotosWithUrl(photo.getUrl());
                SendPhoto sendPhoto = new SendPhoto(tgUser.getChatId(), photoByte);
                sendPhoto.caption(i.incrementAndGet() +". "+ photo.getTitle());
                SendResponse res = telegramBot.execute(sendPhoto);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        SendMessage sendBackButton=new SendMessage(tgUser.getChatId(),"to back");
        sendBackButton.replyMarkup(generateBackButton());
        telegramBot.execute(sendBackButton);
        tgUser.setState(TgState.SELECT_PHOTO);
    }

    private static InlineKeyboardMarkup generateBackButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("back").callbackData("orqaga"));
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardMarkup generateUsersButtons() {
        List<User> userList = DB.loadUser();
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        for (User user : userList) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(user.getName()).callbackData(user.getId().toString()),
                    new InlineKeyboardButton("Todos").callbackData("userIdForTodo/"+user.getId().toString()),
                    new InlineKeyboardButton("Posts").callbackData("userIdForPost/"+user.getId().toString()),
                    new InlineKeyboardButton("Albums").callbackData("userIdForAlbum/"+user.getId().toString())
            );
        }
        return inlineKeyboardMarkup;
    }



}
