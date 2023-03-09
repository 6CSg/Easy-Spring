package com.csg.springframework.context.support;

public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {
    private String[] configLocations;

    public ClassPathXmlApplicationContext() {}

    public ClassPathXmlApplicationContext(String configLocation) {
        this(new String[]{configLocation});
    }
    public ClassPathXmlApplicationContext(String[] configLocations) {
        this.configLocations = configLocations;
        refresh();
    }


    @Override
    public String[] getConfigLocations() {
        return configLocations;
    }
}
