package com.common.toolkit.test.tool;

import com.common.toolkit.classcheck.ClassGraphUtil;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.FieldInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeSignature;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ewen
 */
public class ClassGraphUTest {

  public static void main(String[] args) {
    //扫描指定包
    try (ScanResult scanResult =                // Assign scanResult in try-with-resources
        new ClassGraph()                    // Create a new ClassGraph instance
            .verbose()                      // If you want to enable logging to stderr
            .enableAllInfo()                // Scan classes, methods, fields, annotations
            .whitelistPackages("com.common.toolkit")   // Scan com.xyz and subpackages
            .scan()) {                      // Perform the scan and return a ScanResult
      // Use the ScanResult within the try block, e.g.
//      获取指定类信息
      ClassInfo form = scanResult.getClassInfo("com.common.toolkit.Bootstrap");
      // ...
      System.out.println(form);

      //获取类方法、成员变量
      if (form != null) {
        MethodInfoList formMethods = form.getMethodInfo();
        for (MethodInfo mi : formMethods) {
          String methodName = mi.getName();
          MethodParameterInfo[] mpi = mi.getParameterInfo();
          for (int i = 0; i < mpi.length; i++) {
            String parameterName = mpi[i].getName();
            TypeSignature parameterType =
                mpi[i].getTypeSignatureOrTypeDescriptor();
            System.out.println(
                "Method:" + methodName + "(" + parameterType.toString() + " " + parameterName
                    + ")");
          }
        }
        FieldInfoList formFields = form.getFieldInfo();
        for (FieldInfo fi : formFields) {
          String fieldName = fi.getName();
          TypeSignature fieldType = fi.getTypeSignatureOrTypeDescriptor();
          System.out.println("Field:" + fieldType + " " + fieldName);
        }
        AnnotationInfoList formAnnotations = form.getAnnotationInfo();
        for (AnnotationInfo ai : formAnnotations) {
          String annotationName = ai.getName();
          List<AnnotationParameterValue> annotationParamVals =
              ai.getParameterValues();
          System.out.println("Anno:" + annotationName + " " + annotationParamVals);
        }
      }

      //获取带有指定注解的类信息
      ClassInfoList routeClassInfoList = scanResult
          .getClassesWithAnnotation(SpringBootApplication.class.getName());
      for (ClassInfo routeClassInfo : routeClassInfoList) {
        // Get the Route annotation on the class
        AnnotationInfo annotationInfo = routeClassInfo
            .getAnnotationInfo(SpringBootApplication.class.getName());
        AnnotationParameterValueList annotationParamVals = annotationInfo.getParameterValues();

        // The Route annotation has a parameter named "path"
        Object sc = annotationParamVals.get("scanBasePackages");

        // Alternatively, you can load and instantiate the annotation, so that the annotation
        // methods can be called directly to get the annotation parameter values (this sets up
        // an InvocationHandler to emulate the Route annotation instance, since annotations
        // can't be instantiated directly without also loading the annotated class).
        SpringBootApplication springBootApplication = (SpringBootApplication) annotationInfo
            .loadClassAndInstantiate();
        String[] scanBasePackages = springBootApplication.scanBasePackages();
        System.out.println(Arrays.toString(scanBasePackages));
        // ...
      }

      //获取实现了某接口的类名
      ClassInfoList widgetClasses = scanResult
          .getClassesImplementing("com.common.toolkit.logger.DynamicLog");
      List<String> widgetClassNames = widgetClasses.getNames();
      System.out.println(widgetClasses);

      //获取某类的子类
      ClassInfoList controlClasses = scanResult
          .getSubclasses("com.common.toolkit.logger.LoggerSupport");
      List<Class<?>> controlClassRefs = controlClasses.loadClasses();
      System.out.println(controlClassRefs);

      ClassInfoList checked = scanResult
          .getClassesWithAnnotation(SpringBootApplication.class.getName());
      //并集
      ClassInfoList c1 = controlClasses.union(checked);
      System.out.println(c1);
      //交集
      ClassInfoList c2 = controlClasses.intersect(checked);
      System.out.println(c2);
      //排除checked
      ClassInfoList c3 = controlClasses.exclude(checked);
      System.out.println(c3);
      //filter
      ClassInfoList checkedBoxes = scanResult
          .getSubclasses("com.common.toolkit.logger.LoggerSupport")
          .filter(classInfo -> classInfo.hasAnnotation(SpringBootApplication.class.getName()));
      System.out.println(checkedBoxes);

      //获取所有class->类是否为接口或抽象类,是否有某注解，是否有某方法
      ClassInfoList filtered = scanResult.getAllClasses()
          .filter(classInfo ->
              (classInfo.isInterface() || classInfo.isAbstract())
                  && classInfo.hasAnnotation(SpringBootApplication.class.getName())
                  && classInfo.hasMethod("main"));
      System.out.println(filtered);

      //获取直接子类
      ClassInfoList directBoxes = scanResult
          .getSubclasses("com.common.toolkit.logger.LoggerSupport").directOnly();
      System.out.println(directBoxes);

      //获取所有被注解的类，注解必须在定义的包类
      ClassInfoList metaAnnotations = scanResult.getAllAnnotations()
          .filter(ci -> {
            //获取添加了此class注解的类信息
            ClassInfoList classesWithAnnotation = ci.getClassesWithAnnotation();
            //获取包含此注解的另一个注解类
            ClassInfoList annotations = classesWithAnnotation.getAnnotations();
            return !annotations.isEmpty();
          });
      System.out.println(metaAnnotations);

//      try {

//        画类结构图，画出来有点尴尬
//        scanResult.getAllClasses().generateGraphVizDotFile(new File("D:\\代码\\toolkit-parent\\test\\target\\GraphViz.dot"));
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
    }

//    dup();

    System.out.println(ClassGraphUtil.listDuplicateClass());

  }

  //自定义扫描路径url
  public static void scan(URL[] urls) {
    try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages("com.xyz")
        .overrideClassLoaders(new URLClassLoader(urls))
        .scan()) {
      // ...
    }
  }

  //自定义扫描文件路径
  public static void scanFile() {
    Map<String, String> pathToFileContent = new HashMap<>();
    try (ScanResult scanResult = new ClassGraph().whitelistPaths("META-INF/config").scan()) {
      scanResult.getResourcesWithExtension("xml")
          .forEachByteArray((Resource res, byte[] fileContent) -> {
            pathToFileContent.put(
                res.getPath(), new String(fileContent, StandardCharsets.UTF_8));
          });
    }
  }

}
