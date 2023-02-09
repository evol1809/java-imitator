package imitator.common.config.repository;

public class RepositoryConfig {

    public RepositoryConfig() {
    }

    public RepositoryConfig(FileRepositoryConfig file) {
        this.file = file;
    }

    private FileRepositoryConfig file = new FileRepositoryConfig();

    public FileRepositoryConfig getFile() {
        return file;
    }

    public void setFile(FileRepositoryConfig file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "file=" + file +
                '}';
    }
}
