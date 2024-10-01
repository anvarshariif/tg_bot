package uz.pdp.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import uz.pdp.db.DB;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static uz.pdp.bot.BotService.*;

public class BotController {
    public static ExecutorService executorService= Executors.newFixedThreadPool(10);

    public static void start() {
        telegramBot.setUpdatesListener(updates->{
            for (Update update : updates) {
                executorService.execute(()->{
                    handelUpdate(update);
                });
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }

    private static void handelUpdate(Update update) {
        if (update.message()!=null){
            Message message = update.message();
            TgUser tgUser=getOrCreateTgUser(message.chat().id());
            if (message.text()!=null){
                if (message.text().equals("/start")){
                    acceptStartAndAskUser(tgUser);
                }
            }



        } else if (update.callbackQuery()!=null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            String data = callbackQuery.data();
            TgUser tgUser = getOrCreateTgUser(callbackQuery.from().id());
            if (tgUser.getState().equals(TgState.SELECT_USER)){
                acceptUserIdAndAskPost(tgUser,data);
            } else if (tgUser.getState().equals(TgState.SELECT_POST)){
                acceptPostIdAndAskComment(tgUser,data);
            } else if (tgUser.getState().equals(TgState.SELECT_COMMENT)){
                acceptStartAndAskUser(tgUser);
            }
        }
    }
}
