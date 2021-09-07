import urllib
import boto3
import unicodedata
import os
import json


Access_key_ID = os.environ['Access_key_ID']
SECRET_KEY = os.environ['SECRET_KEY']


def lambda_handler(event, context):
    params = urllib.parse(event['body'])
    text = params['text'][0]
    translatedText = doTranslate(text)
    return {
        'statusCode': 200,
        'body':  json.dumps(translatedText)
    }


def doTranslate(message):
    if is_ja(message):
        SourceCode = 'ja'
        TargetCode = 'en'
    else:
        SourceCode = 'en'
        TargetCode = 'ja'
    translate = boto3.client(
        'translate',
        region_name="us-east-1",
        aws_access_key_id=Access_key_ID,
        aws_secret_access_key=SECRET_KEY
    )
    translateResult = translate.translate_text(
        Text=message,
        SourceLanguageCode=SourceCode,
        TargetLanguageCode=TargetCode
    )
    rs = translateResult['TranslatedText']
    return rs


def is_ja(string):
    for char in string:
        name = unicodedata.name(char)
        if "CJK UNIFIED" in name or "HIRAGANA" in name or "KATAKANA" in name:
            return True
    return False
