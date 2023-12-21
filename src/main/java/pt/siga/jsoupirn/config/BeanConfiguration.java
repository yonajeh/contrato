package pt.siga.jsoupirn.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pt.siga.jsoupirn.service.ContratoIntermatoService;

@Configuration
public class BeanConfiguration {




    @PostConstruct
    public void afterInit(){
        ContratoIntermatoService telegramBot = new ContratoIntermatoService();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
