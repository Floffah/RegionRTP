package dev.floffah.regionrtp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import dev.floffah.regionrtp.RegionRTP;
import java.io.*;
import java.nio.file.Path;

public class ConfigProvider {

    RegionRTP plugin;
    Config config;
    RegionsConfig regions;

    File configFile;
    File regionsFile;

    ObjectMapper objectMapper;

    public ConfigProvider(RegionRTP plugin) {
        this.plugin = plugin;

        this.objectMapper = new ObjectMapper(
            new YAMLFactory()
                .configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false)
        );

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.configFile = Path.of(
            plugin.getDataFolder().getPath(),
            "config.yml"
        ).toFile();
        this.regionsFile = Path.of(
            plugin.getDataFolder().getPath(),
            "regions.yml"
        ).toFile();

        if (!this.configFile.exists()) {
            try {
                this.writeDefaultConfig();
            } catch (IOException e) {
                plugin.handleFatalException(e);
            }
        }

        if (!this.regionsFile.exists()) {
            try {
                this.writeDefaultRegions();
            } catch (IOException e) {
                plugin.handleFatalException(e);
            }
        }

        try {
            this.readConfig();
            this.readRegions();
        } catch (IOException e) {
            plugin.handleFatalException(e);
        }
    }

    private void writeDefaultConfig() throws IOException {
        InputStream resource = this.plugin.getResource("defaultConfig.yml");

        OutputStream configOut = new FileOutputStream(this.configFile);
        configOut.write(resource.readAllBytes());

        resource.close();
        configOut.close();
    }

    private void writeDefaultRegions() throws IOException {
        InputStream resource = this.plugin.getResource("defaultRegions.yml");

        OutputStream regionsOut = new FileOutputStream(this.regionsFile);
        regionsOut.write(resource.readAllBytes());

        resource.close();
        regionsOut.close();
    }

    public void writeConfig() throws IOException {
        objectMapper.writeValue(this.configFile, this.config);
    }

    public void readConfig() throws IOException {
        this.config = objectMapper.readValue(this.configFile, Config.class);
    }

    public void writeRegions() throws IOException {
        objectMapper.writeValue(this.regionsFile, this.regions);
    }

    public void readRegions() throws IOException {
        this.regions = objectMapper.readValue(
            this.regionsFile,
            RegionsConfig.class
        );
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public Config getConfig() {
        return config;
    }

    public RegionsConfig getRegions() {
        return regions;
    }
}
