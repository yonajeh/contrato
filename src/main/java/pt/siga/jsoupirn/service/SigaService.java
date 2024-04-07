package pt.siga.jsoupirn.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class SigaService {
    private int SLOWNESS = 12;

    private static final String BASE_URL = "https://siga.marcacaodeatendimento.pt";
    private final Logger logger = LoggerFactory.getLogger(SigaService.class);

    @Value("${env}")
    private String env;
    @Value("${debugMode}")
    private Boolean debugMode;

    @Autowired
    ContratoIntermatoService telegram;



    public void look() throws Exception {
        if (debugMode){
            logger.info("start");
            telegram.sendBotMessage(ContratoIntermatoService.Type.RESIDENCY,"bdina");
        }
// Create a new instance of the Chrome driver
        WebDriver driver;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("lang=pt-PT");
        if ("digital".equals(env)) {
            SLOWNESS = 14;
            URL gridUrl = new URL("http://146.190.141.218:4444/wd/hub");

            driver = new RemoteWebDriver(gridUrl, options);
        } else {
            driver = new ChromeDriver(options);
        }


        // Navigate to the web page containing the button
        driver.get(BASE_URL + "/Marcacao/Entidades");

        try {
            WebElement button = driver.findElement(By.id("176"));
            button.click();


            Select selectCategoria = new Select(driver.findElement(By.id("IdCategoria")));
            selectCategoria.selectByValue("22002");

            Thread.sleep(300 * SLOWNESS);

            Select selectSubcategoria = new Select(driver.findElement(By.id("IdSubcategoria")));
            selectSubcategoria.selectByValue("30825");

            Thread.sleep(150 * SLOWNESS);

            WebElement buttonNext = driver.findElement(By.className("set-date-button"));
            buttonNext.click();

            Select selectDistrito = new Select(driver.findElement(By.id("IdDistrito")));
            List<SelectDto> districts = new java.util.ArrayList<>(selectDistrito.getOptions().stream()
                    .filter(Objects::nonNull)
                    .map(o -> new SelectDto(o.getAttribute("value"), o.getText()))
                    .filter(d -> StringUtils.hasText(d.getValue()))
                    .toList());
            Collections.shuffle(districts);

            for (SelectDto district : districts) {
                new Select(driver.findElement(By.id("IdDistrito")))
                        .selectByValue(district.getValue());
                Thread.sleep(200 * SLOWNESS);
                Select selectLocalidade = new Select(driver.findElement(By.id("IdLocalidade")));
                List<SelectDto> localities = selectLocalidade.getOptions().stream()
                        .filter(Objects::nonNull)
                        .map(o -> new SelectDto(o.getAttribute("value"), o.getText()))
                        .filter(d -> StringUtils.hasText(d.getValue()))
                        .toList();
                if (debugMode) {
                    logger.info(String.format("count of localities in [%s] is : [%d] ", district.getLabel(), localities.size()));
                }
                Select selectLocalAtendimento = new Select(driver.findElement(By.id("IdLocalAtendimento")));
                List<SelectDto> localAtendimento = selectLocalAtendimento.getOptions().stream()
                        .filter(Objects::nonNull)
                        .map(o -> new SelectDto(o.getAttribute("value"), o.getText()))
                        .filter(d -> StringUtils.hasText(d.getValue()))
                        .toList();

                Thread.sleep(50 * SLOWNESS);

                if (localAtendimento.size() > 1) {


                    localAtendimento.forEach(local -> {
                        try{
                            new Select(driver.findElement(By.id("IdLocalAtendimento")))
                                    .selectByValue(local.getValue());

                            try {
                                Thread.sleep(100 * SLOWNESS);
                            } catch (InterruptedException e) {}

                            WebElement buttonNext2 = driver.findElement(By.className("set-date-button"));
                            buttonNext2.click();
                            try {
                                lookOnPageAnBack(driver, district, local);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }catch (Exception ex){
                            new Select(driver.findElement(By.id("IdLocalidade"))).selectByValue("-1");
                            try {
                                Thread.sleep(100 * SLOWNESS);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            logger.info("mix old and new way.");
                        }


                    });
                    continue;
                }
                Thread.sleep(100 * SLOWNESS);

                try {
                    WebElement buttonNext2 = driver.findElement(By.className("set-date-button"));
                    buttonNext2.click();
                }catch (Exception e){

                    logger.error(
                            String.format("something wrong with failure in %s",district.getLabel()),
                            e
                    );
                    //new Select(driver.findElement(By.id("IdLocalidade"))).selectByValue("-1");

                    continue;
                }

                Thread.sleep(200 * SLOWNESS);

                try {
                    WebElement textAera = driver.findElement(By.cssSelector(".error-message h5"));
                    if (debugMode) {
                        String districtMessage = String.format("%s says : %s", district.getLabel(), textAera.getText());
                        logger.info(districtMessage);
                        telegram.sendBotMessage(ContratoIntermatoService.Type.RESIDENCY,districtMessage);
                    }
                } catch (Exception e) {
                    logger.warn(String.format("big warn about %s", district.getLabel()));
                    try {
                        List<WebElement> spanWithDates = driver.findElements(By.cssSelector(".schedule-list div.no_margin span"));
                        spanWithDates.forEach(el ->{
                            String news = String.format("Schedule in %s at %s", district.getLabel(), el.getText());
                            telegram.sendBotMessage(ContratoIntermatoService.Type.RESIDENCY,news);
                            logger.warn(news);
                        });
                    } catch (Exception ex) {
                        logger.error("Whaaaat ? : " + ex.getMessage());
                    }
                    //
                }
                Thread.sleep(100 * SLOWNESS);


                WebElement buttonBack = driver.findElement(By.cssSelector("#liVoltarButton a"));
                buttonBack.click();


            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        } finally {
            driver.quit();
        }
    }

    private void lookOnPageAnBack(WebDriver driver, SelectDto district, SelectDto local) throws InterruptedException {
        Thread.sleep(200 * SLOWNESS);

        if (local == null ){
            local = district;
        }
        final String localString = local.getLabel();
        try {
            WebElement textAera = driver.findElement(By.cssSelector(".error-message h5"));
            if (debugMode) {
                String districtMessage = String.format("%s - %s says : %s", district.getLabel(), localString, textAera.getText());
                logger.info(districtMessage);
                telegram.sendBotMessage(ContratoIntermatoService.Type.RESIDENCY,districtMessage);
            }
        } catch (Exception e) {
            logger.warn(String.format("big warn about %s - %s", district.getLabel(), localString));
            try {
                List<WebElement> spanWithDates = driver.findElements(By.cssSelector(".schedule-list div.no_margin span"));
                spanWithDates.forEach(el ->{
                    String news = String.format("Schedule in %s - %s at %s", district.getLabel(), localString, el.getText());
                    telegram.sendBotMessage(ContratoIntermatoService.Type.RESIDENCY,news);
                    logger.warn(news);
                });
            } catch (Exception ex) {
                logger.error("Whaaaat ? : " + ex.getMessage());
            }
            //
        }
        Thread.sleep(100 * SLOWNESS);


        WebElement buttonBack = driver.findElement(By.cssSelector("#liVoltarButton a"));
        buttonBack.click();
    }
}
