package org.akaza.openclinica.dao.core;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Properties;

public class CoreResources implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    public static String PROPERTIES_DIR;

    private Properties dataInfo;

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        try {
            setPROPERTIES_DIR();
            String dbName = dataInfo.getProperty("dataBase");
            SQLFactory factory = SQLFactory.getInstance();
            factory.run(dbName);
        } catch (OpenClinicaSystemException e) {
            //throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
        }
    }

    private void setPROPERTIES_DIR() {
        String resource = "properties/placeholder.properties";
        Resource scr = resourceLoader.getResource(resource);
        String absolutePath = null;
        try {
            absolutePath = scr.getFile().getAbsolutePath();
            PROPERTIES_DIR = absolutePath.replaceAll("placeholder.properties", "");
        } catch (IOException e) {
            throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
        }

    }

    public Properties getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(Properties dataInfo) {
        this.dataInfo = dataInfo;
    }

}
