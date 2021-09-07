package com.tokia.tranbot.controller;


import com.tokia.tranbot.BotService.BotService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/translate")
public class TranslateController {

    @Autowired
    public BotService slackBotService;

    @PostMapping("/slack")
    public ResponseEntity<?> handleSlackRequest(@RequestBody String request){
        val isTranslating = slackBotService.handle(request);
        return ResponseEntity.ok().body(isTranslating?"translating...":"Try again!");
    }
}
