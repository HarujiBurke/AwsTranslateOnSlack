package com.tokia.tranbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@PropertySource(value = {"classpath:application.properties"})
public class SlackConfig {
    @Value("${EXPECTED_TOKEN}")
    private String exportedToken;
    @Value("${SLACK_BOT_TOKEN}")
    private String slackBotToken;
    @Value("${TRANSLATE_API}")
    private String translateApi;

}
