package uz.pdp.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import uz.pdp.db.DB;
import uz.pdp.entity.Comment;
import uz.pdp.entity.Post;
import uz.pdp.entity.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        String userId = dataUSerId.split("/")[1];
        SendMessage sendMessage=new SendMessage(tgUser.getChatId(),"posts:");
        sendMessage.replyMarkup(generatePostsForUserButtons(Integer.parseInt(userId)));
        telegramBot.execute(sendMessage);
        tgUser.setState(TgState.SELECT_POST);
    }
    public static void acceptPostIdAndAskComment(TgUser tgUser, String dataPostId) {
        if (dataPostId.equals("orqaga")){
            acceptStartAndAskUser(tgUser);
            return;
        }
        String postId = dataPostId.split("/")[1];
        SendMessage sendMessage=new SendMessage(tgUser.getChatId(),"comments:");
        sendMessage.replyMarkup(generateCommentsForPostButtons(Integer.parseInt(postId)));
        telegramBot.execute(sendMessage);
        tgUser.setState(TgState.SELECT_COMMENT);
    }
    private static InlineKeyboardMarkup generateUsersButtons() {
        List<User> userList = DB.loadUser();
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        for (User user : userList) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(user.getName()).callbackData(user.getId().toString()),
                    new InlineKeyboardButton("Posts").callbackData("userId/"+user.getId().toString())
            );
        }
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardMarkup generateCommentsForPostButtons(Integer postId){
        List<Comment> comments = DB.loadComment().stream().filter(comment -> comment.getPostId().equals(postId)).toList();

        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        for (Comment comment : comments) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(comment.getName()).callbackData(comment.getId().toString())
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
}
