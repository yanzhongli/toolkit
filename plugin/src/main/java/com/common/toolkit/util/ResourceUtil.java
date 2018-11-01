package com.common.toolkit.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ResourceUtil {

  private final static Logger logger = LoggerFactory.getLogger(ResourceUtil.class);
  private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver() {
    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
      Set<Resource> result = new LinkedHashSet<Resource>(16);
      ClassLoader cl = getClassLoader();
      Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path)
          : ClassLoader.getSystemResources(path));
      while (resourceUrls.hasMoreElements()) {
        URL url = resourceUrls.nextElement();
        if ("jar".equals(url.getProtocol())) {
          continue;
        }
        result.add(convertClassLoaderURL(url));
      }
      return result;
    }

    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
      String rootDirPath = determineRootDir(locationPattern);
      String subPattern = locationPattern.substring(rootDirPath.length());
      Set<Resource> rootDirResources = doFindAllClassPathResources("");
      Set<Resource> result = new LinkedHashSet<Resource>(16);
      for (Resource rootDirResource : rootDirResources) {
        rootDirResource = resolveRootDirResource(rootDirResource);
        if (isJarResource(rootDirResource)) {
          result.addAll(doFindPathMatchingJarResources(rootDirResource, rootDirResource.getURL(),
              subPattern));
        } else {
          result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
        }
      }
      return result.toArray(new Resource[result.size()]);
    }
  };

  public static Resource[] getResources(String pattern) {
    Map<String, Resource> loaded = new HashMap<String, Resource>();
    try {
      Resource[] resources = resourcePatternResolver.getResources(pattern);

      for (Resource resource : resources) {
        String fileName = resource.getFilename();
        if (loaded.containsKey(fileName)) {
          logger.error("资源文件：{}已经读取{}，不再读取：{}", fileName, loaded.get(fileName), resource);
          continue;
        }
        logger.debug("资源文件：{}读取{}", fileName, resource);
        loaded.put(fileName, resource);
      }
    } catch (IOException e) {
      logger.error("读取资源文件[{}]异常,应用继续往下运行.", e);
    }
    Collection<Resource> result = loaded.values();
    return result.toArray(new Resource[result.size()]);
  }
}
