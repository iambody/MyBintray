package com.rabbit.router.compiler;

import com.chenenyu.router.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * {@link Route} annotation processor.
 * <p>
 * Created by Cheney on 2016/12/20.
 */
@SupportedAnnotationTypes(Consts.ROUTE_ANNOTATION_TYPE)
@SupportedOptions(Consts.OPTION_MODULE_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RouteProcessor extends AbstractProcessor {
    private Elements elementUtils = null;
    private Filer filer = null;
    private Messager messager = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    /**
     * This method will be called some times.
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (elements == null || elements.isEmpty()) {
            return true;
        }
        // 合法的TypeElement集合
        Set<TypeElement> typeElements = new HashSet<>();
        for (Element element : elements) {
            if (validateElement(element)) {
                typeElements.add((TypeElement) element);
            }
        }
        String moduleName = processingEnv.getOptions().get(Consts.OPTION_MODULE_NAME);
        if (moduleName != null) {
            generateRouteTable(moduleName, typeElements);
            generateInterceptorTable(moduleName, typeElements);
        } else {
            error(null, "No option `%s` passed to annotation processor.", Consts.OPTION_MODULE_NAME);
        }
        return true;
    }

    /**
     * Verify the annotated class.
     */
    private boolean validateElement(Element typeElement) {
        if (!processingEnv.getTypeUtils().isSubtype(typeElement.asType(),
                processingEnv.getElementUtils().getTypeElement(Consts.ACTIVITY_FULL_NAME).asType())) {
            error(typeElement, "%s is not a subclass of `%s`.",
                    typeElement.getSimpleName().toString(), Consts.ACTIVITY_FULL_NAME);
            return false;
        }
        Set<Modifier> modifiers = typeElement.getModifiers();
        // non-public class.
        if (!modifiers.contains(Modifier.PUBLIC)) {
            error(typeElement, "The class %s is not public.", ((TypeElement) typeElement).getQualifiedName());
            return false;
        }
        // abstract class.
        if (modifiers.contains(Modifier.ABSTRACT)) {
            error(typeElement, "The class %s is abstract. You can't annotate abstract classes with @%s.",
                    ((TypeElement) typeElement).getQualifiedName(), Route.class.getSimpleName());
            return false;
        }
        return true;
    }

    private void error(Element element, String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
    }

    /**
     * RouteTable.
     */
    private void generateRouteTable(String moduleName, Set<TypeElement> elements) {
        // Map<String, Class<? extends Activity>> map
        TypeElement activityType = elementUtils.getTypeElement(Consts.ACTIVITY_FULL_NAME);
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(activityType))));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "map").build();

        MethodSpec.Builder handleActivityTable = MethodSpec.methodBuilder(Consts.ROUTE_TABLE_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec);
        for (TypeElement element : elements) {
            Route route = element.getAnnotation(Route.class);
            String[] paths = route.value();
            for (String path : paths) {
                handleActivityTable.addStatement("map.put($S, $T.class)", path, ClassName.get(element));
            }
        }

        TypeElement interfaceType = elementUtils.getTypeElement(Consts.ROUTE_TABLE_FULL_NAME);
        TypeSpec type = TypeSpec.classBuilder(capitalize(moduleName) + Consts.ROUTE_TABLE)
                .addSuperinterface(ClassName.get(interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(handleActivityTable.build())
                .addJavadoc("Generated by Router. Do not edit it!\n")
                .build();
        try {
            JavaFile.builder(Consts.PACKAGE_NAME, type).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * InterceptorTable.
     */
    private void generateInterceptorTable(String moduleName, Set<TypeElement> elements) {
        // Map<Class<? extends Activity>, String[]> map
        TypeElement activityType = elementUtils.getTypeElement(Consts.ACTIVITY_FULL_NAME);
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(activityType))),
                TypeName.get(String[].class));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "map").build();
        MethodSpec.Builder handleInterceptorTable = MethodSpec.methodBuilder(Consts.INTERCEPTOR_TABLE_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(mapParameterSpec);
        for (TypeElement element : elements) {
            Route route = element.getAnnotation(Route.class);
            String[] interceptors = route.interceptors();
            if (interceptors.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (String interceptor : interceptors) {
                    sb.append("\"").append(interceptor).append("\",");
                }
                handleInterceptorTable.addStatement("map.put($T.class, new String[]{$L})",
                        ClassName.get(element), sb.substring(0, sb.lastIndexOf(",")));
            } else if (interceptors.length == 1) {
                handleInterceptorTable.addStatement("map.put($T.class, new String[]{$S})",
                        ClassName.get(element), interceptors[0]);
            }
        }
        TypeSpec type = TypeSpec.classBuilder(capitalize(moduleName) + Consts.INTERCEPTOR_TABLE)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(handleInterceptorTable.build())
                .addJavadoc("Generated by Router. Do not edit it!\n")
                .build();
        try {
            JavaFile.builder(Consts.PACKAGE_NAME, type).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String capitalize(CharSequence self) {
        return self.length() == 0 ? "" :
                "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }
}
