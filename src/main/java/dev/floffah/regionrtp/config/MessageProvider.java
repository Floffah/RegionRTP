package dev.floffah.regionrtp.config;

import dev.floffah.regionrtp.RegionRTP;
import java.io.*;
import java.nio.file.Path;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MessageProvider {

    RegionRTP plugin;

    MessagesConfig messages;

    File messagesFile;

    public MessageProvider(RegionRTP plugin) {
        this.plugin = plugin;

        this.messagesFile = Path.of(
            plugin.getDataFolder().getPath(),
            "messages.yml"
        ).toFile();

        if (!this.messagesFile.exists()) {
            try {
                this.writeDefaultMessages();
            } catch (IOException e) {
                plugin.handleFatalException(e);
            }
        }

        try {
            this.readMessages();
        } catch (IOException e) {
            plugin.handleFatalException(e);
        }
    }

    private void writeDefaultMessages() throws IOException {
        InputStream resource = this.plugin.getResource("defaultMessages.yml");

        OutputStream messagesOut = new FileOutputStream(this.messagesFile);
        messagesOut.write(resource.readAllBytes());

        resource.close();
        messagesOut.close();
    }

    public void writeMessages() throws IOException {
        this.plugin.getConfigProvider()
            .getObjectMapper()
            .writeValue(this.messagesFile, this.messages);
    }

    public void readMessages() throws IOException {
        this.messages = this.plugin.getConfigProvider()
            .getObjectMapper()
            .readValue(this.messagesFile, MessagesConfig.class);
    }

    public Component formatAdminError(String error) {
        return MiniMessage.miniMessage().deserialize("<red>" + error);
    }

    public Component getMessage(String key, TagResolver... patterns) {
        return MiniMessage.miniMessage()
            .deserialize(
                this.messages.prefix + this.messages.messages.get(key),
                patterns
            );
    }
}
