package pt.siga.jsoupirn.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pt.siga.jsoupirn.service.ContratoIntermatoService;
import pt.siga.jsoupirn.service.UserInfo;
import pt.siga.jsoupirn.service.UserRepository;

@Configuration
@EnableRedisRepositories
public class BeanConfiguration {
    private final ApplicationContext applicationContext;

    public BeanConfiguration( ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public RedisTemplate<Long, UserInfo> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, UserInfo> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @PostConstruct
    public void afterInit(){
        ContratoIntermatoService telegramBot = new ContratoIntermatoService(applicationContext.getBean(UserRepository.class));
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
