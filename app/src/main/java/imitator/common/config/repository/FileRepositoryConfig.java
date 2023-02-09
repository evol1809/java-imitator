package imitator.common.config.repository;

public class FileRepositoryConfig {

    public FileRepositoryConfig() {
    }

    public FileRepositoryConfig(String filePath) {
        this.filePath = filePath;
    }

    private String filePath = "";

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "FileRepositoryConfig{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
