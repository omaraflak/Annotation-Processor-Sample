package me.aflak.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import me.aflak.annotation.Activity;

@AutoService(Processor.class)
public class ActivityProcessor extends AbstractProcessor{
    private Filer filer;
    private Messager messager;
    private Elements elements;
    private List<ClassName> classList;

    private final ClassName classContext = ClassName.get("android.content", "Context");
    private final ClassName classIntent = ClassName.get("android.content", "Intent");

    private static final String generatedPackage = "me.aflak.annotations";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();
        this.elements = processingEnvironment.getElementUtils();
        this.classList = new ArrayList<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        /**
         *      1) Getting annotated classes
         */

        for(Element element : roundEnvironment.getElementsAnnotatedWith(Activity.class)){
            if(element.getKind() != ElementKind.CLASS){
                messager.printMessage(Diagnostic.Kind.ERROR, "@Activity should be on top of classes");
                return false;
            }
            classList.add(ClassName.get(elements.getPackageOf(element).getQualifiedName().toString(),
                    element.getSimpleName().toString()));
        }

        /**
         *      2) For each annotated class, generate new static method
         */

        TypeSpec.Builder generatedClass = TypeSpec
                .classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for(ClassName className : classList){
            MethodSpec startMethod = MethodSpec
                    .methodBuilder("start"+className.simpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(classContext, "context")
                    .addStatement("$T intent = new $T(context, $L)", classIntent, classIntent, className+".class")
                    .addStatement("context.startActivity(intent)")
                    .build();

            generatedClass.addMethod(startMethod);
        }

        /**
         *      3) Write Navigator class into file
         */

        try {
            JavaFile.builder(generatedPackage, generatedClass.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Activity.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
