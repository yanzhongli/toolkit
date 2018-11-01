package com.common.toolkit.classcheck;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import java.net.URL;
import java.util.Map.Entry;

/**
 * @author ewen
 */
public class ClassGraphUtil {

  /**
   * 查找classpath下重复的.
   *
   * key-className,URL-class url
   */
  public static ListMultimap<String, URL> listDuplicateClass() {
    ListMultimap<String, URL> multimap = ArrayListMultimap.create();
    for (Entry<String, ResourceList> dup :
        new ClassGraph().scan()
            .getAllResources()
            .classFilesOnly()
            .findDuplicatePaths()) {
      String key = dup.getKey();
      for (Resource res : dup.getValue()) {
        multimap.put(key, res.getURL());
      }
    }
    return multimap;
  }

}
