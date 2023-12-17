package pt.siga.jsoupirn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pt.siga.jsoupirn.service.ContratoIntermatoService;

@SpringBootApplication
@EnableScheduling
public class JsoupIrnApplication {

    public static void main(String[] args) {

        SpringApplication.run(JsoupIrnApplication.class, args);

    }

}
