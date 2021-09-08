package com.tokia.tranbot.BotService;

import com.tokia.tranbot.base.HThread;
import com.tokia.tranbot.config.SlackConfig;
import lombok.AllArgsConstructor;
import lombok.val;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SlackBotServiceImp implements BotService {

    private final SlackConfig slackConfig;

    @Override
    public boolean handle(String request) {
        List<String> list = Arrays.asList(request.split("&"));
        Map<String, String> map = list.stream()
                .collect(Collectors.toMap(str->getKeyRequest(str),str->getValueRequest(str) ));
        if(!isValidRequest(map)){
            return false;
        }
        startToTranslate(map);
        return true;
    }

    private String getValueRequest(String str) {
        String[] arrStr = str.split("=");
        return arrStr[1];
    }

    private String getKeyRequest(String str) {
        String[] arrStr = str.split("=");
        return arrStr[0];
    }

    private boolean isValidRequest(Map<String, String> map) {
        return map.containsKey("command")
                && map.containsKey("text")
                && map.containsKey("token")
                && map.containsKey("user_id")
                && map.containsKey("channel_id");
    }

    private void startToTranslate(Map<String, String> map) {
        HThread hThread = new HThread(
                (arg) -> translateOnSlack(map)
        );
        val t = new Thread(hThread);
        t.start();
    }

    private Object translateOnSlack(Map<String, String> map) {
        String uft8Text = map.get("text");
        String originText = UFT8ToChar(uft8Text);
        map.put("originText",originText);
        String translatedText = translateText(originText);
        returnResultTranslate(map,translatedText);
        return "";
    }

    private String UFT8ToChar(String uft8Text)  {
        try {
            return URLDecoder.decode(uft8Text,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return  "";
        }
    }

    private String translateText(String text){
        String rs = "";
        val jsRequest = new JSONObject();
        jsRequest.put("text",text);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsRequest.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        rs = restTemplate.exchange(slackConfig.getTranslateApi(), HttpMethod.POST, entity, String.class).getBody();
        rs = rs.replace("\"","");
        return org.apache.commons.text.StringEscapeUtils.unescapeJava(rs);
    }

    private void returnResultTranslate(Map<String, String> map, String translatedText) {
        String channel = map.get("channel_id");
        String userId = map.get("user_id");
        String originText  = map.get("originText");
        JSONObject jsonTranslated = new JSONObject();
        jsonTranslated.put("channel",channel);
        jsonTranslated.put("user",userId);
        jsonTranslated.put("text",translatedText + "\n\n*Original*:\n     " + originText);
        postUserToSlack(jsonTranslated);
    }

    private void postToSlack(JSONObject jsonTranslated,String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON.toString());
        httpHeaders.add(HttpHeaders.AUTHORIZATION,"Bearer "+ slackConfig.getSlackBotToken());
        HttpEntity<String> entity = new HttpEntity<>(jsonTranslated.toString(), httpHeaders);
        String rs = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        System.out.println("Translated: "+ rs);
    }

    private void postOnlyUserToSlack(JSONObject jsonTranslated) {
        String url = "https://slack.com/api/chat.postEphemeral";
        postToSlack(jsonTranslated,url);
    }

    private void postUserToSlack(JSONObject jsonTranslated) {
        String url = "https://slack.com/api/chat.postMessage";
        postToSlack(jsonTranslated,url);
    }
}
