package org.aion4j.avm.idea;

import org.aion4j.avm.idea.misc.PluginConfig;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class PluginConfigTest {

    @Before
    public void setUp() throws Exception {

        String homeDir = System.getProperty("user.home");
        File file = new File(homeDir, PluginConfig.CONFIG_FILE);
        file.delete();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getOrUpdateVersionIfRequired() {
        String version = PluginConfig.getOrUpdateVersionIfRequired("0.2");
        assertThat(version, equalTo("0.2"));

        version = PluginConfig.getOrUpdateVersionIfRequired("0.3");
        assertThat(version, equalTo("0.3"));

    }
}