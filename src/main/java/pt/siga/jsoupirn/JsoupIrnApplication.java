package pt.siga.jsoupirn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pt.siga.jsoupirn.service.SigaNatService;
import pt.siga.jsoupirn.service.SigaService;

@SpringBootApplication
@EnableScheduling
public class JsoupIrnApplication {
    private final Logger logger = LoggerFactory.getLogger(JsoupIrnApplication.class);

    @Autowired
    private SigaService  sigaService;
    @Autowired
    private SigaNatService sigaNatService;

    public static void main(String[] args) {

        SpringApplication.run(JsoupIrnApplication.class, args);

    }

    @Scheduled(fixedRate = 9600000)
    public void goN()  {
        try {
            sigaNatService.look();
        } catch (Exception e) {
            logger.error("somthing went not good, ", e);
        }
    }

    @Scheduled(fixedRate = 600000)
    public void goR()  {
        try {
            sigaService.look();
        } catch (Exception e) {
            logger.error("somthing went not good, ", e);
        }
    }
}
