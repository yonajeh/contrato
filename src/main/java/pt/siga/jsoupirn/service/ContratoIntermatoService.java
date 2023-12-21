package pt.siga.jsoupirn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Service
public class ContratoIntermatoService extends TelegramLongPollingBot {

    Logger logger = LoggerFactory.getLogger(ContratoIntermatoService.class);


    private static final HashMap<Long, Long> USERS = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {

        sendBotMessage(update.getMessage().getChatId(), "ok tssna");
        User from = update.getMessage().getFrom();

        USERS.put(from.getId(), update.getMessage().getChatId());

    }

    @Override
    public String getBotUsername() {
        return "contrato_intermato_bot";
    }

    @Override
    public String getBotToken() {
        return "6374186824:AAHigK-6ZpqEObleeFQSVezUVTHGDfPHMA0";
    }

    public void sendBotMessage(Long chatId, String message) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(message);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendBotMessage(String msj) {
        USERS.values().forEach(user ->{
            sendBotMessage(user, msj);
        });
    }

    /**
     * Done! Congratulations on your new bot. You will find it at t.me/contrato_intermato_bot. You can now add a description, about section and profile picture for your bot, see /help for a list of commands. By the way, when you've finished creating your cool bot, ping our Bot Support if you want a better username for it. Just make sure the bot is fully operational before you do this.
     *
     * Use this token to access the HTTP API:
     * 6374186824:AAHigK-6ZpqEObleeFQSVezUVTHGDfPHMA0
     * Keep your token secure and store it safely, it can be used by anyone to control your bot.
     *
     * For a description of the Bot API, see this page: https://core.telegram.org/bots/api
     */
}
