package com.csg.springframework.context.support;

public class FilePathXmlApplicationContext extends AbstractXmlApplicationContext{
    private String[] configLocations;

    public FilePathXmlApplicationContext() {}

    public FilePathXmlApplicationContext(String[] configLocations) {
        this.configLocations = configLocations;
        refresh();
    }
    public FilePathXmlApplicationContext(String configLocation) {
        this(new String[]{configLocation});
    }
    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }
}
