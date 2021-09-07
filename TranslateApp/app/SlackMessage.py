
class SlackMessage:
    def __init__(self):
        self.command = ""
        self.text = ""
        self.channel = ""
        self.user = ""
        self.translated = ""

    def setCommand(self, command):
        self.command = command
        return self

    def getCommand(self):
        return self.command

    def setText(self, text):
        self.text = text
        return self

    def getText(self):
        return self.text

    def setChannel(self, channel):
        self.channel = channel
        return self

    def getChannel(self):
        return self.channel

    def setUser(self, user):
        self.user = user
        return self

    def getUser(self):
        return self.user

    def setTranslated(self, translated):
        self.translated = translated
        return self

    def getTranslated(self):
        return self.translated
